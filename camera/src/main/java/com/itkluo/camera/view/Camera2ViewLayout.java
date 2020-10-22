package com.itkluo.camera.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.itkluo.camera.R;
import com.itkluo.camera.camera2.Camera2Helper;

/**
 * Camera2 相机 View
 *
 * @author luobingyong
 * @date 2020/10/22
 */
public class Camera2ViewLayout extends RelativeLayout {
    private TextureView mCameraView;
    private FrameLayout mCameraCover;
    private Camera2Helper mCamera2Helper;
    private int mCameraCoverColor;


    public Camera2ViewLayout(Context context) {
        this(context, null);
    }

    public Camera2ViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera2ViewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.camera_textureview_containter, this, true);
        mCameraView = findViewById(R.id.textureView);
        mCameraCover = findViewById(R.id.cameraCover);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Camera2ViewLayout);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.Camera2ViewLayout_cameraCoverBackgroundColor) {
                mCameraCoverColor = array.getColor(attr, 0);
            }
        } //释放资源
        array.recycle();
        if (mCameraCoverColor != 0) {
            mCameraCover.setBackgroundColor(mCameraCoverColor);
        }
        if (context instanceof Activity) {
            Activity mActivity = ((Activity) context);
            mCamera2Helper = new Camera2Helper(mActivity, mCameraView);
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
