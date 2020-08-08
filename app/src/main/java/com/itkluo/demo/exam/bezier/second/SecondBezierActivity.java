package com.itkluo.demo.exam.bezier.second;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;

/**
 * 二阶贝塞尔曲线
 * Created by luobingyong on 2020/8/8.
 */
public class SecondBezierActivity extends BaseActivity {


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.layout_bezier);
        RelativeLayout container = findViewById(R.id.container);
        Bezier bezier = new Bezier(mActivity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(bezier, layoutParams);
    }
}
