package com.itkluo.demo.exam.scrollviewswipe.in;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class InsideScrollView extends ScrollView {
    public InsideScrollView(Context context) {
        this(context, null);
    }

    public InsideScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //如果我不允许外部拦截我呢？
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        // 不允许外部拦截我的事件，这个东西是如何发挥作用的，要查看源码解决

        //其实这里也要分情况的，如果我自己已经不能再滑动了，那就让外界拦截吧
//        如何判定一个ScrollView划到了顶端，或者底端
        //我还以为系统有这种API，结果没有，需要自己监听。
        //那就不花这个时间了。留点时间看源码吧.
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
}
