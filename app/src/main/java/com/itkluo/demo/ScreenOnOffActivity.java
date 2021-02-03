package com.itkluo.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.itkluo.demo.utils.ScreenListener;

public class ScreenOnOffActivity extends AppCompatActivity {
    private static final String TAG = "ScreenOnOffActivity";
    private ScreenListener screenListener ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_on_off);
        screenListener = new ScreenListener( ScreenOnOffActivity.this ) ;
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.e(TAG, "屏幕打开了");
            }

            @Override
            public void onScreenOff() {
                Log.e(TAG, "屏幕关闭了");

            }

            @Override
            public void onUserPresent() {
                Log.e(TAG, "解锁了");
            }
        });


    }
}
