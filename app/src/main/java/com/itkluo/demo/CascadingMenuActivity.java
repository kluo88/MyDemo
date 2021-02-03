package com.itkluo.demo;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.widget.cascadingmenu.CascadingMenuFragment;
import com.itkluo.demo.widget.cascadingmenu.CascadingMenuPopWindow;
import com.itkluo.demo.widget.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.itkluo.demo.widget.cascadingmenu.model.DBHelper;
import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下拉分类级联菜单测试
 * Created by luobingyong on 2018/11/20.
 */
public class CascadingMenuActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Map<String, String>> menuData1, menuData2, menuData3;
    private LinearLayout product, sort, activity;
    private TextView productTv, sortTv, activityTv;
    private static final int POPWINDOW_MENU = 1;
    private static final int FRAGMENT_MENU = 2;
    private int popType = FRAGMENT_MENU;

    // 两级联动菜单数据
    private CascadingMenuFragment cascadingMenuFragment = null;
    private CascadingMenuPopWindow cascadingMenuPopWindow = null;
    private DBHelper dBhelper;
    private View menuViewPopWindow;
    private View menuViewFragment;
    private int curShowMenuIndex;

    public CascadingMenuActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cascading_menu);
        findView();
        dBhelper = new DBHelper(this);
    }

    protected void findView() {
        product = (LinearLayout) findViewById(R.id.supplier_list_product);
        sort = (LinearLayout) findViewById(R.id.supplier_list_sort);
        activity = (LinearLayout) findViewById(R.id.supplier_list_activity);
        productTv = (TextView) findViewById(R.id.supplier_list_product_tv);
        sortTv = (TextView) findViewById(R.id.supplier_list_sort_tv);
        activityTv = (TextView) findViewById(R.id.supplier_list_activity_tv);
        product.setOnClickListener(this);
        sort.setOnClickListener(this);
        activity.setOnClickListener(this);
        menuViewPopWindow = findViewById(R.id.menuViewPopWindow);
        menuViewFragment = findViewById(R.id.menuViewFragment);
        menuViewPopWindow.setOnClickListener(this);
        menuViewFragment.setOnClickListener(this);

        menuViewFragment.setBackgroundColor(getResources().getColor(R.color.c_f0f0f0));
        menuViewPopWindow.setBackgroundColor(getResources().getColor(R.color.white));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuViewPopWindow:
                menuViewPopWindow.setBackgroundColor(getResources().getColor(R.color.c_f0f0f0));
                menuViewFragment.setBackgroundColor(getResources().getColor(R.color.white));
                popType = POPWINDOW_MENU;
                break;
            case R.id.menuViewFragment:
                menuViewFragment.setBackgroundColor(getResources().getColor(R.color.c_f0f0f0));
                menuViewPopWindow.setBackgroundColor(getResources().getColor(R.color.white));
                popType = FRAGMENT_MENU;
                break;

            case R.id.supplier_list_product:
                productTv.setTextColor(Color.parseColor("#39ac69"));
                break;
            case R.id.supplier_list_sort:
                sortTv.setTextColor(Color.parseColor("#39ac69"));
                if (popType == POPWINDOW_MENU) {
                    showPopMenu(sort);
                } else {
                    if (curShowMenuIndex == 1) {
                        closeFragmentMenu();
                    } else {
                        showFragmentMenu();
                    }
                }
                break;
            case R.id.supplier_list_activity:
                activityTv.setTextColor(Color.parseColor("#39ac69"));
                break;
        }
    }


    // 级联菜单选择回调接口
    class NMCascadingMenuViewOnSelectListener implements CascadingMenuViewOnSelectListener {
        @Override
        public void getValue(MenuData area) {
            cascadingMenuFragment = null;
            Toast.makeText(getApplicationContext(), "" + area.getMenu_name(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showFragmentMenu() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.short_menu_pop_in, R.anim.short_menu_pop_out);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("menu");
        if (cascadingMenuFragment == null) {
            cascadingMenuFragment = CascadingMenuFragment.newInstance();
            cascadingMenuFragment.setListener(new CascadingMenuFragment.AreaMenuListener() {
                @Override
                public void onAreaSelect(MenuData area) {

                }

                @Override
                public void dismissMenu() {
                    closeFragmentMenu();
                }
            });
            ft.add(R.id.menu_container, cascadingMenuFragment, "menu").show(cascadingMenuFragment);
        } else {
            ft.show(cascadingMenuFragment);
            curShowMenuIndex = 1;
        }
        ft.commit();
    }

    public void closeFragmentMenu() {
        curShowMenuIndex = 0;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.short_menu_pop_in, R.anim.short_menu_pop_out);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("menu");
        if (cascadingMenuFragment != null) {
            ft.hide(cascadingMenuFragment);
            ft.commit();
        }
    }


    private void showPopMenu(View anchor) {
        if (cascadingMenuPopWindow == null) {
            cascadingMenuPopWindow = new CascadingMenuPopWindow(getApplicationContext());
            cascadingMenuPopWindow.setMenuViewOnSelectListener(new NMCascadingMenuViewOnSelectListener());
            cascadingMenuPopWindow.showAsDropDown(anchor, 5, 5);
        } else if (cascadingMenuPopWindow.isShowing()) {
            cascadingMenuPopWindow.dismiss();
        } else {
            cascadingMenuPopWindow.showAsDropDown(anchor, 5, 5);
        }
    }


    private void initMenuData() {
        menuData1 = new ArrayList<Map<String, String>>();
        String[] menuStr1 = new String[]{"全部", "粮油", "衣服", "图书", "电子产品",
                "酒水饮料", "水果"};
        Map<String, String> map1;
        for (int i = 0, len = menuStr1.length; i < len; ++i) {
            map1 = new HashMap<String, String>();
            map1.put("name", menuStr1[i]);
            menuData1.add(map1);
        }

        menuData2 = new ArrayList<Map<String, String>>();
        String[] menuStr2 = new String[]{"综合排序", "配送费最低"};
        Map<String, String> map2;
        for (int i = 0, len = menuStr2.length; i < len; ++i) {
            map2 = new HashMap<String, String>();
            map2.put("name", menuStr2[i]);
            menuData2.add(map2);
        }

        menuData3 = new ArrayList<Map<String, String>>();
        String[] menuStr3 = new String[]{"优惠活动", "特价活动", "免配送费",
                "可在线支付"};
        Map<String, String> map3;
        for (int i = 0, len = menuStr3.length; i < len; ++i) {
            map3 = new HashMap<String, String>();
            map3.put("name", menuStr3[i]);
            menuData3.add(map3);
        }
    }
}
