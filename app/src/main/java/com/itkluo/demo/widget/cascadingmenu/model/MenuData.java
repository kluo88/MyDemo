package com.itkluo.demo.widget.cascadingmenu.model;

/**
 * 级联菜单实体数据
 * Created by luobingyong on 2018/11/20.
 */
public class MenuData {
    private int menu_id;
    private String menu_name;
    private int menu_parent_id;
//    public List<MenuData> child;

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_parent_id() {
        return menu_parent_id;
    }

    public void setMenu_parent_id(int menu_parent_id) {
        this.menu_parent_id = menu_parent_id;
    }

}
