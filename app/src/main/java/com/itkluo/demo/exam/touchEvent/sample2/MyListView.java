package com.itkluo.demo.exam.touchEvent.sample2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author luobingyong
 * @date 2020/12/11
 */
public class MyListView extends ListView {
    private static final String TAG = "MyListView";
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG , "===MyListView MotionEvent.ACTION_DOWN===");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG , "===MyListView MotionEvent.ACTION_MOVE===");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG , "===MyListView MotionEvent.ACTION_UP===");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG , "===MyListView MotionEvent.ACTION_CANCEL===");
                break;
        }
        return super.dispatchTouchEvent(ev);
        // 1
//        return false;
    }
}
