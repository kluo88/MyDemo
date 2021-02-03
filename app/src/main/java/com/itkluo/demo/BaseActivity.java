package com.itkluo.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.itkluo.demo.widget.TitleBar;


/**
 * @author luobingyong
 * @date 2020/5/11
 */
public abstract class BaseActivity extends AppCompatActivity implements TitleBar.OnTitleBarListener{
    protected String TAG;
    protected FragmentActivity mContext;
    protected FragmentActivity mActivity;
    /**
     * 标题栏对象
     */
    protected TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        beforeOnCreate();
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        TAG = getClass().getSimpleName();
        initView(savedInstanceState);
    }

    //----- by kluo 想减少子类要实现的抽象方法, 可让子类在initView()中setContentView, 父类覆写setContentView
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // 做一些依赖于已设置contentView的公共功能, 比如绑定ButterKnife, 设置标题栏
        initTitleBar();
    }

    protected void beforeOnCreate() {
    }

    /**
     * 初始化控件
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * ***************************************** 标题栏 **************************************
     * <p>
     * <p>
     * /**
     * 获取标题栏 id
     */
    protected int getTitleId() {
        return 0;
    }

    /**
     * 初始化标题栏布局
     */
    protected void initTitleBar() {
        // 初始化标题栏的监听
        if (getTitleId() > 0) {
            // 勤快模式
            View view = findViewById(getTitleId());
            if (view instanceof TitleBar) {
                mTitleBar = (TitleBar) view;
            }
        } else if (getTitleId() == 0) {
            ViewGroup viewGroup = findViewById(Window.ID_ANDROID_CONTENT);
            // 懒人模式
            mTitleBar = findTitleBar(viewGroup);
        }
        if (mTitleBar != null) {
            mTitleBar.setOnTitleBarListener(this);
        }
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    private static TitleBar findTitleBar(ViewGroup group) {
        if (group == null) {
            return null;
        }
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof TitleBar)) {
                return (TitleBar) view;
            } else if (view instanceof ViewGroup) {
                TitleBar titleBar = findTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }

    /**
     * {@link OnTitleBarListener}
     */

    /**
     * TitleBar 左边的View被点击了
     * return  false 默认处理Finish页面   true 子类要自己处理返回事件
     */
    @Override
    public boolean onClickBackBtn() {
        return false;
    }

    /**
     * TitleBar 右边的View被点击了
     */
    @Override
    public void onClickRightTv() {

    }

    /**
     * TitleBar 右边的View被点击了
     */
    @Override
    public void onClickRightIv1() {

    }

    /**
     * TitleBar 右边的View被点击了
     */
    @Override
    public void onClickRightIv2() {

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
//        Log.d(TAG, "onNewIntent: " + action);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }


    private Toast toast = null;

    public void showToast(final String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_LONG);
        toast.show();
    }

}
