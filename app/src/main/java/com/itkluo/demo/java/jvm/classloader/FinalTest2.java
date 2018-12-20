package com.itkluo.demo.java.jvm.classloader;

import java.util.Random;

/**
 * https://www.jianshu.com/p/8c8d6cba1f8e
 * Created by luobingyong on 2018/12/18.
 */
public class FinalTest2 {
    public static final int x = new Random().nextInt(100);//3)在编译时无法确定下来的静态变量(运行时常量),会对类进行初始化;

    static {
        System.out.println("FinalTest2 static block");
    }

    public static void main(String args[]) {
        System.out.println(FinalTest2.x);
        //FinalTest2 static block
        // 61（随机数）
    }
}
