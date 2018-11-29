package com.itkluo.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 */
public class NetUtils {
    // 无网络
    public final static int NONE = 0;
    // Wi-Fi
    public final static int WIFI = 1;
    // 3G,GPRS
    public final static int MOBILE = 2;
    final private static String TAG = "NetUtils";
    private volatile static NetUtils instance;

    /**
     * * 获取当前网络状态
     *
     * @param context
     * @return
     */

    public static int getNetworkState(Context context) {
        if (context == null) {
            Log.i(TAG, "getNetworkState: context = null");
            return NONE;
        }
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    Log.e(TAG, "当前WiFi连接可用 ");
                    return WIFI;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    Log.e(TAG, "当前移动网络连接可用 ");
                    return MOBILE;
                }
            } else {
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            }
        }
        // not connected to the internet
        Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
        return NONE;

//        // Wifi网络判断
//        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if (state == State.CONNECTED || state == State.CONNECTING) {
//            return WIFI;
//        }
//        // 手机网络判断
//        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//        if (state == State.CONNECTED || state == State.CONNECTING) {
//            return MOBILE;
//        }
//        return NONE;
    }

    public static boolean isNetWorkConnected(Context context) {
        return getNetworkState(context) != NONE;
    }


}
