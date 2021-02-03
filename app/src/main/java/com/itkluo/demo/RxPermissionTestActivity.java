package com.itkluo.demo;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class RxPermissionTestActivity extends AppCompatActivity {

//    Manifest.permission.ACCESS_FINE_LOCATION,
//    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//    Manifest.permission.READ_CALENDAR,
//    Manifest.permission.READ_CALL_LOG,
//    Manifest.permission.READ_CONTACTS,
//    Manifest.permission.READ_PHONE_STATE,
//    Manifest.permission.READ_SMS,
//    Manifest.permission.RECORD_AUDIO,

    private static final String TAG = "RxPermissionTest";
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_permission_test);
        context = this;
        requestPermissions();
    }

    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(RxPermissionTestActivity.this);
        rxPermission
                .requestEach(
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.i(TAG, permission.name + " is granted.");
                            Toast.makeText(context, permission.name + " is granted.", Toast.LENGTH_LONG).show();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e(TAG, permission.name + " is denied. More info should be provided.");
                            Toast.makeText(context, permission.name + " is denied. More info should be provided.", Toast.LENGTH_LONG).show();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e(TAG, permission.name + " is denied.");
                            Toast.makeText(context, permission.name + " is denied.", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    public void testPermission(View view) {
        requestPermissions();
    }

    /**
     * If multiple permissions at the same time, the result is combined :
     * 最后返回统一结果： 通过或拒绝
     */
    public void testPermission2(View view) {
        final String TAG = context.getLocalClassName();
        RxPermissions rxPermission = new RxPermissions(context);
        rxPermission.request(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA
        )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // All requested permissions are granted
                            Log.e(TAG, "all "+ " is granted.");
                        } else {
                            // At least one permission is denied
                            Log.e(TAG, " At least one "+ " is denied.");
                        }
                    }
                });
    }
}
