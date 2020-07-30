package com.itkluo.demo.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.itkluo.demo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Android拍照以及前后摄像头切换
 *
 * @author luobingyong
 * @date 2020/7/28
 */
public class CameraActivity2 extends AppCompatActivity {
    private static final String TAG = "CameraActivity2";
    private Button btnCamera = null;
    private Button chgCamera = null;
    private SurfaceView mySurfaceView = null;
    private Camera cam = null;
    private SurfaceHolder holder = null;
    private boolean previewRunning = false;
    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 2;//后置摄像头标记
    private int currentCameraType = -1;//当前打开的摄像头标记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera2);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        btnCamera = findViewById(R.id.btn_camera);
        chgCamera = findViewById(R.id.change);
        mySurfaceView = findViewById(R.id.mySurfaceView);
        holder = this.mySurfaceView.getHolder();
        holder.addCallback(new MySurfaceViewCallback());
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(800, 480);
        currentCameraType = BACK;

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cam != null) {
                    cam.autoFocus(new AutoFocusCallbackImpl());
                }
            }
        });

        chgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeCamera() throws IOException {
        if (previewRunning) {
            cam.setPreviewCallback(null);
            cam.stopPreview();
            cam.lock();
            cam.release();
            cam = null;
        }
        if (currentCameraType == FRONT) {
            cam = openCamera(BACK);
        } else if (currentCameraType == BACK) {
            cam = openCamera(FRONT);
        }
        cam.setPreviewDisplay(holder);
        cam.startPreview();
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }

        currentCameraType = type;
        if (type == FRONT && frontIndex != -1) {
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            return Camera.open(backIndex);
        }
        return null;
    }

    private class MySurfaceViewCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            CameraActivity2.this.cam = Camera.open(); //取第一个摄像头
            WindowManager manager = (WindowManager) CameraActivity2.this.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Camera.Parameters param = cam.getParameters();
            param.setPreviewSize(display.getWidth(), display.getHeight());
            param.setPreviewFrameRate(5);//一秒5帧
            param.setPictureFormat(PixelFormat.JPEG);
            param.set("jpeg-quality", 80);
            try {
                cam.setParameters(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                cam.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cam.startPreview();//预览
            previewRunning = true;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (cam != null) {
                if (previewRunning) {
                    cam.stopPreview();
                    previewRunning = false;
                }
                cam.release();//释放摄像头
            }
        }
    }

    private class AutoFocusCallbackImpl implements Camera.AutoFocusCallback {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // 对焦操作
            if (success) {
                cam.takePicture(sc, pc, jpegcall);
            }
        }

    }

    private Camera.PictureCallback jpegcall = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 保存图片操作
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            String fileName = Environment.getExternalStorageDirectory().toString()
                    + File.separator
                    + "AppTest"
                    + File.separator
                    + "PicTest_" + System.currentTimeMillis() + ".jpg";
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();//创建文件夹
            }
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);//向缓冲区压缩图片
                bos.flush();
                bos.close();
                Toast.makeText(CameraActivity2.this, "拍照成功，照片保存在" + fileName + "文件之中！", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //e.printStackTrace();
                Toast.makeText(CameraActivity2.this, "拍照失败！" + e.toString(), Toast.LENGTH_LONG).show();
            }
            cam.stopPreview();
            cam.startPreview();
        }
    };

    private android.hardware.Camera.ShutterCallback sc = new android.hardware.Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            // 按下快门之后进行的操作
        }
    };
    private android.hardware.Camera.PictureCallback pc = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

        }
    };

}
