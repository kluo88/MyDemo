package com.itkluo.demo.camera;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.itkluo.demo.utils.AppConfig;
import com.itkluo.demo.utils.IntentKey;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity2  extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1024;

    public static void start(AppCompatActivity activity) {
        start(activity, false);
    }

    public static void start(AppCompatActivity activity, boolean video) {
        File file = createCameraFile(video);
        Intent intent = new Intent(activity, CameraActivity2.class);
        intent.putExtra(IntentKey.FILE, file);
        intent.putExtra(IntentKey.VIDEO, video);
        activity.startActivityForResult(intent, 100);
    }

    private File mFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData() {
        Intent intent;

        // 启动系统相机
        if (getIntent().getExtras().getBoolean(IntentKey.VIDEO, false)) {
            // 录制视频
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        } else {
            // 拍摄照片
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        if (XXPermissions.isHasPermission(this, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.CAMERA)
                && intent.resolveActivity(getPackageManager()) != null) {
            mFile = (File) getIntent().getExtras().getSerializable(IntentKey.FILE);
            if (mFile != null && mFile.exists()) {

                Uri imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 通过 FileProvider 创建一个 Content 类型的 Uri 文件
                    imageUri = FileProvider.getUriForFile(this, AppConfig.getPackageName() + ".provider", mFile);
                } else {
                    imageUri = Uri.fromFile(mFile);
                }
                // 对目标应用临时授权该 Uri 所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 将拍取的照片保存到指定 Uri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                // 拍摄的视频可设置
//               MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
//                MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
//                MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(CameraActivity2.this, "目标地址错误", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(CameraActivity2.this, "无法启动相机", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    // 重新扫描多媒体（否则可能扫描不到）
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mFile.getPath()}, null, null);
                    cryptFile();
                    break;
                case RESULT_CANCELED:
                    // 删除这个文件
                    mFile.delete();
                    break;
                default:
                    break;
            }
            setResult(resultCode);
            finish();
        }
    }

    private void cryptFile() {
    }

    /**
     * 创建一个拍照图片文件对象
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File createCameraFile(boolean video) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        if (!folder.exists() || !folder.isDirectory()) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory();
            }
        }

        try {
            File file = new File(folder, (video ? "IMG_" : "VID") + new SimpleDateFormat("_yyyyMMdd_HHmmss.", Locale.getDefault()).format(new Date()) + (video ? "mp4" : "jpg"));
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
