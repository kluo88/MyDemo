package com.itkluo.camera.camera1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itkluo.camera.R;
import com.itkluo.camera.util.BitmapUtils;
import com.itkluo.camera.util.FileUtil;
import com.itkluo.camera.util.IoUtils;
import com.itkluo.camera.view.FaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author luobingyong
 * @date 2020/10/19
 */
public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    public static String TYPE_TAG = "type";
    public static int TYPE_CAPTURE = 0;
    public static int TYPE_RECORD = 1;

    private boolean lock = false; //控制MediaRecorderHelper的初始化

    private CameraHelper mCameraHelper;
    private MediaRecorderHelper mMediaRecorderHelper;
    private SurfaceView surfaceView;
    private FaceView faceView;
    private RelativeLayout rlBottom;
    private ImageView ivSetting;
    private ImageButton btnTakePic;
    private ImageView btnStart;
    private ImageView btnStop;
    private ImageView ivExchange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        mCameraHelper = new CameraHelper(this, surfaceView);
        mCameraHelper.addCallBack(new CameraHelper.CallBack() {
            @Override
            public void onPreviewFrame(byte[] data) {
                if (!lock) {
                    if (mCameraHelper.getCamera() != null) {
                        mMediaRecorderHelper = new MediaRecorderHelper(CameraActivity.this,
                                mCameraHelper.getCamera(), mCameraHelper.mDisplayOrientation, mCameraHelper.mSurfaceHolder.getSurface());
                    }
                    lock = true;
                }
            }

            @Override
            public void onTakePic(byte[] data) {
                savePic(data);
                btnTakePic.setClickable(true);
            }

            @Override
            public void onFaceDetect(ArrayList<RectF> faces) {
                faceView.setFaces(faces);
            }
        });

        if (getIntent().getIntExtra(TYPE_TAG, 0) == TYPE_RECORD) { //录视频
            btnTakePic.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        }

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelper.takePic();
            }
        });

        ivExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraHelper.exchangeCamera();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivExchange.setClickable(false);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                if (mMediaRecorderHelper != null) {
                    mMediaRecorderHelper.startRecord();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivExchange.setClickable(true);
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                if (mMediaRecorderHelper != null) {
                    mMediaRecorderHelper.stopRecord();
                }
            }
        });

    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        faceView = (FaceView) findViewById(R.id.faceView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        btnTakePic = (ImageButton) findViewById(R.id.btnTakePic);
        btnStart = (ImageView) findViewById(R.id.btnStart);
        btnStop = (ImageView) findViewById(R.id.btnStop);
        ivExchange = (ImageView) findViewById(R.id.ivExchange);
    }

    private void savePic(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedOutputStream bos = null;
                try {
                    final long temp = System.currentTimeMillis();
                    final File picFile = FileUtil.createCameraFile("camera1");
                    if (picFile != null && data != null) {
                        Bitmap rawBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap resultBitmap;
                        if (mCameraHelper.mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            resultBitmap = BitmapUtils.mirror(BitmapUtils.rotate(rawBitmap, 270f));
                        } else {
                            resultBitmap = BitmapUtils.rotate(rawBitmap, 90f);
                        }
//                            picFile.sink().buffer().write(BitmapUtils.toByteArray(resultBitmap)).close()
                        bos = new BufferedOutputStream(new FileOutputStream(picFile));
                        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraActivity.this, "图片已保存! " + picFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                Log.d(TAG, "图片已保存! 耗时：" + (System.currentTimeMillis() - temp) + "    路径：  " + picFile.getAbsolutePath());
                                BitmapUtils.scanPhoto(CameraActivity.this, picFile.getAbsolutePath());
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "保存图片失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    IoUtils.closeIo(bos);
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        mCameraHelper.releaseCamera();
        if (mMediaRecorderHelper != null) {
            if (mMediaRecorderHelper.isRunning)
                mMediaRecorderHelper.stopRecord();
            mMediaRecorderHelper.release();
        }
        super.onDestroy();
    }

}
