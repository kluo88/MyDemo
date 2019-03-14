package com.itkluo.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.itkluo.demo.widget.menu.PopupMenuItem;
import com.itkluo.demo.widget.menu.TitlePopupMenu;

/**
 * Created by luobingyong on 2019/3/6.
 */
public class TitleDownMenuActivity extends AppCompatActivity {
    //定义标题栏上的按钮
    private ImageButton titleBtn;
    //定义标题栏弹窗按钮
        private TitlePopupMenu titlePopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_down_menu);
        //实例化标题栏按钮并设置监听
        titleBtn = (ImageButton) findViewById(R.id.title_btn);
        titleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopupMenu.show(v);
            }
        });
        titlePopupMenu = new TitlePopupMenu(this);
        titlePopupMenu.addItem(new PopupMenuItem("首页", R.drawable.more_home));
        titlePopupMenu.addItem(new PopupMenuItem( "我的", R.drawable.more_my));
        titlePopupMenu.addItem(new PopupMenuItem( "积分说明", R.drawable.help));
        titlePopupMenu.addItem(new PopupMenuItem( "反馈", R.drawable.more_feedback));

    }

}
