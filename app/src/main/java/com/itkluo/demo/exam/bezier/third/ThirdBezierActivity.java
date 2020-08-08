package com.itkluo.demo.exam.bezier.third;

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
public class ThirdBezierActivity extends BaseActivity {
    private RadioGroup mRadioGroup;
    private Bezier2 mBezier;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.layout_bezier);
        RelativeLayout container = findViewById(R.id.container);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mRadioGroup.setVisibility(View.VISIBLE);
        mBezier = new Bezier2(mActivity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(mBezier, layoutParams);
        mRadioGroup.setOnCheckedChangeListener(new CheckListener());
    }

    class CheckListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb1:
                    mBezier.setMode(true);
                    break;
                case R.id.rb2:
                    mBezier.setMode(false);
                    break;

                default:
                    break;

            }
        }
    }
}
