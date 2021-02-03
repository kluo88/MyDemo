package com.itkluo.camera.camera2;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itkluo.camera.R;
import com.itkluo.camera.view.FaceView;

/**
 * @author luobingyong
 * @date 2020/10/21
 */
public class CameraActivity2 extends AppCompatActivity {
    private TextureView textureView;
    private FaceView faceView;
    private RelativeLayout rlBottom;
    private ImageView ivSetting;
    private ImageButton btnTakePic;
    private ImageView btnStart;
    private ImageView btnStop;
    private ImageView ivExchange;
    private Camera2Helper mCamera2Helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        mCamera2Helper = new Camera2Helper(this, textureView);
        mCamera2Helper.openCamera();
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera2Helper.takePic();
            }
        });
        ivExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera2Helper.exchangeCamera();
            }
        });
    }

    private void initView() {
        textureView = (TextureView) findViewById(R.id.textureView);
        faceView = (FaceView) findViewById(R.id.faceView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        btnTakePic = (ImageButton) findViewById(R.id.btnTakePic);
        btnStart = (ImageView) findViewById(R.id.btnStart);
        btnStop = (ImageView) findViewById(R.id.btnStop);
        ivExchange = (ImageView) findViewById(R.id.ivExchange);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera2Helper.release();
    }
}
