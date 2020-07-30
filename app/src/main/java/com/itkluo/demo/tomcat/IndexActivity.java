package com.itkluo.demo.tomcat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.functions.Consumer;

/**
 * 首页
 * https://www.jianshu.com/p/80f44f051aa4
 * Android服务器——使用TomCat实现软件的版本检测，升级，以及下载更新进度！
 * <p>
 * ps: 手机端连接上,  Tomcat的电脑要关闭公网防火墙 .控制面板–>系统和安全–>Windows防火墙–>启用或关闭Windows防火墙–>关闭
 *
 * @author LGL
 */
public class IndexActivity extends Activity implements OnClickListener {
    private static final String TAG = "IndexActivity";
    // 更新
    private static final int UPDATE_YES = 1;
    // 不更新
    private static final int UPDATE_NO = 2;
    // URL错误
    private static final int URL_ERROR = 3;
    // 没有网络
    private static final int IO_ERROR = 4;
    // 数据异常
    private static final int JSON_ERROR = 5;

    private TextView tv_version;
    private PackageInfo packageInfo;

    private JSONObject jsonObject;
    private String versionName;
    private int versionCode;
    private String content;
    private String url;

    private TextView tv_pro;

    // 升级提示框
    private CustomDialog updateDialog;
    private TextView dialog_update_content;
    private TextView dialog_confrim;
    private TextView dialog_cancel;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_YES:
                    showUpdateDialog();
                    break;
                case UPDATE_NO:
                    goHome();
                    break;
                case URL_ERROR:
                    Toast.makeText(getApplicationContext(), "地址错误",
                            Toast.LENGTH_LONG).show();
                    goHome();
                    break;
                case IO_ERROR:
                    Toast.makeText(getApplicationContext(), "请检查网络",
                            Toast.LENGTH_LONG).show();
                    goHome();
                    break;
                case JSON_ERROR:
                    Toast.makeText(getApplicationContext(), "Json解析错误",
                            Toast.LENGTH_LONG).show();
                    goHome();
                    break;
                // 就算你报任何错，爸比是爱你的，依然让你进主页面
            }
        }
    };
    private String path;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_index);
        initView();
        getJSON();
    }

    /**
     * 初始化控件
     *
     * @author LGL
     */
    private void initView() {
        tv_pro = (TextView) findViewById(R.id.tv_pro);
        tv_version = (TextView) findViewById(R.id.tv_version);
        // 设置版本号
        tv_version.setText("版本号：" + getAppVersion());
    }

    /**
     * 获取APP版本号
     *
     * @return
     */
    private String getAppVersion() {
        try {
            // PackageManager管理器
            PackageManager pm = getPackageManager();
            // 获取相关信息
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本名称
            String name = packageInfo.versionName;
            // 版本号
            int version = packageInfo.versionCode;

            Log.i("版本信息", "版本名称：" + name + "版本号" + version);

            return name;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        // 如果出现异常抛出
        return "无法获取";
    }

    /**
     * 解析JSON
     */
    private void getJSON() {
        // 子线程访问，耗时操作
        new Thread() {
            public void run() {

                Message msg = Message.obtain();

                // 开始访问网络的时间
                long startTime = System.currentTimeMillis();

                try {
                    // JSON地址
                    HttpURLConnection conn = (HttpURLConnection) new URL(
                            // 模拟器一般有一个预留IP：10.0.2.2
                            "http://192.168.3.169:8080/luo/update.json")
//                            "http://localhost:8080/luo/update.json")
                            .openConnection();
                    // 请求方式GRT
                    conn.setRequestMethod("GET");
                    // 连接超时
                    conn.setConnectTimeout(5000);
                    // 响应超时
                    conn.setReadTimeout(3000);
                    // 连接
                    conn.connect();
                    // 获取请求码
                    int responseCode = conn.getResponseCode();
                    // 等于200说明请求成功
                    if (responseCode == 200) {
                        // 拿到他的输入流
                        InputStream in = conn.getInputStream();
                        String stream = Utils.toStream(in);

                        Log.i("JSON", stream);
                        jsonObject = new JSONObject(stream);
                        versionName = jsonObject.getString("versionName");
                        versionCode = jsonObject.getInt("versionCode");
                        content = jsonObject.getString("content");
                        url = jsonObject.getString("url");

                        // 版本判断
                        if (versionCode > getCode()) {
                            // 提示更新
                            msg.what = UPDATE_YES;
                        } else {
                            // 不更新，跳转到主页
                            msg.what = UPDATE_NO;
                        }
                    }

                } catch (MalformedURLException e) {
                    // URL错误
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    // 没有网络
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    // 数据错误
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {

                    // 网络访问结束的时间
                    long endTime = System.currentTimeMillis();
                    // 计算网络用了多少时间
                    long time = endTime - startTime;

                    try {
                        if (time < 3000) {
                            // 停留三秒钟
                            Thread.sleep(3000 - time);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 全部走完发消息
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 获取versionCode
     */
    private int getCode() {
        // PackageManager管理器
        PackageManager pm = getPackageManager();
        // 获取相关信息
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            // 版本号
            int version = packageInfo.versionCode;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 升级弹框
     */
    private void showUpdateDialog() {
        updateDialog = new CustomDialog(this, 0, 0, R.layout.dialog_update,
                R.style.Theme_dialog, Gravity.CENTER, 0);
        //如果他点击其他地方，不安装，我们就直接去
        updateDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                goHome();
            }
        });
        // 更新内容
        dialog_update_content = (TextView) updateDialog
                .findViewById(R.id.dialog_update_content);
        dialog_update_content.setText(content);
        // 确定更新
        dialog_confrim = (TextView) updateDialog
                .findViewById(R.id.dialog_confrim);
        dialog_confrim.setOnClickListener(this);
        // 取消更新
        dialog_cancel = (TextView) updateDialog
                .findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(this);
        updateDialog.show();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_confrim:
                updateDialog.dismiss();

                RxPermissions rxPermission = new RxPermissions(this);
                rxPermission
                        .requestEach(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                               )
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    // 用户已经同意该权限
                                    Log.i(TAG, permission.name + " is granted.");
                                    Toast.makeText(context, permission.name + " is granted.", Toast.LENGTH_LONG).show();
                                    downloadAPK();
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



                break;
            case R.id.dialog_cancel:
                // 跳主页面
                goHome();
                break;
        }
    }

    /**
     * 跳转主页面
     */
    private void goHome() {
        Toast.makeText(this, "跳转到主页", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 下载更新
     */
    private void downloadAPK() {
        tv_pro.setVisibility(View.VISIBLE);
        // 判断是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/MyDemo.apk";
            HttpUtils httpUtils = new HttpUtils();
            /**
             * 1.网络地址 2.存放地址 3.回调
             */
            httpUtils.download(url, path, new RequestCallBack<File>() {

                // 下载进度
                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.i("luo", "下载进度: " + 100 * current / total + "%");
                    // 显示进度
                    tv_pro.setText(100 * current / total + "%");

                }

                // 成功
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    // 跳转系统安装页面
//                    Intent i = new Intent();
//                    i.setAction(Intent.ACTION_VIEW);
//                    i.addCategory(Intent.CATEGORY_DEFAULT);
//                    i.setDataAndType(Uri.fromFile(new File(path)),
//                            "application/vnd.android.package-archive");
//                    startActivity(i);
//                    installApk(path);
                }

                // 失败
                @Override
                public void onFailure(HttpException error, String msg) {
                    Log.i("error", msg);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "未找到SD卡", Toast.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * 安装apk
     *
     * @param fileSavePath
     */
    private void installApk(String fileSavePath) {
//        File file = new File(fileSavePath);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri data;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
//            // "sven.com.fileprovider.fileprovider"即是在清单文件中配置的authorities
//            // 通过FileProvider创建一个content类型的Uri
//            data = FileProvider.getUriForFile(this, "sven.com.fileprovider.fileprovider", file);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
//        } else {
//            data = Uri.fromFile(file);
//        }
//        intent.setDataAndType(data, "application/vnd.android.package-archive");
//        startActivity(intent);
    }



}
