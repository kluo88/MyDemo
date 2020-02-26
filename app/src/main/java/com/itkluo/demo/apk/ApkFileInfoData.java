package com.itkluo.demo.apk;

import android.graphics.drawable.Drawable;

/**
 * @author luobingyong
 * @date 2020/1/17
 */
public class ApkFileInfoData {
    private String packageName;
    private Drawable appIcon;
    private String appName;
    private String versionName;
    private String versionCode;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "ApkFileInfoData{" +
                "packageName='" + packageName + '\'' +
                ", appIcon=" + appIcon +
                ", appName='" + appName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                '}';
    }
}
