package com.itkluo.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
            "http://res.qhhznt.com/rmadmin54/png/1543285623_1191.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285641_5616.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285655_7051.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285678_5689.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285700_633.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285720_4604.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285735_9929.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285762_6368.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285795_6508.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285831_5092.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285847_2127.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285864_5579.png"
            , "http://res.qhhznt.com/rmadmin54/png/1543285881_4559.png"
    };

    private void initData() {
        for (int i = 0; i < images.length; i++) {
            strs.add(new MenuModel("美食" + i, images[i%images.length]));
        }
    }

    private void updateUI() {
        List<String> list = Arrays.asList(images);
        banner.setData(strs);
    }


}
