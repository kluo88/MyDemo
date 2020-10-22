package com.itkluo.camera.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭相关工具类
 *
 * @author luobingyong
 * @date 2019/12/18
 */
public class IoUtils {

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIo(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {

                }
            }
        }
    }
}
