package com.itkluo.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";

    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;
    private TextView content;
    private TextView content2;
    private TextView text_screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.test_rx_permission_in_activity)
    void testRxPermissionInActivity() {
        Intent intent = new Intent(MainActivity2.this, RxPermissionTestActivity.class);
        startActivity(intent);
    }


}
