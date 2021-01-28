package com.itkluo.demo;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
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

        // 滴滴出行开源的效率测试工具
        DoraemonKit.install(this);

    }

    /**
     * 获得当前app运行的AppContext
     */
    public static MyApplication getInstance() {
        return sInstance;
    }


}