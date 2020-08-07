package com.itkluo.demo.exam.scrollviewswipe.out;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 先尝试外部拦截
 * 现象：ScrollView会自动拦截事件下发，导致下层收不到事件.
 * 外部拦截；重写onInterceptTouchEvent，直接return false.
 */
public class OutsideScrollView extends ScrollView {

    public OutsideScrollView(Context context) {
        this(context, null);
    }

    public OutsideScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutsideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        return false;//为什么这里不拦截，他就直接可以支持两边的滑动了？

        //唯一的解释就是，它接到了事件的回传,在onTouchEvent中同样也处理了滑动事件。
        //证实我没有猜错

//        return true;//如果是return true呢？所有事件全都拦截，我自己处理,直接走 我自己的onTouchEvent,这个时候，子View收不到任何事件，包括down

//        return super.onInterceptTouchEvent(ev);//默认的处理方式，拦截"纵"向的滑动move事件,由于一旦拦截，就是一个系列的事件，所以后续的up也不会有了
        // ，但是不包括down，down还是会传下去的，检查了源码，发现没有拦截down
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);//由于真正的滑动效果，都是在onTouchEvent中处理的，所以，只要让本ScrollView的onTouchEvent能收到事件消费。
        //scrollView默认消费Event，所以上层的onTouchEvent是不会执行的
    }
}
