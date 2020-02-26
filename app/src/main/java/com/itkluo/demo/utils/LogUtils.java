package com.itkluo.demo.utils;

import android.util.Log;

/**
 * @author DELL
 */
public class LogUtils {
    private static boolean sIsOutputLog = true;

    public static boolean isOutputLog() {
        return sIsOutputLog;
    }

    /**
     * 打印info级别日志
     *
     * @param tag
     * @param message
     */
    public static void i(String tag, String message) {
        if (sIsOutputLog) {
            Log.i(tag, message);
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
            Log.v(tag, message);
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
            Log.w(tag, message);
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
            Log.d(tag, message);
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
            Log.e(tag, message);
        }
    }


}
