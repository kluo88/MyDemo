package com.itkluo.demo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * json工具类
 *
 * @author luobingyong
 * @date 2018/8/17
 */
public class JsonUtils {
    /**
     * json格式转对象
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return new Gson().fromJson(json, clazz);
        } catch (Exception e) {
            LogUtils.e("ParseJsonEntity", "json解析出错：" + e.getMessage() + "\n原因是：" + e.getCause());
            return null;
        }
    }

    public static <T> T jsonToObject(String json, Type type) {
        try {
            return new Gson().fromJson(json, type);
        } catch (Exception e) {
            LogUtils.e("ParseJsonEntity", "fastjson解析出错：" + e.getMessage() + "\n原因是：" + e.getCause());
            return null;
        }

    }

    public static <T> List<T> jsonToList(String json, Class<T> className) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        List<JsonObject> jsonObjs = new Gson().fromJson(json, type);
        List<T> list = new ArrayList<T>();
        for (JsonObject jsonObj : jsonObjs) {
            list.add(new Gson().fromJson(jsonObj, className));
        }
        return list;
    }

    /**
     * 功能描述：把JSON数据转换成普通字符串列表
     */
    public static List<String> jsonToList(String jsonData) {
        try {
            return new Gson().fromJson(jsonData, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            LogUtils.e("ParseJsonEntity", "json解析出错：" + e.getMessage() + "\n原因是：" + e.getCause());
            return null;
        }
    }

    public static <T> String toJson(Object clazz) {
        try {
            return new Gson().toJson(clazz);
        } catch (Exception e) {
            LogUtils.e("ToJsonEntity", "json转换错误：" + e.getMessage() + "\n原因是：" + e.getCause());
            return null;
        }
    }

    /**
     * 功能描述：把指定的java对象转为json数据
     */
    public static <T> String toJson(Class<T> clazz) {
        try {
            return new Gson().toJson(clazz);
        } catch (Exception e) {
            LogUtils.e("ToJsonEntity", "json转换错误：" + e.getMessage() + "\n原因是：" + e.getCause());
            return null;
        }
    }

}
