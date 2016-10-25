package com.example.future.qqslidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Author: Future <br>
 * QQ: <br>
 * Description:<br>
 * date: 12:29  12:29.
 */

public class LineareLayoutForMainLayout extends LinearLayout {
    private static final String TAG = "LineareLayoutForMainLay";
    private QQSlidMenu qqSlidMenu;

    public LineareLayoutForMainLayout(Context context) {
        this(context, null);
    }

    public LineareLayoutForMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineareLayoutForMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setQQSlidMenu(QQSlidMenu qqSlidMenu) {
        this.qqSlidMenu = qqSlidMenu;
    }

    // 如果菜单布局打开了 则拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (qqSlidMenu != null && qqSlidMenu.isMenuLayoutOpen) {
            Log.i(TAG, "onInterceptTouchEvent: ");
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果 菜单布局 打开了
        if (qqSlidMenu != null && qqSlidMenu.isMenuLayoutOpen) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                qqSlidMenu.menuLayoutClose();
                Log.i(TAG, "onTouchEvent: 执行关闭菜单");
                return true;
            }
        }

        return super.onTouchEvent(event);
    }
}
