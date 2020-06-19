package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by luobingyong on 2018/8/20.
 */
public class AppInfoActivity extends AppCompatActivity {
    private TextView content;
    private TextView content2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        content = findViewById(R.id.content);
        content2 = findViewById(R.id.content2);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.getPkgUsageStats();
            }
        });


        TextView textView = (TextView) findViewById(R.id.text);
        String systemInfoStr = SystemInfoTools.getBuildInfo();
        systemInfoStr += "-------------------------------------\r\n";
        systemInfoStr += SystemInfoTools.getSystemPropertyInfo();
        textView.setText(systemInfoStr);



    }
}
