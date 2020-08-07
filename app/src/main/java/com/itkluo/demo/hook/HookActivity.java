package com.itkluo.demo.hook;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;

/**
 * 我们的目的：
 * 下面的代码里，有一个view被设置了点击事件
 * 我要求，
 * 在点击事件执行之前，或者之后，加入我自己的代码逻辑，不准变动原先点击事件的代码
 *
 * @author luobingyong
 * @date 2020/8/4
 */
public class HookActivity extends BaseActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_hook);
        View v = findViewById(R.id.tv);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "别点啦，再点我咬你了...", Toast.LENGTH_SHORT).show();
            }
        });

        HookSetOnClickListenerHelper.hook(this, v);//这个hook的作用，是 用我们自己创建的点击事件代理对象，替换掉之前的点击事件。
        //所以，这个hook动作，必须在setOnClickListener之后，不然就不起作用

    }
}
