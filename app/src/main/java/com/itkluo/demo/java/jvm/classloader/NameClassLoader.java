package com.itkluo.demo.java.jvm.classloader;

/**
 * Created by luobingyong on 2019/2/22.
 */
public class NameClassLoader {
    public static void main(String[] args) {
        ClassLoader ClassLoader1 = NameClassLoader.class.getClassLoader();
        ClassLoader ClassLoader2 = ClassLoader1.getParent();
        ClassLoader ClassLoader3 = ClassLoader2.getParent();
        System.out.println(ClassLoader1);
        System.out.println(ClassLoader2);
        System.out.println(ClassLoader3);

    }
}
