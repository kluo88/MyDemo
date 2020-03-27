package com.itkluo.demo.system;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.itkluo.demo.MyApplication;

import java.nio.ByteBuffer;

/**
 * @author Bert
 * @date 2017/4/14
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ShotUtils {

    public static final int REQUEST_MEDIA_PROJECTION = 1;
    private Context mContext;
    private ImageReader mImageReader;
    private Intent mResultData = null;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private WindowManager mWindowManager;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private final static class HolderClass {
        private final static ShotUtils INSTANCE = new ShotUtils(MyApplication.getInstance().getApplicationContext());
    }

    public static ShotUtils getInstance() {
        return ShotUtils.HolderClass.INSTANCE;
    }

    private ShotUtils(Context context) {
        super();
        this.mContext = context;
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        createImageReader();
    }

    public void setData(Intent resultData) {
        mResultData = resultData;
    }

    public void init(Activity mActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
//            Toast.makeText(mContext, "系统版本不支持", Toast.LENGTH_SHORT).show()
            return;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                mActivity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mActivity.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createImageReader() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }

    public void startScreenShot(final ShotListener mShotListener) {
        startVirtual();
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startCapture(mShotListener);
            }
        }, 5);
    }

    private void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void setUpMediaProjection() {
        if (mResultData == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            mContext.startActivity(intent);
        } else {
            if (mMediaProjection == null) {
                mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
            }

        }
    }

    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void startCapture(ShotListener mShotListener) {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            startScreenShot(mShotListener);
        } else {
            Bitmap bitmap = covetBitmap(image);
            if (bitmap != null) {
                mShotListener.OnSuccess(bitmap);
                stopVirtual();
            }
        }
    }

    private Bitmap covetBitmap(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        //每个像素的间距
        int pixelStride = planes[0].getPixelStride();
        //总的间距
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();
        return bitmap;
    }

    public void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        tearDownMediaProjection();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    public interface ShotListener {

        void OnSuccess(Bitmap bitmap);
    }
}


