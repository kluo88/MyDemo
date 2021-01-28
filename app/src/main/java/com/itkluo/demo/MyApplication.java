package com.itkluo.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.itkluo.demo.utils.DebugLog;
import com.itkluo.demo.utils.ProcessUtils;

//import com.github.moduth.blockcanary.BlockCanary;

/**
 * @author DELL
 */
public final class MyApplication extends Application {
    private static MyApplication sInstance;
    public static Handler sHandler;
    private Activity mCurrentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        if (TextUtils.equals(ProcessUtils.getProcessName(this, android.os.Process.myPid()),
                getPackageName())) {
            sInstance = this;
            sHandler = new android.os.Handler();
//            BlockCanary.install(this, new AppBlockCanaryContext()).start();
        }

        // 吐司工具类
        ToastUtils.init(this);
        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.i("Toast", text.toString());
                }
                return intercept;
            }
        });
        initActivityLifecycleCallbacks();
    }

    /**
     * 获得当前app运行的AppContext
     */
    public static MyApplication getInstance() {
        return sInstance;
    }

    private void initActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityCreated: ");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityStarted: ");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityResumed: ");
                mCurrentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityPaused: ");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityStopped: ");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivitySaveInstanceState: ");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                DebugLog.e(activity.getClass().getSimpleName(), "onActivityDestroyed: ");
            }
        });
    }


}