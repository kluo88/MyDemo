package com.itkluo.demo.exam.scrollviewswipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;
import com.itkluo.demo.exam.scrollviewswipe.in.InsideInterceptActivity;
import com.itkluo.demo.exam.scrollviewswipe.out.OutsideInterceptActivity;

/**
 * 外部拦截解决方案
 *
 * @author luobingyong
 * @date 2020/8/6
 */
public class ScrollViewInterceptMain extends BaseActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.intercept_demo_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollViewInterceptMain.this.startActivity(new Intent(mActivity, OutsideInterceptActivity.class));
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollViewInterceptMain.this.startActivity(new Intent(mActivity, InsideInterceptActivity.class));
            }
        });

    }
}
