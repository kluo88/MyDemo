package com.itkluo.demo.java.jvm.classloader;

/**
 * 静态代码块、构造代码块、构造方法的执行顺序
 * Created by luobingyong on 2018/12/19.
 */
public class Fu {
    static {
        System.out.println("父静态代码块");
    }
    {
        System.out.println("父代码块");
    }
    public Fu(){
        System.out.println("父构造函数");
    }
}
