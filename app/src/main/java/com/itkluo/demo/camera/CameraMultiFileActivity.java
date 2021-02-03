package com.itkluo.demo.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.itkluo.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 调系统相机拍多张图片
 */
public class CameraMultiFileActivity extends AppCompatActivity {
    private Button bt;
    private List<Map<String, Bitmap>> list;
    private GridView gridView;
    private View pb;
    private FragmentActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        bt = (Button) findViewById(R.id.query);
        pb = findViewById(R.id.pb);
        gridView = ((GridView) findViewById(R.id.ryMain));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XXPermissions.with(mActivity)
                        .permission(com.hjq.permissions.Permission.CAMERA)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    // 打开相机拍照
                                    takeOnCamera();
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                if (never) {
                                    ToastUtils.show("您拒绝了打开相机的权限，无法完成");
                                    // gotoSetting();
                                    XXPermissions.gotoPermissionSettings(mActivity);
                                } else {
                                    ToastUtils.show("您拒绝了打开相机的权限，无法完成");
                                }

                            }
                        });
            }
        });

    }

    public void takeOnCamera() {
        GetCameraFileUtils.takeOnCamera(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("data", "onActivityResult: " + data);
        //关闭相机之后获得时间；2；
        pb.setVisibility(View.VISIBLE);
        if (requestCode == GetCameraFileUtils.TAKE_ON_CAMERA) {
            //这里可以拓展不同按钮，给下面的方法传不同的参数
            getFileList(System.currentTimeMillis());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * @param endStamp 结束拍照的时间   13位(到毫秒)
     */
    private void getFileList(final long endStamp) {
        XXPermissions.with(mActivity)
                .permission(com.hjq.permissions.Permission.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
                            //  读取照片然后选择合适的照片保存再list里面
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    List<MyBitmap> list2 = getContentProvider(endStamp);

                                    Log.e("list", "call: " + list2.toString() + ".size" + list2.size());
                                    if (list2 != null) {
                                        if (list2.size() > 7) {//这里看要求最多几张照片
                                            list2 = list2.subList(list2.size() - 7, list2.size());
                                        }
                                        final List<MyBitmap> finalList = list2;
                                        gridView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                //拿到数据源然后拓展
                                                gridView.setAdapter(new Myadapter(mActivity, finalList));//拿到了就可以搞了
                                                pb.setVisibility(View.GONE);
                                            }
                                        });

                                    }

                                }
                            }).start();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {
                        if (never) {
                            ToastUtils.show("您拒绝了读取照片的权限");
                            // gotoSetting();
                            XXPermissions.gotoPermissionSettings(mActivity);
                        } else {
                            ToastUtils.show("您拒绝了读取照片的权限");
                        }

                    }
                });
    }

    /**
     * 获取ContentProvider
     *
     * @return
     */
    public List<MyBitmap> getContentProvider(long endStamp) {
        List<MyBitmap> lists = new ArrayList<MyBitmap>();

        List<String> cameraFile = GetCameraFileUtils.getCameraFile(this, endStamp);

        for (String strings : cameraFile) {
            Log.e("setsize", "getContentProvider: " + strings);
            try {
                Bitmap bitmap = convertToBitmap(strings, 300, 300);

                MyBitmap myBitmap = new MyBitmap(strings, bitmap);
                lists.add(myBitmap);
            } catch (Exception e) {
                Log.e("exceptionee", "getSystemTime: " + e.toString());

            }

        }

        return lists;
    }

    /**
     * 根据路径，二次采样并且压缩
     *
     * @param filePath   路径
     * @param destWidth  压缩到的宽度
     * @param destHeight 压缩到的高度
     * @return
     */
    public Bitmap convertToBitmap(String filePath, int destWidth, int destHeight) {
        //第一采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        int sampleSize = 1;
        while ((outWidth / sampleSize > destWidth) || (outHeight / sampleSize > destHeight)) {

            sampleSize *= 2;
        }
        //第二次采样
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(filePath, options);
    }

}
