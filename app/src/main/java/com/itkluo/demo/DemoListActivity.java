package com.itkluo.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itkluo.ConstraintLayoutActivity;
import com.itkluo.demo.aidl.ClientActivity2;
import com.itkluo.demo.binder.ClientActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_list);
        ListView listView = (ListView) findViewById(R.id.listView);
        String[] values = {"使用Binder进行IPC通信", "使用AIDL进行IPC通信", "图片轮播", "ViewPage列表中gridview", "下拉级联菜单", "点击箭头显示下拉菜单", "ConstraintLayout嵌套在ScrollView里面"};
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
                        DemoListActivity.this.startActivity(new Intent(DemoListActivity.this, ClientActivity.class));
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
                    default:
                        break;
                }
            }
        });
    }
}
