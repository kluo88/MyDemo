package com.itkluo.demo.usb.wdreader.readerlib;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 描述：    日志打印保存本地工具类
 * 项目名称：AndroidUtilsDemo
 * 创建人：  FangZhixin
 * 创建时间：2018/12/7
 */
public class LogDriveUtil {

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARM = 4;
    private static final int ERROR = 5;
    //当前运行显示log的级别
    public static int level = VERBOSE;
    private static String TAG = "LogDriveUtil";
    private static String pkgName = "unInit";
    //是否保存日志
    private static boolean isSaveLog = false;
    //设置保留的日志数
    private static int saveLogNum = 30;
    //
    private static SimpleDateFormat logTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.getDefault());
    private static SimpleDateFormat logFileFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static String logPath = Environment.getExternalStorageDirectory() + File.separator + "YlzLog";

    /**
     * 在Application初始化，如果不初始化，则使用默认参数
     *
     * @param context 上下文
     * @param save    是否保存日志
     * @param count   保存的日志的最大数目，超出数目的就按名称排序开始删除
     */
    public static void init(Context context, boolean save, int count) {
        pkgName = context.getPackageName();
        File cacheFile = context.getExternalCacheDir();
        if (cacheFile != null) {
            //  /mnt/internal_sd/Android/data/com.fzx.androidutilsdemo/log
            logPath = cacheFile.getParentFile().getAbsolutePath() + File.separator + "ylz_log";
            Log.i(TAG, "init: logPath=" + logPath);
        }
        isSaveLog = save;
        saveLogNum = count;
        clearLog();//清除保留外的log
    }

    public static void v(String msg) {
        if (level <= VERBOSE) {
            v(TAG, msg);
        }
    }

    public static void v(String TAG, String msg) {
        if (level <= VERBOSE) {
            Log.v(TAG, msg);
        }
        if (isSaveLog) {
            write("V/" + TAG, msg);
        }
    }

    public static void d(String msg) {
        if (level <= DEBUG) {
            d(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (level <= DEBUG) {
            Log.d(TAG, msg);
        }
        if (isSaveLog) {
            write("D/" + TAG, msg);
        }
    }

    public static void i(String msg) {
        if (level <= INFO) {
            i(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (level <= INFO) {
            Log.i(TAG, msg);
        }
        if (isSaveLog) {
            write("I/" + TAG, msg);
        }
    }

    public static void w(String msg) {
        if (level <= WARM) {
            w(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (level <= WARM) {
            Log.w(TAG, msg);
        }
        if (isSaveLog) {
            write("W/" + TAG, msg);
        }
    }

    public static void e(String msg) {
        if (level <= ERROR) {
            e(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (level <= ERROR) {
            Log.e(TAG, msg);
        }
        if (isSaveLog) {
            write("E/" + TAG, msg);
        }
    }

    private static void write(String tag, String msg) {
        String logTime = logTimeFormat.format(Calendar.getInstance().getTime());
        String fileName = logFileFormat.format(Calendar.getInstance().getTime());
        String info = logTime + "/" + pkgName + " " + tag + " " + msg;
        writeString2File(logPath + "/ylz_" + fileName + ".log", info, true);
    }

    /**
     * 写字符串到文件中（txt文件）
     *
     * @param path     String，文件路径
     * @param text     String，字符串
     * @param isAppend boolean，是否往后追加内容
     */
    public static void writeString2File(String path, String text, boolean isAppend) {
        File file = new File(path);
        writeString2File(file, text, isAppend);
    }

    /**
     * 写字符串到文件中（txt文件）
     *
     * @param file     File，文件
     * @param text     String，字符串
     * @param isAppend boolean，是否往后追加内容
     */
    public static void writeString2File(File file, String text, boolean isAppend) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        long startTime = System.currentTimeMillis();
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file, isAppend));
            bufferedWriter.write(text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
//            System.out.println("writeString2File用时" + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLog() {
        File file = new File(logPath);
        if (!file.exists()) {
            return;
        }
        if (saveLogNum <= 0) {
            saveLogNum = 1;//至少保存一份最近的log
        }
        File[] files = file.listFiles();
        orderByTime(files);
        if (files.length > saveLogNum) {
            int delCount = files.length - saveLogNum;
            for (int i = 0; i < delCount; i++) {
                File delFile = files[i];
                boolean delete = delFile.delete();
                Log.i(TAG, "删除" + delFile.getName() + ((delete) ? "成功" : "失败"));
            }
        }
    }

    public static void orderByTime(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            @Override
            public boolean equals(Object obj) {
                Log.i(TAG, "equals: ");
                return true;
            }
        });
    }

    /**
     *
     * @param files
     */
    public static void orderByName(File[] files) {
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    /**
     * 按文件大小排序
     * @param fileList
     */
    public static void orderByLength(File[] fileList) {
        List<File> files = Arrays.asList(fileList);
        Collections.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        for (File f : files) {
            if (f.isDirectory()) continue;
            System.out.println(f.getName() + ":" + f.length());
        }
    }

}
