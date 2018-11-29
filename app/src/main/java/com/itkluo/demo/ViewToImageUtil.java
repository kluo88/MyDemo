package com.itkluo.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by luobingyong on 2018/8/13.
 */
public class ViewToImageUtil {
    private static final String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
    private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/clip";//保存的确切位置

    public static void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.TRANSPARENT);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        try {
            saveFile(cachebmp, Calendar.getInstance().getTimeInMillis() + ".png", SAVE_REAL_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        view.destroyDrawingCache();

    }

    public static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        // v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    //下面就是保存的方法，传入参数就可以了：
    public static void saveFile(Bitmap bm, String fileName, String path) throws IOException {
        String subForder = path;
        File foder = new File(subForder);
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(subForder, fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 90, bos);
        bos.flush();
        bos.close();
        //保存图片后发送广播通知更新数据库
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(myCaptureFile);
//        intent.setData(uri);
//        MyApplication.getInstance().sendBroadcast(intent);
    }

}
