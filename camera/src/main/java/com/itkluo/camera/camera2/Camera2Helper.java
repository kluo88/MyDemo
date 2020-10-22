package com.itkluo.camera.camera2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import com.itkluo.camera.util.BitmapUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Camera2 相机工具类：实现预览、拍照、保存照片等功能
 *
 * @author luobingyong
 * @date 2020/10/21
 */
public class Camera2Helper {
    private static final String TAG = "Camera2Helper";
    private Activity mActivity;
    private TextureView mTextureView;    //用于预览的SurfaceView对象

    private static final int PREVIEW_WIDTH = 720;                                         //预览的宽度
    private static final int PREVIEW_HEIGHT = 1280;                                       //预览的高度
    private static final int SAVE_WIDTH = 720;                                            //保存图片的宽度
    private static final int SAVE_HEIGHT = 1280;                                          //保存图片的高度

    private CameraManager mCameraManager;

    private ImageReader mImageReader = null;
    private CameraDevice mCameraDevice = null;
    private CameraCaptureSession mCameraCaptureSession = null;

    private String mCameraId = "0";
    private CameraCharacteristics mCameraCharacteristics;

    private int mCameraSensorOrientation = 0;                                            //摄像头方向
    private int mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;              //默认使用后置摄像头
    private int mDisplayRotation;  //手机方向

    private boolean isSurfaceAvailable = false;                                          //是否 SurfaceTexture 已准备好
    private boolean canTakePic = true;                                                       //是否可以拍照
    private boolean canExchangeCamera = false;                                               //是否可以切换摄像头

    private Handler mCameraHandler;
    private HandlerThread handlerThread = new HandlerThread("CameraThread");

    private Size mPreviewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT);                      //预览大小
    private Size mSavePicSize = new Size(SAVE_WIDTH, SAVE_HEIGHT);                            //保存图片大小

    private boolean showToast = true;   //是否弹 toast


    public Camera2Helper(Activity activity, TextureView textureView) {
        mActivity = activity;
        mTextureView = textureView;
        init();
    }

    private void init() {
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
    }

    /**
     * 初始化SurfaceView，开启预览
     */
    private void startInitSurface() {
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                isSurfaceAvailable = true;
                initCameraInfo();
                openCameraInternal();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                releaseCamera();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    /**
     * 初始化
     */
    private void initCameraInfo() {
        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIdList = new String[0];
        try {
            cameraIdList = mCameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (cameraIdList.length == 0) {
            toast("没有可用相机");
            return;
        }

        for (String id : cameraIdList) {
            CameraCharacteristics cameraCharacteristics = null;
            try {
                cameraCharacteristics = mCameraManager.getCameraCharacteristics(id);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            int facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

            if (facing == mCameraFacing) {
                mCameraId = id;
                mCameraCharacteristics = cameraCharacteristics;
            }
            log("设备中的摄像头 " + id);
        }

        if (mCameraCharacteristics == null) {
            log("没找到匹配的摄像头 " + mCameraFacing);
            return;
        }

        int supportLevel = mCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
//            toast("相机硬件不支持新特性")
        }

        //获取摄像头方向
        mCameraSensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
        StreamConfigurationMap configurationMap = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        Size[] savePicSize = configurationMap.getOutputSizes(ImageFormat.JPEG);          //保存照片尺寸
        Size[] previewSize = configurationMap.getOutputSizes(SurfaceTexture.class); //预览尺寸

        mDisplayRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        boolean exchange = exchangeWidthAndHeight(mDisplayRotation, mCameraSensorOrientation);

        mSavePicSize = getBestSize(
                exchange ? mSavePicSize.getHeight() : mSavePicSize.getWidth(),
                exchange ? mSavePicSize.getWidth() : mSavePicSize.getHeight(),
                exchange ? mSavePicSize.getHeight() : mSavePicSize.getWidth(),
                exchange ? mSavePicSize.getWidth() : mSavePicSize.getHeight(),
                Arrays.asList(savePicSize), "设置保存图片尺寸");

        mPreviewSize = getBestSize(
                exchange ? mPreviewSize.getHeight() : mPreviewSize.getWidth(),
                exchange ? mPreviewSize.getWidth() : mPreviewSize.getHeight(),
                exchange ? mTextureView.getHeight() : mTextureView.getWidth(),
                exchange ? mTextureView.getWidth() : mTextureView.getHeight(),
                Arrays.asList(previewSize), "设置预览尺寸");

        mTextureView.getSurfaceTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

        log("预览最优尺寸 ：" + mPreviewSize.getWidth() + " * " + mPreviewSize.getHeight() + ", 比例  " + ((float) mPreviewSize.getWidth()) / mPreviewSize.getHeight());
        log("保存图片最优尺寸 ：" + mSavePicSize.getWidth() + " * " + mSavePicSize.getHeight() + ", 比例  " + ((float) mSavePicSize.getWidth()) / mSavePicSize.getHeight());


        mImageReader = ImageReader.newInstance(mSavePicSize.getWidth(), mSavePicSize.getHeight(), ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(onImageAvailableListener, mCameraHandler);
    }

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();

            //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            image.close();

            BitmapUtils.saveCameraPic(data, mCameraSensorOrientation == 270, new BitmapUtils.SavePicCallBack() {
                @Override
                public void onSuccess(final String savedPath, final String time) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("图片保存成功！ 保存路径：" + savedPath + " 耗时：" + time);
                        }
                    });
                }

                @Override
                public void onFailed(final String msg) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast("图片保存失败！ " + msg);
                        }
                    });
                }
            });
        }
    };


    /**
     * 打开相机
     */
    private void openCameraInternal() {
        try {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                toast("没有相机权限！");
                return;
            }
            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    log("onOpened");
                    mCameraDevice = camera;
                    createCaptureSession(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    log("onDisconnected");
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    log("onError " + error);
                    toast("打开相机失败！" + error);
                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    /**
     * 创建预览会话
     */
    private void createCaptureSession(CameraDevice cameraDevice) {

        CaptureRequest.Builder captureRequestBuilder = null;
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        Surface surface = new Surface(mTextureView.getSurfaceTexture());
        captureRequestBuilder.addTarget(surface);  // 将CaptureRequest的构建器与Surface对象绑定在一起
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);      // 闪光灯
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE); // 自动对焦

        // 为相机预览，创建一个CameraCaptureSession对象
        try {
            final CaptureRequest.Builder finalCaptureRequestBuilder = captureRequestBuilder;
            cameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    try {
                        session.setRepeatingRequest(finalCaptureRequestBuilder.build(), mCaptureCallBack, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    toast("开启预览会话失败！");
                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //这个回调接口用于拍照结束时重启预览，因为拍照会导致预览停止
    private CameraCaptureSession.CaptureCallback mCaptureCallBack = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            canExchangeCamera = true;
            canTakePic = true;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            log("onCaptureFailed");
            toast("开启预览失败！");
        }
    };

    /**
     * 打开相机
     */
    public void openCamera() {
        if (!isSurfaceAvailable) {
            startInitSurface();
            return;
        }
        openCameraInternal();
    }

    /**
     * 拍照
     */
    public void takePic() {
        if (mCameraDevice == null || !mTextureView.isAvailable() || !canTakePic) {
            return;
        }

        CaptureRequest.Builder captureRequestBuilder = null;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        captureRequestBuilder.addTarget(mImageReader.getSurface());

        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE); // 自动对焦
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);     // 闪光灯
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mCameraSensorOrientation);      //根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
        if (mCameraCaptureSession != null) {
            try {
                mCameraCaptureSession.capture(captureRequestBuilder.build(), null, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            toast("拍照异常！");
        }
    }

    /**
     * 切换摄像头
     */
    public void exchangeCamera() {
        if (mCameraDevice == null || !canExchangeCamera || !mTextureView.isAvailable()) {
            return;
        }

        if (mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        } else {
            mCameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
        }

        mPreviewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT); //重置预览大小

        releaseCamera();

        initCameraInfo();

        openCameraInternal();
    }

    /**
     * 根据提供的参数值返回与指定宽高相等或最接近的尺寸
     *
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @param maxWidth     最大宽度(即TextureView的宽度)
     * @param maxHeight    最大高度(即TextureView的高度)
     * @param sizeList     支持的Size列表
     * @return 返回与指定宽高相等或最接近的尺寸
     */
    private Size getBestSize(int targetWidth, int targetHeight, int maxWidth, int maxHeight, List<Size> sizeList, String tag) {
        List<Size> bigEnough = new ArrayList<Size>();     //比指定宽高大的Size列表
        List<Size> notBigEnough = new ArrayList<Size>();  //比指定宽高小的Size列表

        for (Size size : sizeList) {

            //宽<=最大宽度  &&  高<=最大高度  &&  宽高比 == 目标值宽高比
            if (size.getWidth() <= maxWidth && size.getHeight() <= maxHeight
                    && size.getWidth() == size.getHeight() * targetWidth / targetHeight) {

                if (size.getWidth() >= targetWidth && size.getHeight() >= targetHeight)
                    bigEnough.add(size);
                else
                    notBigEnough.add(size);
            }
            log(tag + "-->" + "系统支持的尺寸: " + size.getWidth() + " * " + size.getHeight() + ",  比例 ：" + ((float) size.getWidth()) / size.getHeight());
        }

        log(tag + "-->" + "最大尺寸 ：" + maxWidth + "* " + maxHeight + ", 比例 ：" + ((float) maxWidth) / maxHeight);
        log(tag + "-->" + "目标尺寸 ：" + targetWidth + " * " + targetHeight + ", 比例 ：" + ((float) targetWidth) / targetHeight);

        //选择bigEnough中最小的值  或 notBigEnough中最大的值
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            return sizeList.get(0);
        }
    }

    /**
     * 根据提供的屏幕方向 [displayRotation] 和相机方向 [sensorOrientation] 返回是否需要交换宽高
     */
    private boolean exchangeWidthAndHeight(int displayRotation, int sensorOrientation) {
        boolean exchange = false;
        switch (displayRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    exchange = true;
                }
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    exchange = true;
                }
                break;
            default:
                log("Display rotation is invalid: " + displayRotation);
                break;
        }

        log("屏幕方向  " + displayRotation);
        log("相机方向  " + sensorOrientation);
        return exchange;
    }

    private void releaseCamera() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }

        canExchangeCamera = false;
    }

    private void releaseThread() {
        handlerThread.quitSafely();
    }

    public void release() {
        releaseCamera();
        releaseThread();
    }

    private static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size size1, Size size2) {
            return java.lang.Long.signum(((long) size1.getWidth()) * size1.getHeight() - ((long) size2.getWidth()) * size2.getHeight());
        }

    }

    private void toast(String msg) {
        if (!showToast) {
            return;
        }
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }

    private void log(String msg) {
        Log.e(TAG, msg);
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    /**
     * 设置使用的摄像头 0前置 1后置
     * @param cameraFacing
     */
    public void setDefaultCameraFacing(int cameraFacing) {
        if (cameraFacing == 0) {
            mCameraFacing = CameraCharacteristics.LENS_FACING_FRONT;
        } else {
            mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        }
    }
}
