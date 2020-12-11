package com.itkluo.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itkluo.demo.aidl.ClientActivity2;
import com.itkluo.demo.api.CameraActivity;
import com.itkluo.demo.apk.GetApkFileInfoActivity;
import com.itkluo.demo.exam.ProgressActivity;
import com.itkluo.demo.exam.bezier.BezierMain;
import com.itkluo.demo.exam.scrollviewswipe.ScrollViewInterceptMain;
import com.itkluo.demo.exam.touchEvent.sample2.ViewPagerListViewTouchActivity;
import com.itkluo.demo.hook.HookActivity;
import com.itkluo.demo.java.list.SpecInfo;
import com.itkluo.demo.model.GoodsDetailBean;
import com.itkluo.demo.sernsor.SensorSampleActivity;
import com.itkluo.demo.system.ShotUtils;
import com.itkluo.demo.tomcat.IndexActivity;
import com.itkluo.demo.tts2.VoicePlayActivity;
import com.itkluo.demo.usb.myhid.UsbConnectHidActivity;
import com.itkluo.demo.usb.wdreader.WdSSCardActivity;
import com.itkluo.demo.utils.VibrateAndToneUtil;
import com.itkluo.demo.widget.GoodRulePopupWindow;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Shut up and show me the code
 */
public class DemoListActivity extends AppCompatActivity {
    private static final String TAG = "DemoListActivity";
    private ViewGroup mainLayout;
    private AppCompatActivity mActivity;
    private int itemCount;

    private boolean isJustShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = DemoListActivity.this;
        if (isJustShow) {
            //前后摄像头
            startActivity(new Intent(mActivity, CameraActivity.class));
            finish();
            return;
        }

        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_demo_list);

        requestPermissions();

        ListView listView = (ListView) findViewById(R.id.listView);
        mainLayout = findViewById(R.id.mainLayout);
        String[] values = {"使用Binder进行IPC通信", "使用AIDL进行IPC通信", "图片轮播", "ViewPage列表中gridview", "下拉级联菜单", "点击箭头显示下拉菜单", "ConstraintLayout嵌套在ScrollView里面"
                , "CoordinatorLayout嵌套滑动", "CoordinatorLayout嵌套ListView", "可扩展收缩的FlowLayout", "过度绘制布局(设置/辅助功能/开发者选项/，打开调试GPU过度绘制选项）", "内存MAT分析",
                "伸缩TextView--CollapsibleTextView", "测试 Demo", "改造系统TabLayout", "抢购倒计时", "商品规格选择弹窗", "点击右上角弹出下拉菜单", "RxJava操作符", "使用用TomCat实现软件的版本检测"
                , "获取路径下未安装的apk信息", "跳转到veb应用商店的搜索页面", "传感器", "震动和提示音", "卡顿检测工具BlockCanary", "截图", "获取手机信息"
                , "系统信息", "二维码", "NFC", "启动其他App", "MediaPlayer拼接播放数字语音", "SoundPool拼接播放数字语音", "发广播激活百度ota", "USB", "前后摄像头", "Hook入门"
                , "ScrollView嵌套拦截", "贝塞尔曲线", "Android USB Host与HID设备通信","ViewPager嵌套ListView滑动冲突测试"
        };

        //自定义View https://github.com/18598925736/UiDrawTest  https://www.jianshu.com/p/8ee2acc24755
        //多渠道 https://github.com/18598925736/GradleStudy1021 https://www.jianshu.com/p/d9b0e19ee1d8
        //https://www.jianshu.com/p/94933c63ecb2  https://github.com/18598925736/EnjoyGradleHank/commits/master
        //AOP优雅权限框架 https://github.com/18598925736/GracefulPermissionFramework/tree/dev_aspectJ https://www.jianshu.com/p/f53e519505c9
        //MVP https://www.jianshu.com/p/47ce942ab3cc https://github.com/18598925736/MvpDemo
        //MVVM https://www.jianshu.com/p/62b4833172fb

        //串口库和USB库
        //https://github.com/WuSG2016/McLibrary
        //https://github.com/Geek8ug/Android-SerialPort
        //https://juejin.im/post/6844903870368317447

        itemCount = values.length;

        //List<String> list = Arrays.asList(values);
        //Arrays.asList(values)返回的是一个只读的List，不能进行add和remove
        //new ArrayList<>(Arrays.asList(values))则是一个可写的List，可以进行add和remove
        List<String> list = new ArrayList<>(Arrays.asList(values));
        Collections.reverse(list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                int index = itemCount - 1 - position;
                switch (index) {
                    case 0:
//                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ClientActivity.class));
                        SpecInfo.jsonToMapTest();
                        break;
                    case 1:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ClientActivity2.class));
                        break;
                    case 2:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, VpIndicateActivity.class));
                        break;
                    case 3:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ViewpagerGridMenuActivity.class));
                        break;
                    case 4:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, CascadingMenuActivity.class));
                        break;
                    case 5:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, DownMenuActivity.class));
                        break;
                    case 6:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ConstraintLayoutActivity.class));
                        break;
                    case 7:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, CoordinatorLayoutActivity.class));
                        break;
                    case 8:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, CoordinatorListViewActivity.class));
                        break;
                    case 9:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, FlowLayoutActivity.class));
                        break;
                    case 10:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, OverDrawActivity.class));
                        break;
                    case 11:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, MemoryActivity.class));
                        break;
                    case 12:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, CollapsibleTextViewActivity.class));
                        break;
                    case 13:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ProgressActivity.class));
                        break;
                    case 14:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, MyTabLayoutActivity.class));
                        break;
                    case 15:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, CountDownTimerViewActivity.class));
                        break;
                    case 16:
                        initJsonData();
                        showPopupwindow();
                        break;
                    case 17:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, TitleDownMenuActivity.class));
                        break;
                    case 18:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, RxJavaDemoAct.class));
                        break;
                    case 19:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, IndexActivity.class));
                        break;
                    case 20:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, GetApkFileInfoActivity.class));
                        break;
                    case 21:
                        search(mActivity, "腾讯", "");
                        //方式二 目标在 AnroidManifest 文件中给 activity 节点设置 Android:exported="true"，
//                        Intent intent = new Intent();
//                        intent.setClassName("com.vebos.appstore", "com.vebos.appstore.ui.search.SearchMainActivity");
//                        startActivity(intent);
                        break;
                    case 22:
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, SensorSampleActivity.class));
                        break;
                    case 23:
                        VibrateAndToneUtil.getInstance().vibrateAndPlayTone();
                        break;
                    case 24://卡顿模拟
                        System.out.println("Start test BlockCanary");
                        SystemClock.sleep(10_000);
                        System.out.println("处理其他业务逻辑");
                        break;
                    case 25:
//                        Intent intent = new Intent(mActivity, ScreenShotService.class);
//                        startService(intent);
                        ShotUtils.getInstance().startScreenShot(new ShotUtils.ShotListener() {
                            @Override
                            public void OnSuccess(Bitmap bitmap) {

                            }
                        });
                        break;
                    case 26://获取手机信息
                        mActivity.startActivity(new Intent(mActivity, PhoneInfoActivity.class));
                        break;
                    case 27://获取系统信息
                        mActivity.startActivity(new Intent(mActivity, AppInfoActivity.class));
                        break;
                    case 28:
                        //二维码  http://wuxiaolong.me/2016/04/22/zxing/
                        break;
                    case 29:
                        mActivity.startActivity(new Intent(mActivity, NFCCheckActivity.class));
                        break;
                    case 30:
                        startApp(mActivity, "com.veb.facecheck");
                        break;
                    case 31:
                        //TTS
                        List<Integer> list1 = new ArrayList<>();
                        list1.add(R.raw.mp3_key_code_1);
                        list1.add(R.raw.mp3_unit_bai);
                        list1.add(R.raw.mp3_key_code_2);
                        list1.add(R.raw.mp3_unit_shi);
                        //MediaPlayer播放   可参考 https://blog.csdn.net/qq_34908107/article/details/76423519
//                        VoiceUtil1.getInstance().play(list1);

                        Intent intent1 = new Intent(mActivity, VoicePlayActivity.class);
                        intent1.putExtra("type", 0);
                        mActivity.startActivity(intent1);
                        break;
                    case 32:
                        //TTS
                        //SoundPool播放   待参考优化 https://github.com/jiangkang/KTools/tree/master/app/src/main/java/com/jiangkang/ktools/audio
//                        SoundPoolUtil.getInstance().play(R.raw.mp3_key_code_1
//                                , R.raw.mp3_unit_bai, R.raw.mp3_key_code_2
//                                , R.raw.mp3_unit_shi, R.raw.mp3_key_code_8
////                                , R.raw.mp3_key_code_dot, R.raw.mp3_key_code_5, R.raw.mp3_key_code_6, R.raw.mp3_unit_yuan
//                        );

                        Intent intent2 = new Intent(mActivity, VoicePlayActivity.class);
                        intent2.putExtra("type", 1);
                        mActivity.startActivity(intent2);
                        break;

                    //另外一种优化，支付宝使用SequenceInputStream合并几个音频播放，解决播放的衔接停顿
                    case 33:
                        //发广播激活百度ota
                        Intent intent = new Intent("action_init_bai_du_ota");
//                        intent.setPackage(mActivity.getPackageName());
                        mActivity.sendBroadcast(intent);
                        break;
                    case 34:
                        //USB
                        startActivity(new Intent(mActivity, WdSSCardActivity.class));
                        break;
                    case 35:
                        //前后摄像头
                        startActivity(new Intent(mActivity, CameraActivity.class));
                        break;
                    case 36:
                        //Hook入门
                        startActivity(new Intent(mActivity, HookActivity.class));
                        break;
                    case 37:
                        //ScrollView嵌套拦截
                        startActivity(new Intent(mActivity, ScrollViewInterceptMain.class));
                        break;
                    case 38:
                        //贝塞尔曲线
                        startActivity(new Intent(mActivity, BezierMain.class));
                        break;
                    case 39:
                        //Android USB Host与HID设备通信 https://blog.csdn.net/qweadf1/article/details/41646439
                        //https://www.jianshu.com/p/e2e57cddac6a
                        startActivity(new Intent(mActivity, UsbConnectHidActivity.class));
                        break;
                    case 40://ViewPager嵌套ListView滑动冲突测试
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ViewPagerListViewTouchActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
        //内容泄露示例,  旋转屏幕观察Android monitor内存情况
//        LeakCauseSample instance = LeakCauseSample.getInstance(this);
        //Android 5.0以上 截图工具，先要弹窗由用户授权截取屏幕
//        ShotUtils.getInstance().init(mActivity);
    }

    private int getRawFileVoiceTime(int rawId) {
        int duration = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + rawId);
            mediaPlayer.setDataSource(mActivity, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
//            file.close();

            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }


    /**
     * start app
     *
     * @param context
     * @param packageName
     */
    public static void startApp(@NonNull Context context, @NonNull String packageName) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "startApp, Package does not exist.");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ShotUtils.REQUEST_MEDIA_PROJECTION:
                // 得到截取授权，保存起来
                ShotUtils.getInstance().setData(data);
                break;
            default:
                break;
        }
    }

    /**
     * 应用商店的搜索页面
     *
     * @param context
     * @param keyword     关键字搜索
     * @param packageName
     */
    public static void search(Context context, String keyword, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction("com.vebos.appstore.search");
            intent.putExtra("keyword", keyword);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//        intent.putExtra("packageName", packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GoodsDetailBean data;
    private GoodRulePopupWindow rulePopup;

    private void initJsonData() {
        String jsonString = null;
        try {
            InputStream is = getAssets().open("specs.json");//打开json数据
            byte[] by = new byte[is.available()];//转字节
            is.read(by);
            jsonString = new String(by, "utf-8");
            is.close();//关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        data = gson.fromJson(jsonString, GoodsDetailBean.class);
    }


    private void showPopupwindow() {
        if (rulePopup == null) {
            rulePopup = new GoodRulePopupWindow(this, data);
            //设置是否遮住状态栏
            fitPopupWindowOverStatusBar(rulePopup, true);
            //设置关闭监听，当PopupWindow关闭，背景恢复1f
            rulePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
//                darkenBackgroud(1f);
                }
            });

        }
        //设置显示位置（从底部弹出）
        rulePopup.showAtLocation(mainLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //当弹出Popupwindow时，背景变半透明
//           darkenBackgroud(0.4f);
    }

    //弹出的窗口是否覆盖状态栏
    public void fitPopupWindowOverStatusBar(PopupWindow popupWindow, boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                //利用反射重新设置mLayoutInScreen的值，当mLayoutInScreen为true时则PopupWindow覆盖全屏。
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(popupWindow, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(this);
        Disposable subscribe = rxPermission.requestEach(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.i(TAG, permission.name + " is granted.");
//                            Toast.makeText(mActivity, permission.name + " is granted.", Toast.LENGTH_LONG).show();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e(TAG, permission.name + " is denied. More info should be provided.");
                            Toast.makeText(mActivity, permission.name + " is denied. More info should be provided.", Toast.LENGTH_LONG).show();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e(TAG, permission.name + " is denied.");
                            Toast.makeText(mActivity, permission.name + " is denied.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    /**
     * 多渠道维度获取
     * 从manifest中读取出metadata
     */
    private void getMetaData() {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_META_DATA);
            Bundle bd = appInfo.metaData;
            String client = bd.getString("CLIENT");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
