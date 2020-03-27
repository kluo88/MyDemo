package com.itkluo.demo;

import android.app.Application;
import android.text.TextUtils;

import com.github.moduth.blockcanary.BlockCanary;
import com.itkluo.demo.optimize.AppBlockCanaryContext;
import com.itkluo.demo.utils.ProcessUtils;

/**
 * @author DELL
 */
public final class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (TextUtils.equals(ProcessUtils.getProcessName(this, android.os.Process.myPid()),
                getPackageName())) {
            sInstance = this;
            BlockCanary.install(this, new AppBlockCanaryContext()).start();
        }
    }

    /**
     * 获得当前app运行的AppContext
     */
    public static MyApplication getInstance() {
        return sInstance;
    }


}