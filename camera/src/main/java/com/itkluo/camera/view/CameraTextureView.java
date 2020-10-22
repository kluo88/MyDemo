package com.itkluo.camera.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.itkluo.camera.camera2.Camera2Helper;

/**
 * Camera2 相机 View
 *
 * @author luobingyong
 * @date 2020/10/22
 */
public class CameraTextureView extends TextureView {
    private Context mContext;
    private Activity mActivity;
    private Camera2Helper mCamera2Helper;

    public CameraTextureView(Context context) {
        this(context, null);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        if (mContext instanceof Activity) {
            mActivity = ((Activity) mContext);
            mCamera2Helper = new Camera2Helper(mActivity, this);
            mCamera2Helper.setDefaultCameraFacing(0);
            mCamera2Helper.setShowToast(true);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (mCamera2Helper != null) {
            mCamera2Helper.openCamera();
        }
    }

    /**
     * 拍照
     */
    public void takePic() {
        if (mCamera2Helper != null) {
            mCamera2Helper.takePic();
        }
    }

    public void pause() {

    }

    /**
     * 释放摄像头
     */
    public void release() {
        if (mCamera2Helper != null) {
            mCamera2Helper.release();
        }
    }


}
