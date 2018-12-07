package com.itkluo.demo.model.memorytest;

/**
 * Created by luobingyong on 2018/12/3.
 */
public class MemorySingleton {
    private static MemorySingleton sInstance;
    private ObjectA objectA;

    public static synchronized MemorySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new MemorySingleton();
        }
        return sInstance;
    }

    private MemorySingleton() {
        objectA = new ObjectA();
    }

}
