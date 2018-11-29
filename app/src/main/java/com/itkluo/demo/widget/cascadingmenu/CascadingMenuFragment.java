package com.itkluo.demo.widget.cascadingmenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itkluo.demo.R;
import com.itkluo.demo.widget.cascadingmenu.contract.CascadingMenuContract;
import com.itkluo.demo.widget.cascadingmenu.interfaces.CascadingMenuDismissListener;
import com.itkluo.demo.widget.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.itkluo.demo.widget.cascadingmenu.model.DBHelper;
import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.ArrayList;

/**
 * Fragment调用方式显示级联菜单  适应异步获取菜单
 * Created by luobingyong on 2018/11/20.
 */
public class CascadingMenuFragment extends Fragment {
    private Context context;
    private ViewGroup rootView;
    private CascadingMenuView cascadingMenuView;
    private AreaMenuListener listener;
    private DBHelper dBhelper;

    public static CascadingMenuFragment newInstance() {
        CascadingMenuFragment fragment = new CascadingMenuFragment();
        Bundle bundle = new Bundle();
//        bundle.putSerializable("bean", bean);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListener(AreaMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        dBhelper = new DBHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cascading_menu, container, false);
        //实例化级联菜单
        MenuDataPresenter presenter = new MenuDataPresenter();
        //new View方式
//        cascadingMenuView = new CascadingMenuView(context).setMenuType(CascadingMenuView.EMenuType.Three).setHeightDp(200);
//        rootView.addView(cascadingMenuView);

        cascadingMenuView = rootView.findViewById(R.id.cascadingMenuView);
        //绑定Presenter
        cascadingMenuView.startLoad(presenter);
        //设置回调接口
        cascadingMenuView.setCascadingMenuViewOnSelectListener(new MCascadingMenuViewOnSelectListener());

        rootView.setOnClickListener(new View.OnClickListener() {//点击区域外退出
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点菜单外隐藏", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.dismissMenu();
                }
            }
        });
        return rootView;
    }

    private class MenuDataPresenter implements CascadingMenuContract.Presenter {
        private CascadingMenuContract.View mView;

        public void setView(CascadingMenuContract.View mView) {
            this.mView = mView;
        }

        @Override
        public void start() {
            // TODO: 这里可做异步取数据
            ArrayList<MenuData> menuList = dBhelper.getProvince();
            // TODO: 取数据完更新UI
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
        public void getValue(MenuData area) {
            if (listener != null) {
                listener.onAreaSelect(area);
            }
        }

    }

    public interface AreaMenuListener extends CascadingMenuDismissListener {
        void onAreaSelect(MenuData area);
    }

}
