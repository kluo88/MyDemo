package com.itkluo.demo.utils;


import com.itkluo.demo.BuildConfig;

/**
 * App 配置管理类
 *
 * @author luobingyong
 * @date 2020/9/28
 */
public class AppConfig {
    /**
     * 当前是否为 Debug 模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    /**
     * 获取当前应用的包名
     */
    public static String getPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    /**
     * 获取当前应用的版本名
     */
    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 获取当前应用的版本码
     */
    public static int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }


}
