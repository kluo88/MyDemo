package com.itkluo.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.itkluo.camera.camera1.CameraActivity;
import com.itkluo.camera.camera2.CameraActivity2;

import java.util.List;

/**
 * https://www.jianshu.com/p/0ea5e201260f
 * https://github.com/smashinggit/Study
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button mBtCapture;
    private Button mBtCamera;
    private Button mBtCameraRecord;
    private Button mBtCamera2;
    private Button mBtCamera2Face;
    private Button mBtMonitor;
    private Button mBtMonitor2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        mBtCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(Intent(this, CaptureActivity.class));
            }
        });
        mBtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.TYPE_TAG, CameraActivity.TYPE_CAPTURE);
                startActivity(intent);
            }
        });
        mBtCameraRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.TYPE_TAG, CameraActivity.TYPE_RECORD);
                startActivity(intent);
            }
        });
        mBtCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity2.class);
                startActivity(intent);
            }
        });
        mBtMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraMonitorActivity.class);
                startActivity(intent);
            }
        });
        mBtMonitor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraMonitorActivity2.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        XXPermissions.with(MainActivity.this)
                // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .constantRequest()
                .permission(Permission.Group.STORAGE)
                .permission(Permission.READ_PHONE_STATE)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        Log.i(TAG, "权限请求被拒绝了,不能继续依赖该权限的相关操作了，展示setting ");

                    }
                });
    }

    private void initView() {
        mBtCapture = (Button) findViewById(R.id.btCapture);
        mBtCamera = (Button) findViewById(R.id.btCamera);
        mBtCameraRecord = (Button) findViewById(R.id.btCameraRecord);
        mBtCamera2 = (Button) findViewById(R.id.btCamera2);
        mBtCamera2Face = (Button) findViewById(R.id.btCamera2Face);
        mBtMonitor = (Button) findViewById(R.id.btMonitor);
        mBtMonitor2 = (Button) findViewById(R.id.btMonitor2);
    }
}
