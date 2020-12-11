package com.itkluo.demo.exam.touchEvent.sample2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author luobingyong
 * @date 2020/12/11
 */
public class MyViewPager extends ViewPager {
    private static final String TAG = "MyViewPager";
    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG , "===MyViewPager MotionEvent.ACTION_DOWN===");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG , "===MyViewPager MotionEvent.ACTION_MOVE===");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG , "===MyViewPager MotionEvent.ACTION_UP===");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG , "===MyViewPager MotionEvent.ACTION_CANCEL===");
                break;
        }
        return super.dispatchTouchEvent(ev);
        // 1
//        return false;

    }
}
