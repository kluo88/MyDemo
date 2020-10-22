package com.itkluo.camera.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author luobingyong
 * @date 2020/10/21
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    private BitmapUtils() {

    }

    public static byte[] toByteArray(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream) os);
        return os.toByteArray();
    }


    public static Bitmap mirror(Bitmap rawBitmap) {
        if (rawBitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(-1.0F, 1.0F);
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);
    }


    public static Bitmap rotate(Bitmap rawBitmap, float degree) {
        if (rawBitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);
    }


    public static Bitmap decodeBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        if (bitmap == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, (OutputStream) bos);
        BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size(), options);
    }


    public static Bitmap decodeBitmapFromFile(String path, int reqWidth, int reqHeight) {
        if (path == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }


    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        if (res == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int rawWidth = options.outWidth;
        int rawHeight = options.outHeight;
        int inSampleSize = 1;

        if (rawWidth > reqWidth || rawHeight > reqHeight) {
            int halfWidth = rawWidth / 2;
            int halfHeight = rawHeight / 2;

            while ((halfWidth / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2;  //设置inSampleSize为2的幂是因为解码器最终还是会对非2的幂的数进行向下处理，获取到最靠近2的幂的数
            }
        }
        return inSampleSize;
    }

    public static void saveCameraPic(final byte[] data, final boolean isMirror, final SavePicCallBack callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedOutputStream bos = null;
                try {
                    long temp = System.currentTimeMillis();
                    File picFile = FileUtil.createCameraFile("camera2");
                    if (picFile != null && data != null) {
                        Bitmap rawBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap resultBitmap;
                        if (isMirror) {
                            resultBitmap = mirror(rawBitmap);
                        } else {
                            resultBitmap = rawBitmap;
                        }
//                        picFile.sink().buffer().write(toByteArray(resultBitmap)).close();
                        bos = new BufferedOutputStream(new FileOutputStream(picFile));
                        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();

                        Log.d(TAG, "图片已保存! 耗时：" + (System.currentTimeMillis() - temp) + "    路径：  " + picFile.getAbsolutePath());

                        callback.onSuccess(picFile.getAbsolutePath(), (System.currentTimeMillis() - temp) + "");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailed(e.getMessage());
                } finally {
                    IoUtils.closeIo(bos);
                }
            }
        }).start();
    }

    /**
     * 让Gallery上能马上看到该图片
     */
    public static void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public  interface SavePicCallBack {
        void onSuccess(String savedPath, String time);

        void onFailed(String msg);
    }
}
