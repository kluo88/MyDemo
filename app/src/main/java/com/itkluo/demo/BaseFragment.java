package com.itkluo.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Fragment+ViewPager懒加载
 *
 * @author luobingyong
 * @date 2020/8/5
 */
public abstract class BaseFragment extends Fragment {

    protected static Handler mHandler = new Handler(Looper.getMainLooper());

    //双重判定，保证懒加载
    protected boolean isVisible;//这个，标记，当前Fragment是否可见
    private boolean isPrepared = false;//这个，标记当前Fragment是否已经执行了onCreateView
    //只有两个标记同时满足，才进行数据加载

    protected View root;

    private ProgressBar processBar;

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isPrepared = true;
        root = inflater.inflate(getLayoutId(), container, false);
        processBar = root.findViewById(R.id.processBar);
        if (processBar != null) {
            processBar.setVisibility(View.VISIBLE);
        }
        onLazyLoad();
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;//这个方法和onCreateView存在先后顺序，如果这个方法先，那么isVisible就会先变成true，但是这个时候，isPrepared还不是true，所以，懒加载不会进行。而要等到onCreateView执行的时候。
            onLazyLoad();
        } else {
            isVisible = false;
        }
    }

    /**
     * 懒加载
     */
    private void onLazyLoad() {
        if (isPrepared && isVisible) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (processBar != null) {
                        processBar.setVisibility(View.GONE);
                    }
                    isPrepared = false;//懒加载，只加载一次,这句话要不要，就具体看需求
                    initData();
                }
            }, 3000);
        } else {
            Log.d("onLazyLoadTag", "拒绝执行initData，因为条件不满足");
        }
    }

    protected abstract void initData();


}
