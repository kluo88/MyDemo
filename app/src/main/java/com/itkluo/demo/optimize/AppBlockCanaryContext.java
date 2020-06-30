package com.itkluo.demo.optimize;

//import com.github.moduth.blockcanary.BlockCanaryContext;

//import com.github.moduth.blockcanary.BlockCanaryContext;

/**
 * 实现各种上下文，包括应用标示符，用户uid，网络类型，卡慢判断阙值，Log保存位置等
 *
 * @author luobingyong
 * @date 2020/3/18
 */
public class AppBlockCanaryContext {//extends BlockCanaryContext {
    private static final String TAG = "AppBlockCanaryContext";

//    @Override
//    public String provideQualifier() {
//        String qualifier = "";
//        try {
//            PackageInfo info = MyApplication.getInstance().getPackageManager()
//                    .getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
//            qualifier += info.versionCode + "_" + info.versionName + "_YYB";
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "provideQualifier exception", e);
//        }
//        return qualifier;
//    }
//
//    /**
//     * 设置的卡顿阀值
//     *
//     * @return
//     */
//    @Override
//    public int provideBlockThreshold() {
//        return 500;
//    }
//
//    @Override
//    public boolean displayNotification() {
//        return BuildConfig.DEBUG;
//    }
//
//    @Override
//    public boolean stopWhenDebugging() {
//        return false;
//    }
}

