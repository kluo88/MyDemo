package com.itkluo.demo.exam.bezier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;
import com.itkluo.demo.exam.bezier.heart.HeartBezierActivity;
import com.itkluo.demo.exam.bezier.second.SecondBezierActivity;
import com.itkluo.demo.exam.bezier.third.ThirdBezierActivity;
import com.itkluo.demo.exam.scrollviewswipe.in.InsideInterceptActivity;
import com.itkluo.demo.exam.scrollviewswipe.out.OutsideInterceptActivity;

/**
 * 贝塞尔曲线练习
 *
 * @author luobingyong
 * @date 2020/8/6
 */
public class BezierMain extends BaseActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.bezier_demo_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierMain.this.startActivity(new Intent(mActivity, SecondBezierActivity.class));
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierMain.this.startActivity(new Intent(mActivity, ThirdBezierActivity.class));
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BezierMain.this.startActivity(new Intent(mActivity, HeartBezierActivity.class));
            }
        });

    }
}
