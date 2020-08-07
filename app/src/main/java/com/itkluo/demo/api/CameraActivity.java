package com.itkluo.demo.api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.R;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 标记前置，后置摄像头，以及当前打开的摄像头，并且首先默认打开前置摄像头，监听Button的click事件，切换摄像头
 *
 * @author luobingyong
 * @date 2020/7/28
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = "CameraActivity";
    private Button button;
    private TextView textCameraType;
    private TextView textInfo;
    private Camera camera;
    private CameraView cameraView;
    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 2;//后置摄像头标记
    private int currentCameraType = -1;//当前打开的摄像头标记
    private static final int ERR_CODE_FRONT_EMPTY = -1;
    private static final int ERR_CODE_BACK_EMPTY = -2;
    private static final int ERR_CODE_FRONT_OPEN_FAIL = -11;
    private static final int ERR_CODE_BACK_OPEN_FAIL = -22;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_camera);
        initView();
        requestPermissions();
        setup();
    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeCamera();
                } catch (IOException e) {
                }
            }
        });
        cameraView = (CameraView) findViewById(R.id.cameraview);
        textCameraType = findViewById(R.id.textCameraType);
        textInfo = findViewById(R.id.textInfo);
    }

    private void setup() {
        if (!checkCamera()) {
            Toast.makeText(CameraActivity.this, "无摄像头", Toast.LENGTH_SHORT).show();
            CameraActivity.this.finish();
        }
        try {
            camera = openCamera(BACK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (camera == null) {
//                Log.e(TAG, "打开后置摄像头失败");
//                CameraActivity.this.finish();
            }
        }

        if (camera != null) {
            cameraView.init(camera);
        }
    }

    private void showCurCameraTypeText(int errCode, int cameraIndex) {
        StringBuilder str = new StringBuilder();
        if (currentCameraType == FRONT) {

//                 str.append("前置摄像");
            str.append("摄像");
        } else if (currentCameraType == BACK) {
//            str.append("后置摄像");
            str.append("摄像");
        }
        switch (errCode) {
            case ERR_CODE_FRONT_EMPTY:
            case ERR_CODE_BACK_EMPTY:
                str.append("为空");
                break;
            case ERR_CODE_FRONT_OPEN_FAIL:
            case ERR_CODE_BACK_OPEN_FAIL:
                str.append("打开失败");
                break;
            default:
                break;
        }

        str.append(" currentCameraIndex: ").append(cameraIndex);

        textCameraType.setText(str.toString());
    }

    /**
     * @return 摄像头是否存在
     */
    private boolean checkCamera() {
//        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }


    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Log.i(TAG, "cameraCount: " + cameraCount);
        Camera getCamera = null;
        Camera.CameraInfo info = new Camera.CameraInfo();
        StringBuilder logStr = new StringBuilder("cameraCount: " + cameraCount).append("\n");
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);

            String strCameraType = "";
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
                strCameraType = "前置";
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
                strCameraType = "后置";
            }

            Log.i(TAG, "Camera.CameraInfo.facing: " + info.facing + "  cameraIndex: " + cameraIndex);
            logStr.append("cameraIndex: ").append(cameraIndex).append(" ").append("Camera.CameraInfo.facing: ").append(info.facing)
                    .append(" ").append(strCameraType)
                    .append("\n");

        }

        textInfo.setText(logStr.toString());

        // FIXME: 2020/7/28 切换显示所有摄像头
        frontIndex = 0;
        backIndex = 1;

        currentCameraType = type;

        if (type == FRONT && frontIndex == -1) {
            showCurCameraTypeText(ERR_CODE_FRONT_EMPTY, frontIndex);
            return null;
        } else if (type == BACK && backIndex == -1) {
            showCurCameraTypeText(ERR_CODE_BACK_EMPTY, backIndex);
            return null;
        }


        if (type == FRONT) {
            getCamera = Camera.open(frontIndex);
            if (getCamera == null) {
                Log.e(TAG, "打开前置摄像头失败");
                showCurCameraTypeText(ERR_CODE_FRONT_OPEN_FAIL, frontIndex);
            } else {
                showCurCameraTypeText(0, frontIndex);

            }
        } else if (type == BACK) {
            getCamera = Camera.open(backIndex);
            if (getCamera == null) {
                Log.e(TAG, "打开后置摄像头失败");
                showCurCameraTypeText(ERR_CODE_BACK_OPEN_FAIL, backIndex);
            } else {
                showCurCameraTypeText(0, backIndex);
            }
        }


        return getCamera;
    }

    private void changeCamera() throws IOException {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }


        if (currentCameraType == FRONT) {
            camera = openCamera(BACK);

        } else if (currentCameraType == BACK) {
            camera = openCamera(FRONT);
        }

        if (camera != null) {
            camera.setPreviewDisplay(cameraView.getHolder());
            camera.startPreview();
        }
    }

    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(this);
        Disposable subscribe = rxPermission.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.i(TAG, permission.name + " is granted.");
                            Toast.makeText(mActivity, permission.name + " is granted.", Toast.LENGTH_LONG).show();
//                            setup();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e(TAG, permission.name + " is denied. More info should be provided.");
                            Toast.makeText(mActivity, permission.name + " is denied. More info should be provided.", Toast.LENGTH_LONG).show();
                            CameraActivity.this.finish();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e(TAG, permission.name + " is denied.");
                            Toast.makeText(mActivity, permission.name + " is denied.", Toast.LENGTH_LONG).show();
                            CameraActivity.this.finish();
                        }
                    }
                });

    }

}
