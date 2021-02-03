package com.itkluo.camera.camera1;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luobingyong
 * @date 2020/10/20
 */
public class CameraHelper implements Camera.PreviewCallback {
    private static final String TAG = "CameraHelper";
    private Activity mActivity;
    private Camera mCamera;//Camera对象
    private Camera.Parameters mParameters;   //Camera对象的参数
    private SurfaceView mSurfaceView;    //用于预览的SurfaceView对象
    protected SurfaceHolder mSurfaceHolder;                      //SurfaceHolder对象

    private CallBack mCallBack;//自定义的回调

    int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;  //摄像头方向
    int mDisplayOrientation = 0;  //预览旋转的角度

    private int picWidth = 2160;        //保存图片的宽
    private int picHeight = 3840;       //保存图片的高
    private SurfaceHolder.Callback mSurfaceCallback;

    public CameraHelper(Activity activity, SurfaceView surfaceView) {
        mActivity = activity;
        mSurfaceView = surfaceView;
        mSurfaceHolder = mSurfaceView.getHolder();
        init();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCallBack.onPreviewFrame(data);
    }

    void takePic() {
        if (mCamera != null) {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    mCamera.startPreview();
                    mCallBack.onTakePic(data);
                }
            });
        }
    }

    private void init() {
        if (null != mSurfaceHolder && null != mSurfaceCallback) {
            mSurfaceHolder.removeCallback(mSurfaceCallback);
        }
        mSurfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if (mCamera == null) {
                    openCamera(mCameraFacing);
                }
                startPreview();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                releaseCamera();
            }
        };
        mSurfaceHolder.addCallback(mSurfaceCallback);
    }

    //打开相机
    private Boolean openCamera(int cameraFacing) {
        boolean supportCameraFacing = supportCameraFacing(cameraFacing);
        if (supportCameraFacing) {
            try {
                mCamera = Camera.open(cameraFacing);
                initParameters(mCamera);
                mCamera.setPreviewCallback(this);
            } catch (Exception e) {
                e.printStackTrace();
                toast("打开相机失败!");
                return false;
            }
        }
        return supportCameraFacing;
    }

    //配置相机参数
    private void initParameters(Camera camera) {
        try {
            mParameters = camera.getParameters();
            mParameters.setPreviewFormat(ImageFormat.NV21);

            //获取与指定宽高相等或最接近的尺寸
            //设置预览尺寸
            Camera.Size bestPreviewSize = getBestSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), mParameters.getSupportedPreviewSizes(),
                    "设置预览尺寸");
            if (bestPreviewSize != null) {
                mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            }
            //设置保存图片尺寸
            Camera.Size bestPicSize = getBestSize(picWidth, picHeight, mParameters.getSupportedPictureSizes()
                    , "设置保存图片尺寸");
            if (bestPicSize != null) {
                mParameters.setPictureSize(bestPicSize.width, bestPicSize.height);
            }
            //对焦模式
            if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
            toast("相机初始化失败!");
        }
    }

    //开始预览
    void startPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setCameraDisplayOrientation(mActivity);
            mCamera.startPreview();
            startFaceDetect();
        }
    }

    private void startFaceDetect() {
        if (mCamera != null) {
            mCamera.startFaceDetection();
            mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                @Override
                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                    if (mCallBack != null) {
                        mCallBack.onFaceDetect(transForm(faces));
                    }
                    Log.e(TAG, "检测到 " + faces.length + "张人脸");
                }
            });
        }
    }

    //判断是否支持某一对焦模式
    private boolean isSupportFocus(String focusMode) {
        boolean autoFocus = false;
        List<String> listFocusMode = mParameters.getSupportedFocusModes();
        for (String mode : listFocusMode) {
            if (mode == focusMode) {
                autoFocus = true;
            }
            Log.e(TAG, "相机支持的对焦模式： " + mode);
        }
        return autoFocus;
    }

    //切换摄像头
    void exchangeCamera() {
        releaseCamera();
        if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        openCamera(mCameraFacing);
        startPreview();
    }

    //释放相机
    void releaseCamera() {
        if (mCamera != null) {
            // mCamera?.stopFaceDetection()
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    //获取与指定宽高相等或最接近的尺寸
    private Camera.Size getBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizeList, String tag) {
        Camera.Size bestSize = null;
        double targetRatio = (((double) targetHeight) / targetWidth);  //目标大小的宽高比
        double minDiff = targetRatio;

        for (Camera.Size size : sizeList) {
            double supportedRatio = (((double) size.width) / size.height);
            Log.e(TAG, tag + "-->" + "系统支持的尺寸 : " + size.width + "*" + size.height + ",比例" + supportedRatio);
        }

        for (Camera.Size size : sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size;
                break;
            }

            double supportedRatio = (((double) size.width) / size.height);
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }
        Log.e(TAG, tag + "-->" + "目标尺寸 ：" + targetWidth + "*" + targetHeight + "，" + "  比例  " + targetRatio);
        Log.e(TAG, tag + "-->" + "最优尺寸 ：" + bestSize.height + " * " + bestSize.width);
        return bestSize;
    }

    //设置预览旋转的角度
    private void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraFacing, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int screenDegree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                screenDegree = 0;
                break;
            case Surface.ROTATION_90:
                screenDegree = 90;
                break;
            case Surface.ROTATION_180:
                screenDegree = 180;
                break;
            case Surface.ROTATION_270:
                screenDegree = 270;
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360;
            mDisplayOrientation = (360 - mDisplayOrientation) % 360;          // compensate the mirror
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360;
        }
        mCamera.setDisplayOrientation(mDisplayOrientation);

        Log.e(TAG, "屏幕的旋转角度 : " + rotation);
        Log.e(TAG, "setDisplayOrientation(result) : " + mDisplayOrientation);
    }

    //判断是否支持某个相机
    private boolean supportCameraFacing(int cameraFacing) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
                return true;
            }
        }
        return false;
    }

    //将相机中用于表示人脸矩形的坐标转换成UI页面的坐标
    private ArrayList<RectF> transForm(Camera.Face[] faces) {
        Matrix matrix = new Matrix();
        // Need mirror for front camera.
        boolean mirror = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        matrix.setScale(mirror ? -1f : 1f, 1f);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(((float) mDisplayOrientation));
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(mSurfaceView.getWidth() / 2000f, mSurfaceView.getHeight() / 2000f);
        matrix.postTranslate(mSurfaceView.getWidth() / 2f, mSurfaceView.getHeight() / 2f);

        ArrayList<RectF> rectList = new ArrayList<RectF>();
        for (Camera.Face face : faces) {
            RectF srcRect = new RectF(face.rect);
            RectF dstRect = new RectF(0f, 0f, 0f, 0f);
            matrix.mapRect(dstRect, srcRect);
            rectList.add(dstRect);
        }
        return rectList;
    }


    private void toast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    Camera getCamera() {
        return mCamera;
    }

    void addCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    interface CallBack {
        void onPreviewFrame(byte[] data);

        void onTakePic(byte[] data);

        void onFaceDetect(ArrayList<RectF> faces);
    }

}
