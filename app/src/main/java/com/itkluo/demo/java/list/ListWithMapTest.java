package com.itkluo.demo.java.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luobingyong on 2019/1/15.
 */
public class ListWithMapTest {
    public static void main(String[] args) throws Exception {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("page", 1);
            map.put("name", "name_" + i);
            dataList.add(map);
        }
        System.out.println("更改前：" + dataList);
        Map<String, Object> selectedDataMap = dataList.get(1);
        int page = (int) selectedDataMap.get("page");
        selectedDataMap.put("page", page + 2);
        System.out.println("更改后：" + dataList);
    }
}