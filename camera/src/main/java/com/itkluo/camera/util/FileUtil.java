package com.itkluo.camera.util;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author luobingyong
 * @date 2020/10/21
 */
public class FileUtil {
    private static String rootFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TempFile";

    public static File createImageFile(boolean isCrop) {
        try {
            File rootFile = new File(rootFolderPath + File.separator + "capture");
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = isCrop ? "IMG_" + timeStamp + "_CROP.jpg" : "IMG_" + timeStamp + ".jpg";
            return new File(rootFile.getAbsolutePath() + File.separator + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File createCameraFile(String folderName) {
        try {
            File rootFile = new File(rootFolderPath + File.separator + folderName);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            return new File(rootFile.getAbsolutePath() + File.separator + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File createVideoFile() {
        try {
            File rootFile = new File(rootFolderPath + File.separator + "video");
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "VIDEO_" + timeStamp + ".mp4";
            return new File(rootFile.getAbsolutePath() + File.separator + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
