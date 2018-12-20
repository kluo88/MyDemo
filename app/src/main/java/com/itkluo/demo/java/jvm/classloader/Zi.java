package com.itkluo.demo.java.jvm.classloader;

/**
 * 静态代码块、构造代码块、构造方法的执行顺序
 * Created by luobingyong on 2018/12/19.
 */
public class Zi extends Fu {
    static {
        System.out.println("子静态代码块");
    }
    {
        System.out.println("子代码块");
    }
    public Zi(){
        System.out.println("子构造函数");
    }

    public static void main(String[] args) {
        Zi zi = new Zi();
//        输出结果
//                父静态代码块
//                子静态代码块
//                父代码块
//                父构造函数
//                子代码块
//                子构造函数
    }

}
