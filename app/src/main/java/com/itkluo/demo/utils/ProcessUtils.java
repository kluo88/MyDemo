package com.itkluo.demo.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author luobingyong
 * @date 2020/1/17
 */
public class ProcessUtils {
    public static String getProcessName(Context context, int pid) {
        if (context == null) {
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

}
