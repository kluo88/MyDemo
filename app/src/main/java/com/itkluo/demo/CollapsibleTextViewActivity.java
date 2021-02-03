package com.itkluo.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itkluo.demo.widget.collapsibletextview.CollapsibleTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 可展开|收缩TextView内容
 * Created by luobingyong on 2018/12/7.
 */
public class CollapsibleTextViewActivity extends AppCompatActivity {
    private List<String> strList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collapsible_textview);
        initData();
        useRecyclerView();
    }

    private void useRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecycleAdapter recycleAdapter = new RecycleAdapter(strList);
        recyclerView.setAdapter(recycleAdapter);
    }

    private void initData() {
        for (int i = 0; i < 109; i++) {
            if (i % 3 == 0) {
                strList.add(String.format("第%d条数据我子恒昌as砥砺奋进阿萨德飞机司法局了书法家啥的房间哦啊接啥都放假飒的发搜房安嗽发搜房奥法奥法到啊飞洒佛家说发哦发送佛奥发奥爱上佛沙发建瓯市金佛山飞机哦自健身房加上发阿萨书法家暗室逢灯骄傲搜缴费", i));
            } else {
                strList.add(String.format("第%d条数据", i));
            }
        }
    }


    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        private List<String> mDatas;

        public RecycleAdapter(List<String> mDatas) {
            this.mDatas = mDatas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collapsible_textview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.desc_collapse_tv.setText(mDatas.get(position),TextView.BufferType.NORMAL);
        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CollapsibleTextView desc_collapse_tv;

            public ViewHolder(View itemView) {
                super(itemView);
                desc_collapse_tv = itemView.findViewById(R.id.desc_collapse_tv);
            }
        }
    }

}
