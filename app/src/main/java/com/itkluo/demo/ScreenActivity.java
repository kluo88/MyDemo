package com.itkluo.demo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView content;
    private TextView content2;
    private TextView text_screen;

    private OrientationEventListener mOrientationListener; // 屏幕方向改变监听器
    private int startRotation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        setupTextView();
        setupScreen();

        startListener();
    }

    /**
     * 开启监听器
     */
    private final void startListener() {

        mOrientationListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (startRotation == -2) {//初始化角度
                    startRotation = rotation;
                }
                //变化角度大于30时，开启自动旋转，并关闭监听
                int r = Math.abs(startRotation - rotation);
                r = r > 180 ? 360 - r : r;
                if (r > 30) {
                    //开启自动旋转，响应屏幕旋转事件
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                    this.disable();
                }
            }
        };
    }


    private void setupScreen() {
        text_screen = (TextView) findViewById(R.id.content2);
        //获取当前的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        text_screen.setText("onCreate" + time);
        //给TextView设置点击事件清除文字
        text_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_screen.setText("重新来！");

            }
        });
    }

    //改变横竖屏切换的方法
    public void changeScreen(View view) {

        //设置完之后变成强制设定为横屏或纵屏，如同AndroidManifest.xml中设置了android:screenOrientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //（如果竖屏）设置屏幕为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置为置屏幕为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //2秒后开启屏幕旋转监听，用来开启自动旋转，响应屏幕旋转事件
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRotation = -2;
                mOrientationListener.enable();
            }
        }, 2000);


    }


    private void setupTextView() {
        content = findViewById(R.id.content);
        content2 = findViewById(R.id.content2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        text_screen.append("\n onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        text_screen.append("\n onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        text_screen.append("\n onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        text_screen.append("\n onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        text_screen.append("\n onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        text_screen.append("\n onDestroy");
    }


}
