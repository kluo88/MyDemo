package com.itkluo.demo.widget.cascadingmenu;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.itkluo.demo.widget.cascadingmenu.contract.CascadingMenuContract;
import com.itkluo.demo.widget.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.itkluo.demo.widget.cascadingmenu.model.DBHelper;
import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.ArrayList;

/**
 * PopWindow调用方式显示级联菜单
 * Created by luobingyong on 2018/11/20.
 */
public class CascadingMenuPopWindow extends PopupWindow {
    private Context context;
    private CascadingMenuView cascadingMenuView;
    private CascadingMenuViewOnSelectListener menuViewOnSelectListener;
    private DBHelper dBhelper;

    public void setMenuViewOnSelectListener(CascadingMenuViewOnSelectListener menuViewOnSelectListener) {
        this.menuViewOnSelectListener = menuViewOnSelectListener;
    }

    public CascadingMenuPopWindow(Context context) {
        super(context);
        this.context = context;
//        init();
    }

    private void init() {
        dBhelper = new DBHelper(context);
        //实例化级联菜单
        MenuDataPresenter presenter = new MenuDataPresenter();
        cascadingMenuView = new CascadingMenuView(context).setMenuType(CascadingMenuView.EMenuType.Three);
        //绑定Presenter
        cascadingMenuView.startLoad(presenter);
        //设置回调接口
        cascadingMenuView.setCascadingMenuViewOnSelectListener(new MCascadingMenuViewOnSelectListener());

        setContentView(cascadingMenuView);
        setAnimationStyle(android.R.style.Animation_Dialog);
        setFocusable(true);
        setOutsideTouchable(false);
        setBackgroundDrawable(null);
//        setAnimationStyle(R.style.Animation);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(dp2px(context, 380));
    }

    private class MenuDataPresenter implements CascadingMenuContract.Presenter {
        private CascadingMenuContract.View mView;

        public void setView(CascadingMenuContract.View mView) {
            this.mView = mView;
        }

        @Override
        public void start() {
            ArrayList<MenuData> menuList = dBhelper.getProvince();
            mView.setFirstMenuList(menuList, 0);
        }

        @Override
        public void requestSecondMenuList(MenuData parentMenuData) {
            ArrayList<MenuData> secondMenuList = dBhelper.getCity(parentMenuData.getMenu_id());
            mView.setSecondMenuList(secondMenuList);
        }

        @Override
        public void requestThirdMenuList(MenuData parentMenuData) {
            ArrayList<MenuData> thirdMenuList = dBhelper.getDistrict(parentMenuData.getMenu_id());
            mView.setThirdMenuList(thirdMenuList);
        }
    }

    //级联菜单选择回调接口
    class MCascadingMenuViewOnSelectListener implements CascadingMenuViewOnSelectListener {

        @Override
        public void getValue(com.itkluo.demo.widget.cascadingmenu.model.MenuData menuItem) {
            if (menuViewOnSelectListener != null) {
                menuViewOnSelectListener.getValue(menuItem);
                dismiss();
            }
        }
    }

    private static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

}
