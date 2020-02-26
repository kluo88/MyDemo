package com.itkluo.demo.apk;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author luobingyong
 * @date 2019/12/27
 */
public class AppDetailBean implements Serializable {
    private static final long serialVersionUID = 74008491480690396L;
    /**
     * app id
     */
    @SerializedName("id")
    public String id;
    /**
     * app name
     */
    @SerializedName("title")
    public String appName;
    /**
     * 类型 比如微信 type："社交"
     */
    @SerializedName("type")
    public String type;
    @SerializedName("body")
    public String body;
    /**
     * 下载链接
     */
    @SerializedName("url")
    public String downloadUrl;
    /**
     * 作者
     */
    @SerializedName("author")
    public String author;
    /**
     * 缩略图
     */
    @SerializedName("thumbnail")
    public String thumbnail;
    /**
     * 等级（分数）
     */
    @SerializedName("grade")
    public String grade;
    /**
     * 平台
     */
    @SerializedName("platform")
    public String platform;
    /**
     * 更新内容 "no"
     */
    @SerializedName("update_type")
    public String updateType;
    /**
     * 包名
     */
    @SerializedName("packagename")
    public String packageName;
    /**
     * 版本 "1.0.0.0"
     */
    @SerializedName("version")
    public String version;
    /**
     * 版本名 "2"
     */
    @SerializedName("version_num")
    public String versionCode;
    /**
     * 大小 11.35M
     */
    @SerializedName("size")
    public String size;
    /**
     * 详情图片
     */
    @SerializedName("detail_img")
    public String detailImage;
    /**
     * 详情图片数量
     */
    @SerializedName("img_count")
    public String detailImageCount;
    /**
     * 下载次数
     */
    @SerializedName("download_num")
    public String downloadCount;
    /**
     * 广告图片
     */
    @SerializedName("ad_img")
    public String adImage;
    /**
     * 是否使用标志 1
     */
    @SerializedName("ad_tag")
    public String adTag;
    /**
     * /介绍
     */
    @SerializedName("introduction")
    public String introduction;

    public int getVersionCode() {
        return Integer.valueOf(versionCode);
    }

    @Override
    public String toString() {
        return "AppDetailBean{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", author='" + author + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", grade='" + grade + '\'' +
                ", platform='" + platform + '\'' +
                ", updateType='" + updateType + '\'' +
                ", packageName='" + packageName + '\'' +
                ", version='" + version + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", size='" + size + '\'' +
                ", detailImage='" + detailImage + '\'' +
                ", detailImageCount='" + detailImageCount + '\'' +
                ", downloadCount='" + downloadCount + '\'' +
                ", adImage='" + adImage + '\'' +
                ", adTag='" + adTag + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}
