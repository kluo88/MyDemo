package com.itkluo.demo.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import com.itkluo.demo.apk.ApkFileInfoData;
import com.itkluo.demo.apk.AppDetailBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描存储apk的目录，解析apk信息、生成apk的png文件、生成json数据
 *
 * @author luobingyong
 * @date 2020/1/19
 */
public class ApkFileParser {
    private static final String TAG = "ApkFileParser";
    private static final String APK_FILE_EXTENSION = ".apk";
    private static final String PNG_FILE_EXTENSION = ".png";
    public static final String sApkFileDir = getApkListDir("ApkList");
    private static final String sDestApkFileDir = getApkListDir("DestApkList");

    private final static class HolderClass {
        private final static ApkFileParser INSTANCE = new ApkFileParser();
    }

    public static ApkFileParser getImpl() {
        return HolderClass.INSTANCE;
    }

    private ApkFileParser() {
    }

    private static String getApkListDir(String dirName) {
        File folder = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
            }
        }
        return folder.getAbsolutePath();
    }


    /**
     * 扫描存储apk的目录，解析apk信息、生成apk的png文件、生成json数据
     *
     * @return
     */
    public String scanDirWithGetApkListJson(Context context) {
        String scanDirPath = sApkFileDir;
        if (TextUtils.isEmpty(scanDirPath)) {
            return scanDirPath;
        }
        File scanDir = new File(scanDirPath);
        if (!scanDir.exists()) {
            return null;
        }
        List<AppDetailBean> appListItems = new ArrayList<>();
        final File[] files = scanDir.listFiles();
        if (files == null || files.length == 0) {
            LogUtils.d(TAG, "No files in app dir " + scanDir);
            return null;
        }
        int fileCount = 0;
        for (File file : files) {
            final boolean isPackage = (isApkFile(file) || file.isDirectory());
            if (!isPackage) {
                // Ignore entries which are not packages
                continue;
            }
            ApkFileInfoData apkFileInfoData = startProcessApkFile(context, file);
            if (apkFileInfoData == null) {
                continue;
            }
            // 转化为传输的json数据实体
            AppDetailBean appDetailBean = convertToJsonBean(apkFileInfoData);
            if (appDetailBean == null) {
                continue;
            }
            // 文件大小
            appDetailBean.size = getApkFileFormatSize(file);
            appListItems.add(appDetailBean);
            fileCount++;
        }
        // 生成Json实体
        return generateAppListItemsJson(appListItems);
    }

    /**
     * 获取apk格式化单位的文件大小 "11.2M"
     *
     * @return
     */
    private static String getApkFileFormatSize(File file) {
        long fileSize = file.length();
        double kiloByte = (double) fileSize / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 转化为传输的json数据实体
     *
     * @param apkFileInfoData
     * @return
     */
    private AppDetailBean convertToJsonBean(ApkFileInfoData apkFileInfoData) {
        AppDetailBean appDetailBean = new AppDetailBean();
        appDetailBean.packageName = apkFileInfoData.getPackageName();
        appDetailBean.appName = apkFileInfoData.getAppName();
        appDetailBean.version = apkFileInfoData.getVersionName();
        appDetailBean.versionCode = apkFileInfoData.getVersionCode();
        // 阿里存储的图片地址 http://vebappstore.oss-cn-hangzhou.aliyuncs.com/com.tencent.mm_660_icon.png
        appDetailBean.thumbnail = "http://vebappstore.oss-cn-hangzhou.aliyuncs.com/"
                + apkFileInfoData.getPackageName() + PNG_FILE_EXTENSION;
        // 阿里存储的apk下载地址 http://vebappstore.oss-cn-hangzhou.aliyuncs.com/com.tencent.mm_660.apk
        appDetailBean.downloadUrl = "http://vebappstore.oss-cn-hangzhou.aliyuncs.com/"
                + apkFileInfoData.getPackageName() + APK_FILE_EXTENSION;
        appDetailBean.size = "";
        return appDetailBean;
    }

    /**
     * 解析apk文件
     *
     * @param file
     * @return
     */
    private ApkFileInfoData startProcessApkFile(Context context, File file) {
        ApkFileInfoData apkFileInfoData = ApkFileParser.getUnInstallAPKInfo(context, file.getAbsolutePath());
        // 以包名做重命名
        File newFile = new File(sApkFileDir, apkFileInfoData.getPackageName() + APK_FILE_EXTENSION);
        file.renameTo(newFile);
        // 生成图片文件
        File iconFile = new File(sApkFileDir, apkFileInfoData.getPackageName() + PNG_FILE_EXTENSION);
        ImageUtils.saveDrawableToFile(apkFileInfoData.getAppIcon(), iconFile);
        return apkFileInfoData;
    }

    /**
     * 生成App列表json数据
     *
     * @param appListItems
     */
    private String generateAppListItemsJson(List<AppDetailBean> appListItems) {
        return JsonUtils.toJson(appListItems);
    }

    private static boolean isApkFile(File file) {
        return isApkPath(file.getName());
    }

    private static boolean isApkPath(String path) {
        return path.endsWith(APK_FILE_EXTENSION);
    }


    /**
     * 获取未安装apk的版本,图标等信息,
     * 使用PackageManager及PackageInfo类
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static ApkFileInfoData getUnInstallAPKInfo(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath) || !apkPath.toLowerCase().endsWith(APK_FILE_EXTENSION)) {
            return null;
        }
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return null;
        }
        ApkFileInfoData apkFileInfoData = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String packageName = packageInfo.packageName;
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            String appName = (String) pm.getApplicationLabel(applicationInfo);
            Drawable icon = pm.getApplicationIcon(applicationInfo);
            apkFileInfoData = new ApkFileInfoData();
            apkFileInfoData.setPackageName(packageName);
            apkFileInfoData.setVersionName(versionName);
            apkFileInfoData.setAppName(appName);
            apkFileInfoData.setVersionCode(String.valueOf(versionCode));
            apkFileInfoData.setAppIcon(icon);
        }
        return apkFileInfoData;
    }

    /**
     * 拷贝文件
     *
     * @param sourceFile
     * @param destFile
     * @return
     */
    private static boolean copyFile(String sourceFile, String destFile) {
        File in = new File(sourceFile);
        File out = new File(destFile);
        if (!in.exists()) {
            return false;
        }
        if (!out.exists()) {
            out.mkdirs();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(new File(destFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int c;
        byte[] b = new byte[1024 * 5];

        try {
            while ((c = fis.read(b)) != -1) {
                fos.write(b, 0, c);
            }

            fos.flush();
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

}
