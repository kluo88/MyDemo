package com.itkluo.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isNeedReconnect = true;
    private NetworkChangeReceiver networkChangeReceiver;

    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;
    private TextView content;
    private TextView content2;
    private TextView text_screen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        registerNetworkBroadcast();
//        initReceiver();
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        // Restore saved state.
        if (savedInstanceState != null) {
//            editText1.setTag(R.id.tag_first,savedInstanceState.getString(TEXT_ONE));
        }

        setupTextView();
        setupScreen();

        findViewById(R.id.btn_clip_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewToImageUtil.viewSaveToImage(findViewById(R.id.screen_get));
            }
        });
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

    private void setupTextView() {
        content = findViewById(R.id.content);
        content2 = findViewById(R.id.content2);
        // 进行计算屏幕宽度，动态显示
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;
//        content.setMaxWidth(width - getPixels(TypedValue.COMPLEX_UNIT_DIP, 78));
    }

    public static int getPixels(int Unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(Unit, size, metrics);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (isNeedReconnect && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetUtils.isNetWorkConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "连接上网络", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "断开了网络", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void registerNetworkBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filter.addAction("android.net.wifi.STATE_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, filter);
    }

    private void unregisterNetworkBroadcast() {
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mNetworkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        registerReceiver(mNetworkConnectChangedReceiver, filter);
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

    /**
     * ***************************************** 清单文件不配置,  横竖屏切换时生命周期会重调 **************************************
     */

//protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    int orientation = getResources().getConfiguration().orientation;
//    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        setContentView(R.layout.land);
//    } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//        setContentView(R.layout.port);
//    }
//    //接下来，就可以做一些初始化等操作了
//
//}

    // 上面这两种方式实现对于横竖屏的切换加载不同的布局文件，都会让activity重新加载一次，那么必然就会导致数据的丢失或者是数据的重新获取，造成了过多的额外的功耗，那么我们可以在翻转之前保存一下现在已经获取到的数据，那么在翻转之后就可以直接使用，而不需要重新获取或者重新加载，具体在下面看

    //重写Activity.onSaveInstanceState()，用户横竖屏切换前保存数据
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("linc", "onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
//        outState.putString(TEXT_ONE, "" + editText1.getTag(R.id.tag_first));//avoid null point
//    outState.putSerializable();//object
    }

    /**
     * ***************************************** 清单文件配置 android:configChanges="orientation|keyboardHidden|screenSize">
     * 使横竖屏切换不改变生命周期,仅接收回调 **************************************
     */

    //1.布局分别在layout-land和layout-port目录中的同名main.xml时，就是上面说的第一种情况
    public void onConfigurationChanged1(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        //注意，这里删除了init()，否则又初始化了，状态就丢失
        setupTextView();
    }

    //2.布局为不按照layout-land和layout-port目录，而自定义名字时
    public void onConfigurationChanged2(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        int mCurrentOrientation = getResources().getConfiguration().orientation;

            if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {

                // If current screen is portrait

//            setContentView(R.layout.mainP);
                //注意，这里删除了init()，否则又初始化了，状态就丢失
                setupTextView();

            } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {

                //If current screen is landscape

//            setContentView(R.layout.mainL);
                //注意，这里删除了init()，否则又初始化了，状态就丢失
                setupTextView();
        }

    }

    //3.当然有时候连布局都不用更改的话，就可以直接对原有控件进行调用操作了，比如：
    public void onConfigurationChanged(Configuration newConfig) {


        Log.d(TAG, "onConfigurationChanged() called with: newConfig = [" + newConfig + "]");

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            content.setText("当前屏幕为横屏");
        } else {
            content.setText("当前屏幕为竖屏");

        }
        super.onConfigurationChanged(newConfig);
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //设置横屏
    }

    //改变横竖屏切换的方法
    public void changeScreen(View view) {
        /**
         * int ORIENTATION_PORTRAIT = 1;  竖屏
         * int ORIENTATION_LANDSCAPE = 2; 横屏
         */
        //获取屏幕的方向  ,数值1表示竖屏，数值2表示横屏
        int screenNum = getResources().getConfiguration().orientation;
        //（如果竖屏）设置屏幕为横屏
        if (screenNum == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //设置为置屏幕为竖屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
