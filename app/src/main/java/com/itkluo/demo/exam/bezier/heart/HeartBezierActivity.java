package com.itkluo.demo.exam.bezier.heart;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;

/**
 * 四段三阶贝塞尔曲线组合 实现让一个圆渐变成为心形
 * Created by luobingyong on 2020/8/8.
 */
public class HeartBezierActivity extends BaseActivity {
    private RadioGroup mRadioGroup;
    private Bezier3 mBezier;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.layout_bezier);
        RelativeLayout container = findViewById(R.id.container);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mBezier = new Bezier3(mActivity);
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
                    break;
                case R.id.rb2:
                    break;

                default:
                    break;

            }
        }
    }
}
