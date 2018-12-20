package com.itkluo.demo.exam.touchEvent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by luobingyong on 2018/12/20.
 */
public class MyViewGroup01 extends LinearLayout {
    public MyViewGroup01(Context context) {
        super(context);
    }

    public MyViewGroup01(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup01(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        System.out.println("MyViewGroup01 dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        System.out.println("MyViewGroup01 onInterceptTouchEvent");
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("MyViewGroup01 onTouchEvent");
        return super.onTouchEvent(event);
    }

}
