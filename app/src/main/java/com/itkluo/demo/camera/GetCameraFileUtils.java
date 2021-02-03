package com.itkluo.demo.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 相册或拍照选取多张照片工具类
 */
public class GetCameraFileUtils {
    public static final int TAKE_ON_CAMERA = 1002;

    private static final String[] PROJECTION = {MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATA};
    private static long startStamp = Long.MAX_VALUE;

    /**
     * 调手机相机拍多张照片
     */
    public static void takeOnCamera(Activity activity) {
        //打开相机之前，记录时间1
        startStamp = System.currentTimeMillis();
        Intent intent = new Intent();
        //此处之所以诸多try catch，是因为各大厂商手机不确定哪个方法
        try {
            intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            activity.startActivityForResult(intent, TAKE_ON_CAMERA);
        } catch (Exception e) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                activity.startActivityForResult(intent, TAKE_ON_CAMERA);
            } catch (Exception e1) {
                try {
                    intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE);
                    activity.startActivityForResult(intent, TAKE_ON_CAMERA);
                } catch (Exception ell) {
                    Toast.makeText(activity, "打开摄像机失败，请从相册选择照片", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * 获取拍摄的照片，在调用takeOnCamera之后返回界面是调用该方法。
     *
     * @param context
     * @param endStamp 结束拍照的时间   13位(到毫秒)
     * @return
     */
    public static List<String> getCameraFile(Context context, long endStamp) {

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                            null, null,
                            MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT 0,30");
            if (cursor != null) {
                //HashSet去重的
                HashSet<String> findList = new HashSet<String>();

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    long createTemp = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                    if (isAfterStart(startStamp, endStamp, createTemp)) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                        if (path.startsWith("IMG")) {//筛选
                            findList.add(path);
//                        }
                    }
                    cursor.moveToNext();
                }
                return new ArrayList<>(findList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 判断照片创建时间是不是在 调相机起始~拍照结束
     *
     * @param startStamp  开始调相机的时间 13位(到毫秒)
     * @param endStamp    结束拍照的时间   13位(到毫秒)
     * @param createStamp 照片创建的时间 时间戳可能是10位(到秒)或者是13位(到毫秒)，要先统一位数
     * @return
     */
    private static boolean isAfterStart(long startStamp, long endStamp, long createStamp) {
        int createTempLength = String.valueOf(createStamp).length();
        if (createTempLength == 10) {
            startStamp = startStamp / 1000;
            endStamp = endStamp / 1000;

        }

        if (endStamp <= 0) {
            return (createStamp - startStamp >= 0);
        }
        return (createStamp - startStamp >= 0) && (endStamp - createStamp >= 0);
    }


    /**
     * 得到手机中所有的照片（async）
     */
    public static List<String> getAllPhoto(Context context) {

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                            null, null,
                            MediaStore.Images.Media.DATE_ADDED + " DESC");
            if (cursor != null) {
                List<String> photoList = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    photoList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    cursor.moveToNext();
                }
                return photoList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
