package com.itkluo.demo.widget.cascadingmenu.contract;

import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.List;

/**
 * 级联菜单MVP模式的契约接口
 * Created by luobingyong on 2018/11/21.
 */
public interface CascadingMenuContract {
    interface View {

        void setFirstMenuList(List<MenuData> menuList, int selectPosition);

        void setSecondMenuList(List<MenuData> menuList);

        void setThirdMenuList(List<MenuData> menuList);
    }

    interface Presenter {

        void setView(CascadingMenuContract.View view);

        /**
         * 开始获取数据
         */
        void start();

        //    /**
//     * 第一级菜单数据
//     *
//     * @return
//     */
//    void requestFirstItem();

        /**
         * 第二级菜单数据
         *
         * @param parentMenuData
         */
        void requestSecondMenuList(MenuData parentMenuData);

        /**
         * 第三极菜单数据
         *
         * @param parentMenuData
         */
        void requestThirdMenuList(MenuData parentMenuData);
    }

}
