package com.itkluo.demo.java.list;

import com.itkluo.demo.utils.JsonUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * 解析后台返回的json的map结构,  保证顺序
 * Created by luobingyong on 2019/3/20.
 */
public class SpecInfo implements Serializable {
    private static final long serialVersionUID = 7666251775049572095L;
    private Map<String, String> spec_name;

    public Map<String, String> getSpec_name() {
        return spec_name;
    }

    public void setSpec_name(Map<String, String> spec_name) {
        this.spec_name = spec_name;
    }

    public static String json() {
        return "{\n" +
                "    \"spec_name\": {\n" +
                "        \"3\": \"尺码\",\n" +
                "        \"33\": \"颜色\"\n" +
                "    }\n" +
                "}";
    }

    public static String json2() {
        return "{\n" +
                "    \"spec_name\": {\n" +
                "    \t  \"76\": \"尺码\",\n" +
                "    \t   \"55\": \"尺码\",\n" +
                "    \t    \"32\": \"尺码\",\n" +
                "    \t     \"8\": \"尺码\",\n" +
                "    \t      \"90\": \"尺码\",\n" +
                "        \"4\": \"尺码\",\n" +
                "        \"22\": \"颜色\",\n" +
                "        \"1\": \"尺码\",\n" +
                "        \"2\": \"尺码\",\n" +
                "        \"7\": \"尺码\",\n" +
                "        \"400\": \"尺码\",\n" +
                "        \"44\": \"尺码\",\n" +
                "        \"67\": \"尺码\",\n" +
                "        \"57\": \"尺码\",\n" +
                "        \"53\": \"尺码\",\n" +
                "        \"9\": \"尺码\",\n" +
                "         \"b\": \"尺码\",\n" +
                "          \"c\": \"尺码\",\n" +
                "           \"a\": \"尺码\",\n" +
                "            \"d\": \"尺码\",\n" +
                "        \"99\": \"尺码\"\n" +
                "    }\n" +
                "}";
    }

    public static void jsonToMapTest() {
        SpecInfo obj = JsonUtils.jsonToObject(SpecInfo.json2(), SpecInfo.class);
        // Google的Gson工具实际解析成LinkedTreeMap,  所以就保证了顺序
        Map<String, String> spec_name = obj.getSpec_name();

        Iterator<Map.Entry<String, String>> iterator = spec_name.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println("转换json--map: " + entry.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        jsonToMapTest();
    }


}