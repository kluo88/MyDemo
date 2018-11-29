package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.itkluo.demo.model.MenuModel;
import com.itkluo.demo.widget.ViewpagerGridMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luobingyong on 2018/11/16.
 */
public class ViewpagerGridMenuActivity extends AppCompatActivity {
    private ViewpagerGridMenu banner;
    private List<MenuModel> strs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_grid_menu);
        banner = (ViewpagerGridMenu) findViewById(R.id.banner);
        initData();
        updateUI();
    }

    private String[] images = new String[]{
            "http://res.qhhznt.com/rmadmin75/jpg/1540435312_3083.jpg"
            , "http://res.qhhznt.com/rmadmin75/png/1540435268_4949.png"
            , "http://res.qhhznt.com/rmadmin75/png/1540353097_6681.png"
            , "http://res.qhhznt.com/rmadmin75/png/1540287662_1406.png"
            , "http://res.qhhznt.com/rmadmin75/png/1540352831_4606.png"
            , "http://res.qhhznt.com/rmadmin75/png/1540287677_795.png"
            , "http://res.qhhznt.com/rmadmin75/jpg/1540263689_9531.jpg"
            , "http://res.qhhznt.com/rmadmin75/jpg/1540263771_5758.jpg"
    };

    private void initData() {
        for (int i = 0; i < 12; i++) {
            strs.add(new MenuModel("美食" + i, images[i%images.length]));
        }
    }

    private void updateUI() {
        List<String> list = Arrays.asList(images);
        banner.setData(strs);
    }


}
