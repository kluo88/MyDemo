package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luobingyong on 2018/12/1.
 */
public class CoordinatorListViewActivity extends AppCompatActivity {
    private ListView listView;
    private List<String> strList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_layout2);
        initData();
        useListView();
    }

    private void useListView() {
        listView = findViewById(R.id.listView);
        listView.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, strList.toArray(new String[strList.size()]));
        listView.setAdapter(adapter);
    }

    private void initData() {
        for (int i = 0; i < 2; i++) {
            strList.add(String.format("第%d条数据", i));
        }
    }


}
