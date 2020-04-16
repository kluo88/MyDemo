package com.itkluo.demo.java.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 获取当前文件夹下的所有SO文件名, 并按照指定格式输出
 *
 * @author luobingyong
 * @date 2020/4/16
 */
public class TakeName {
    public static void main(String[] args) {
        File directory = new File("");
        String path = null;
        try {
            path = directory.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String path = "C://...//lib//armeabi-v7a";
        path += File.separator + "lib" + File.separator + "armeabi-v7a";
        // 获取到当前的路径为：D:\_project\myDemo\MyDemo-master\lib\armeabi-v7a
        System.out.println("获取到当前的路径为：" + path);
        getFile(path, "LOCAL_PREBUILT_JNI_LIBS := \\", "    @lib/armeabi-v7a/", " \\");
    }

    private static void getFile(String path, String start, String prefix, String suffix) {
        File file = new File(path);
        File[] list = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".so");
            }
        });

        int length = list.length;
        System.out.println("文件总数： "+length);

        System.out.println(start);

        for (int i = 0; i < length; i++) {
            if (list[i].isFile()) {
                if (i == length - 1) {
                    System.out.println(prefix + list[i].getName());
                } else {
                    System.out.println(prefix + list[i].getName() + suffix);
                }
            }
//            else if (array[i].isDirectory()) {
//                getFile(array[i].getPath());
//            }
        }
    }

}
