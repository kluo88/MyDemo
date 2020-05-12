package com.itkluo.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.itkluo.demo.aidl.ClientActivity2;
import com.itkluo.demo.apk.GetApkFileInfoActivity;
import com.itkluo.demo.exam.ProgressActivity;
import com.itkluo.demo.java.list.SpecInfo;
import com.itkluo.demo.model.GoodsDetailBean;
import com.itkluo.demo.sernsor.SensorSampleActivity;
import com.itkluo.demo.system.ShotUtils;
import com.itkluo.demo.tomcat.IndexActivity;
import com.itkluo.demo.utils.VibrateAndToneUtil;
import com.itkluo.demo.widget.GoodRulePopupWindow;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoListActivity extends AppCompatActivity {
    private static final String TAG = "DemoListActivity";
    private ViewGroup mainLayout;
    private AppCompatActivity mActivity;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_demo_list);
        mActivity = DemoListActivity.this;
        ListView listView = (ListView) findViewById(R.id.listView);
        mainLayout = findViewById(R.id.mainLayout);
        String[] values = {"使用Binder进行IPC通信", "使用AIDL进行IPC通信", "图片轮播", "ViewPage列表中gridview", "下拉级联菜单", "点击箭头显示下拉菜单", "ConstraintLayout嵌套在ScrollView里面"
                , "CoordinatorLayout嵌套滑动", "CoordinatorLayout嵌套ListView", "可扩展收缩的FlowLayout", "过度绘制布局(设置/辅助功能/开发者选项/，打开调试GPU过度绘制选项）", "内存MAT分析",
                "伸缩TextView--CollapsibleTextView", "测试 Demo", "改造系统TabLayout", "抢购倒计时", "商品规格选择弹窗", "点击右上角弹出下拉菜单", "RxJava操作符", "使用用TomCat实现软件的版本检测"
                , "获取路径下未安装的apk信息", "跳转到veb应用商店的搜索页面", "传感器", "震动和提示音", "卡顿检测工具BlockCanary", "截图", "获取手机信息", "二维码", "NFC"
        };

        //List<String> list = Arrays.asList(values);
        //Arrays.asList(values)返回的是一个只读的List，不能进行add和remove
        //new ArrayList<>(Arrays.asList(values))则是一个可写的List，可以进行add和remove
        List<String> list = new ArrayList<>(Arrays.asList(values));
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter.getItem(position);
                switch (position) {
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
                    case 24:
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
                    case 26:
                        mActivity.startActivity(new Intent(mActivity, PhoneInfoActivity.class));
                        break;
                    case 27:
                        //二维码  http://wuxiaolong.me/2016/04/22/zxing/
                        break;
                    case 28:
                        mActivity.startActivity(new Intent(mActivity, NFCCheckActivity.class));
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
        Intent intent = new Intent();
        intent.setAction("com.vebos.appstore.search");
        intent.putExtra("keyword", keyword);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//        intent.putExtra("packageName", packageName);
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
}
