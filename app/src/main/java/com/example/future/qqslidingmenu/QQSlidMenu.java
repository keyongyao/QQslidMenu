package com.example.future.qqslidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Author: <br>
 * QQ: <br>
 * Description:侧边的菜单<br>
 * date: on 9:36.
 */

public class QQSlidMenu extends FrameLayout {
    private static final String TAG = "QQSlidMenu";
    // 菜单布局是否被打开
    public boolean isMenuLayoutOpen;
    private ViewDragHelper mDragHelper;
    // 左侧菜单的布局
    private View menuLayout;
    // 主体的布局
    private View mainLayout;
    // 可以拖拽的区域
    private float dragRange;
    private FloatEvaluator mFloatEvaluator;
    private IntEvaluator mIntEvaluator;
    // 菜单布局 打开监听器
    private onDragStateChangeListener mOnDragStateChangeListener;
    /**
     * View的拖拽帮手回调
     */
    ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {
        // 当捕捉到的view 移动的时候执行
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 如果移动的是menuLayout 则 需要固定位置
            if (changedView == menuLayout) {
                menuLayout.layout(0, 0, menuLayout.getMeasuredWidth(), menuLayout.getMeasuredHeight());
                int newLeft = mainLayout.getLeft() + dx;
                if (newLeft < 0) {
                    newLeft = 0;//限制mainView的左边
                }
                if (newLeft > dragRange) newLeft = (int) dragRange;//限制mainView的右边
                mainLayout.layout(newLeft, mainLayout.getTop() + dy, newLeft + mainLayout.getMeasuredWidth(), mainLayout.getBottom() + dy);
            }


            // 计算滑动百分比
            float fraction = (mainLayout.getLeft() / dragRange);
            Log.i(TAG, "onViewPositionChanged: " + fraction);
            // 设置 监听借口 方法
            if (mOnDragStateChangeListener != null) {
                if (fraction == 0 && isMenuLayoutOpen) {
                    isMenuLayoutOpen = false;
                    mOnDragStateChangeListener.onClose();
                } else if (fraction > 0.999999f && !isMenuLayoutOpen) {
                    isMenuLayoutOpen = true;
                    mOnDragStateChangeListener.onOpen();
                } else {
                    mOnDragStateChangeListener.onChanging(fraction);
                }
            }

            // 执行 大小 缩放到0.8
            ViewHelper.setScaleX(changedView, mFloatEvaluator.evaluate(fraction, 1f, 0.8f));
            ViewHelper.setScaleY(changedView, mFloatEvaluator.evaluate(fraction, 1f, 0.8f));
            // menuLayout 执行位移  大小 和 透明色变
            ViewHelper.setScaleX(menuLayout, mFloatEvaluator.evaluate(fraction, 0.5f, 1f));
            ViewHelper.setScaleY(menuLayout, mFloatEvaluator.evaluate(fraction, 0.5f, 1f));
            ViewHelper.setTranslationX(menuLayout, mIntEvaluator.evaluate(fraction, (int) (-dragRange / 2), 0));
            ViewHelper.setAlpha(menuLayout, mFloatEvaluator.evaluate(fraction, 0.3f, 1.f));
            // 给背景添加黑幕 模式为
            getBackground().setColorFilter((int) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
        }

        // 用户手放开 执行
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainLayout.getLeft() > dragRange * 0.5) {
                menuLayoutOpen();
            } else {
                menuLayoutClose();
            }
            // 照顾 用户飞快的向右滑动速度
            if (xvel > 150 && !isMenuLayoutOpen) {
                menuLayoutOpen();
            }
            // 照顾 用户飞快的向左滑动速度

            if (xvel < -150 && isMenuLayoutOpen) {
                menuLayoutClose();
            }

        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;
         * 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        // 可以限制 捕捉到View 可以 偏离左侧的距离，默认是 0
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainLayout) {
                if (left < 0) left = 0;
                if (left > dragRange) left = (int) dragRange;
            }

            return left;
        }

        // 用户想要拖拽的 子View 或者 ID  当返回true时执行方法:onViewCaptured(View, int)
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 想要捕捉 主布局
            return mainLayout == child || child == menuLayout;
        }
    };

    public QQSlidMenu(Context context) {
        this(context, null);
    }

    public QQSlidMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQSlidMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 第一：初始化一些对象，使得ViewDragHelper 关联当前的 ViewGroup
     */
    private void init() {
        mDragHelper = ViewDragHelper.create(this, dragCallback);
        mFloatEvaluator = new FloatEvaluator();
        mIntEvaluator = new IntEvaluator();
    }

    /**
     * 第二：找出 子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        menuLayout = getChildAt(0);
        mainLayout = getChildAt(1);
    }

    // 第三：在 onMeasure()后执行，可以初始化自己的 View 的 宽和高
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        dragRange = getMeasuredWidth() * 0.6f;
    }

    // 第四 ： 由  ViewDragHelper 判断 是否要拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);

    }

    // 第五：由 ViewDragHelper 消费触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    // mainLayout 平滑关闭
    public void menuLayoutClose() {
        mDragHelper.smoothSlideViewTo(mainLayout, 0, 0);
        ViewCompat.postInvalidateOnAnimation(QQSlidMenu.this);
    }

    // mainLayout 平滑移动
    public void menuLayoutOpen() {
        mDragHelper.smoothSlideViewTo(mainLayout, (int) dragRange, 0);
        ViewCompat.postInvalidateOnAnimation(QQSlidMenu.this);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(QQSlidMenu.this);
        }
    }

    /**
     * 设置 菜单布局监视器
     *
     * @param mOnDragStateChangeListener
     */
    public void setmOnDragStateChangeListener(onDragStateChangeListener mOnDragStateChangeListener) {
        this.mOnDragStateChangeListener = mOnDragStateChangeListener;
    }

    /**
     * 菜单布局监视器
     */
    public interface onDragStateChangeListener {
        void onOpen();

        void onClose();

        void onChanging(float fraction);
    }


}
