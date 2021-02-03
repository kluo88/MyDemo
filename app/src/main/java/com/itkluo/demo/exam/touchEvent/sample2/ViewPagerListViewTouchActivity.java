package com.itkluo.demo.exam.touchEvent.sample2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.itkluo.demo.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerListViewTouchActivity extends AppCompatActivity {
    private MyViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_list_view_touch);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyAdapter());
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(getApplicationContext(), R.layout.viewpager_item_listview, null);
            ListView listView = view.findViewById(R.id.listView);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                list.add("===page：" + position + "  item：" + i);
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewPagerListViewTouchActivity.this
                    , android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);

            ((MyViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((MyViewPager) container).removeView((View) object);
        }

    }
}
