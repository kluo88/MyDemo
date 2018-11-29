package com.itkluo.demo.model;

import java.io.Serializable;

/**
 * Created by luobingyong on 2018/11/16.
 */
public class MenuModel implements Serializable {
    private static final long serialVersionUID = 3688588914563527732L;
    private String name;
    private String imgUrl;

    public MenuModel(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
