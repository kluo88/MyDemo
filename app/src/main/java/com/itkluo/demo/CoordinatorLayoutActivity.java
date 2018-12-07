package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luobingyong on 2018/12/1.
 */
public class CoordinatorLayoutActivity extends AppCompatActivity {
    private List<String> strList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout);
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
        for (int i = 0; i < 39; i++) {
            strList.add(String.format("第%d条数据", i));
        }
    }


    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        private List<String> mDatas;

        public RecycleAdapter(List<String> mDatas) {
            this.mDatas = mDatas;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv_name.setText(mDatas.get(position));
        }


        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }

}
