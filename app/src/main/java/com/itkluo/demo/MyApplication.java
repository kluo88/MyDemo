package com.itkluo.demo;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;

import com.itkluo.demo.utils.ProcessUtils;

//import com.github.moduth.blockcanary.BlockCanary;

/**
 * @author DELL
 */
public final class MyApplication extends Application {
    private static MyApplication sInstance;
    public static Handler sHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (TextUtils.equals(ProcessUtils.getProcessName(this, android.os.Process.myPid()),
                getPackageName())) {
            sInstance = this;
            sHandler = new android.os.Handler();
//            BlockCanary.install(this, new AppBlockCanaryContext()).start();
        }
    }

    /**
     * 获得当前app运行的AppContext
     */
    public static MyApplication getInstance() {
        return sInstance;
    }


}