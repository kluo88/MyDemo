package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itkluo.demo.widget.MyTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 改造系统TabLayout
 * Created by luobingyong on 2018/12/20.
 */
public class MyTabLayoutActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private MyTabLayout tab;
    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytab_layout);
        initView();
    }

    private void initView() {
        tab = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewPager);
        TextView textView;
        for (int i = 0; i < 4; i++) {
            textView = new TextView(this);
            textView.setText(String.format("第%d页数据", i));
            mViewList.add(textView);
            mTitleList.add("Tab " + i);
            tab.addTab(tab.newTab().setText(mTitleList.get(i)));//添加tab选项卡
        }
        pagerAdapter = new MyPagerAdapter(mViewList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        tab.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
//        tab.setTabsFromPagerAdapter(pagerAdapter);//给Tabs设置适配器
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return (mViewList.get(position));
        }

        @Override
        public int getCount() {
            if (mViewList == null)
                return 0;
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //这里这个一定要写，不然卡片标题显示不出来
            return mTitleList.get(position);//页卡标题
        }

    }


}
