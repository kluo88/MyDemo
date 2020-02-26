package com.itkluo.demo.apk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

/**
 * Created by luobingyong on 2018/8/20.
 */
public class AppUtils {
    /**
     * 安装指定路径下的Apk
     */
    public void installApk(Activity context, String apkFilePath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File(apkFilePath)), "application/vnd.android.package-archive");
        context.startActivityForResult(intent, 0);
    }

    /**
     * 卸载指定包名的App
     */
    public void uninstallApp(Activity context,String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivityForResult(intent, 0);
    }

    /**
     * 打开指定包名的App
     */
    public void openSpecifiedApp(Activity context,String packageName){
        PackageManager manager = context.getPackageManager();
        Intent launchIntentForPackage = manager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null) {
            context.startActivity(launchIntentForPackage);
        }
    }

}
