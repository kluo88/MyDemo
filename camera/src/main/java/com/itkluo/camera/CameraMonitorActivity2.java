package com.itkluo.camera;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itkluo.camera.view.Camera2ViewLayout;

/**
 * 布局中嵌入摄像头预览View
 * 使用 自定义控件 {@link Camera2ViewLayout}
 * @author luobingyong
 * @date 2020/10/22
 */
public class CameraMonitorActivity2 extends AppCompatActivity {

    private Camera2ViewLayout mCameraView;
    private ImageButton btnTakePic;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_monitor2);
        btnTakePic = (ImageButton) findViewById(R.id.btnTakePic);
        etPassword = findViewById(R.id.etPassword);
        mCameraView = findViewById(R.id.cameraView);
//        mCameraView.startPreview();
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraView.takePic();
            }
        });
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {//敲击回车键后调用
                if (!"1234".equals(etPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "密码:" +
                            etPassword.getText().toString() + " 错误！", Toast.LENGTH_LONG).show();
                    mCameraView.takePic();
                }
                return true;
            }
        });
//        etPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                tv_show.setText(editable.toString().trim());
//            }
//        });
//    }
}

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }

    public void startPreview(View view) {
        mCameraView.startPreview();
    }

    public void stopPreview(View view) {
        mCameraView.pause();
    }

    public void destroyCamera(View view) {
        mCameraView.destroy();
    }
}
