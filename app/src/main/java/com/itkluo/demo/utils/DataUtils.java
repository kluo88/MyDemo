package com.itkluo.demo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luobingyong on 2019/5/29.
 */
public class DataUtils {
    /**
     * 流转换Stream的工具
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String toStream(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 长度
        int length = 0;
        byte[] buffer = new byte[1024];
        // -1代表读完了
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        // 读完关闭
        in.close();
        out.close();
        // 我们把返回的数据转换成String
        return out.toString();
    }

}
