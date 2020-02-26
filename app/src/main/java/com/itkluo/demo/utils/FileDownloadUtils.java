package com.itkluo.demo.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.Locale;

/**
 * @author luobingyong
 * @date 2019/12/27
 */
public class FileDownloadUtils {
    private static final String TAG = "FileDownloadUtils";
    private static String defaultSaveRootPath;

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(defaultSaveRootPath)) {
            return defaultSaveRootPath;
        }
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "soft");
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory();
            }
        }
        return defaultSaveRootPath = folder.getAbsolutePath();
    }

    public static String getDefaultSaveFilePath(final String url) {
        return generateFilePath(getDefaultSaveRootPath(), generateFileName(url));
    }

    public static String generateFileName(final String url) {
        int index = url.lastIndexOf("/");
        String substr = url;
        if (index > 0) {
            substr = url.substring(index + 1);
        }
        return substr;
    }

    private static String generateFilePath(String directory, String filename) {
        if (filename == null) {
            Log.e(TAG, "can't generate real path, the file name is null");
            return null;
        }
        if (directory == null) {
            Log.e(TAG, "can't generate real path, the directory is null");
            return null;
        }

        return formatString("%s%s%s", directory, File.separator, filename);
    }

    private static String formatString(final String msg, Object... args) {
        return String.format(Locale.ENGLISH, msg, args);
    }



}
