package com.example.future.qqslidingmenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class ExampleActivity extends AppCompatActivity {
    private static final String TAG = "ExampleActivity";
    private QQSlidMenu slidMenu;
    private ListView menuList;
    private ListView mainList;
    private ImageView headIcon;
    private LineareLayoutForMainLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setLinstener();

    }

    /**
     * 设置监视器
     */
    private void setLinstener() {
        slidMenu.setmOnDragStateChangeListener(new QQSlidMenu.onDragStateChangeListener() {
            @Override
            public void onOpen() {
                Log.i(TAG, "onOpen: 菜单打开了" + slidMenu.isMenuLayoutOpen);
            }

            @Override
            public void onClose() {
                Log.i(TAG, "onClose: 菜单关闭了" + slidMenu.isMenuLayoutOpen);
                ViewPropertyAnimator.animate(headIcon)
                        .translationXBy(15)
                        .setInterpolator(new CycleInterpolator(4)).
                        setDuration(300).start();
            }

            @Override
            public void onChanging(float fraction) {
                Log.i(TAG, "onChanging: 菜单打开百分比:" + fraction);
                ViewHelper.setAlpha(headIcon, new FloatEvaluator().evaluate(fraction, 1f, 0f));

            }
        });
    }

    /**
     * 填充2个ListView的列表数据
     */
    private void initData() {
        menuList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view1 = convertView == null ? super.getView(position, convertView, parent) : convertView;
                ((TextView) view1).setTextColor(Color.WHITE);
                ((TextView) view1).setText(Constant.sCheeseStrings[position]);
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ExampleActivity.this, "点击了 " + Constant.sCheeseStrings[position], Toast.LENGTH_SHORT).show();
                    }
                });

                return view1;
            }
        });
        mainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                ((TextView) view).setText(Constant.NAMES[position]);
                // 先缩小
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                // 在放大
                ViewPropertyAnimator.animate(view).scaleX(1f).setDuration(250).start();
                ViewPropertyAnimator.animate(view).scaleY(1f).setDuration(250).start();
                // 设置点击事件  测试 帧布局 点击事件的传递
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ExampleActivity.this, "点击位置" + Constant.NAMES[position], Toast.LENGTH_SHORT).show();
                    }
                });
                return view;
            }
        });
        ll.setQQSlidMenu(slidMenu);


    }

    /**
     * 初始化布局
     */
    private void initView() {
        setContentView(R.layout.activity_main);
        slidMenu = (QQSlidMenu) findViewById(R.id.sildmenu);
        menuList = (ListView) findViewById(R.id.menu_listview);
        mainList = (ListView) findViewById(R.id.main_listview);
        headIcon = (ImageView) findViewById(R.id.iv_head);
        ll = (LineareLayoutForMainLayout) findViewById(R.id.ll);

    }
}
