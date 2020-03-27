package com.itkluo.demo.system;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.itkluo.demo.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 截图功能
 * 通过反射调用系统的截图功能，在service中执行，可以截取任何界面，，截取图片保存本地
 *
 * @author luobingyong
 * @date 2020/3/19
 */
public class ScreenShotService extends Service {
    private String TAG = "ScreenShotService";
    private Boolean isMounted = false;
    private MiReceiver miReceiver;
    private Class screenshotClass = null;
    private Method screenShotMethod;
    private Bitmap bitmap;
    private final String folderPath = "/storage/udisk/screenshot";
    private File folder;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
//        registerBroadcastReceiver();
        new SavePicTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public ScreenShotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void screenshot() {
        try {
            if (screenshotClass == null) {
                screenshotClass = Class.forName("android.view.SurfaceControl");
            }
            if (screenShotMethod == null) {
                screenShotMethod = screenshotClass.getMethod("screenshot", int.class, int.class);
            }
            if (folder == null) {
                folder = new File(folderPath);
            }
            bitmap = (Bitmap) screenShotMethod.invoke(null, new Object[]{1920, 720});
            if (isMounted) {
                String path = folderPath + "/" + UUID.randomUUID().toString() + ".png";
                if (folder.exists()) {
                    saveScreenShot(bitmap, path);
                } else {
                    folder.mkdirs();
                    saveScreenShot(bitmap, path);
                }
            } else {
                Log.i(TAG, "usb未插入");
            }
            bitmap.recycle();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void saveScreenShot(Bitmap bitmap, String path) {
        Log.i(TAG, "开始保存截图");
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            Log.i(TAG, "FileNotFoundException: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "IOException: " + e.toString());
            e.printStackTrace();
        }
    }

    class MiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("mi.intent.action.SWCONTROL_STATUS".equals(intent.getAction())) {
                if (intent.getBooleanExtra("sw_control", false)) {
                    Log.i(TAG, "截图");
                    new SavePicTask().execute();
                    //screenshot();
                }

            } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                isMounted = true;
                Log.i(TAG, "isMounted: " + isMounted);

            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction()) || Intent.ACTION_MEDIA_EJECT.equals(intent.getAction()) || Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction())) {
                isMounted = false;
                Log.i(TAG, "isMounted: " + isMounted);
            }
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        //usb挂载相关广播
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction("mi.intent.action.SWCONTROL_STATUS");
        filter.addDataScheme("file");
        if (miReceiver == null) {
            miReceiver = new MiReceiver();
        }
        registerReceiver(miReceiver, filter);
        Log.i(TAG, "注册广播");
    }

    public class SavePicTask extends AsyncTask {
        //执行线程任务前的操作
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        @Override
        protected Object doInBackground(Object[] objects) {
            screenshot();
            return null;
        }

        //在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        //接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(MyApplication.getInstance(), "截图完成", Toast.LENGTH_SHORT).show();
            super.onPostExecute(o);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
