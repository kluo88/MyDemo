package com.itkluo.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.camera.view.CameraTextureView;

/**
 * 布局中嵌入摄像头预览View
 *
 * @author luobingyong
 * @date 2020/10/22
 */
public class CameraMonitorActivity extends AppCompatActivity {

    private CameraTextureView mCameraView;
    private ImageButton btnTakePic;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_monitor);
        btnTakePic = (ImageButton) findViewById(R.id.btnTakePic);
        etPassword = findViewById(R.id.etPassword);
        mCameraView = findViewById(R.id.cameraView);
        mCameraView.startPreview();
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
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.release();
    }
}
