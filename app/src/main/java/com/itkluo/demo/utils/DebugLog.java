package com.itkluo.demo.utils;

import android.util.Log;


/**
 * 用于调测代码时，log的打印
 * TAG自动增加"_test"后缀，以便设置日志过滤：logcat窗口Create New Logcat Filter--Log Tag添加正则  .*(_test)
 *
 * @author luobingyong
 * @date 2020/6/2
 */
public class DebugLog {
    private static final String TAG_SUFFIX = "_test";
    private static String mTag = "DebugLog" + TAG_SUFFIX;
//    private static boolean sIsOutputLog = BuildConfig.DEBUG;
    private static boolean sIsOutputLog = false;

    /**
     * 打印info级别日志
     *
     * @param tag
     * @param message
     */
    public static void i(String tag, String message) {
        if (sIsOutputLog) {
            Log.i(tag + TAG_SUFFIX, message);
        }

    }

    /**
     * 打印Verbose级别日志
     *
     * @param tag
     * @param message
     */
    public static void v(String tag, String message) {
        if (sIsOutputLog) {
            Log.v(tag + TAG_SUFFIX, message);
        }
    }


    /**
     * 打印warn级别日志
     *
     * @param tag
     * @param message
     */
    public static void w(String tag, String message) {
        if (sIsOutputLog) {
            Log.w(tag + TAG_SUFFIX, message);
        }
    }

    /**
     * 打印dubug级别日志
     *
     * @param tag
     * @param message
     */
    public static void d(String tag, String message) {
        if (sIsOutputLog) {
            Log.d(tag + TAG_SUFFIX, message);
        }
    }

    /**
     * 打印Error级别日志
     *
     * @param tag
     * @param message
     */
    public static void e(String tag, String message) {
        if (sIsOutputLog) {
            Log.e(tag + TAG_SUFFIX, message);
        }
    }

    /**
     * 打印info级别日志
     *
     * @param message
     */
    public static void i(String message) {
        i(mTag, message);
    }

    /**
     * 打印Verbose级别日志
     *
     * @param message
     */
    public static void v(String message) {
        v(mTag, message);
    }


    /**
     * 打印warn级别日志
     *
     * @param message
     */
    public static void w(String message) {
        w(mTag, message);
    }

    /**
     * 打印dubug级别日志
     *
     * @param message
     */
    public static void d(String message) {
        d(mTag, message);
    }

    /**
     * 打印Error级别日志
     *
     * @param message
     */
    public static void e(String message) {
        e(mTag, message);
    }


}
