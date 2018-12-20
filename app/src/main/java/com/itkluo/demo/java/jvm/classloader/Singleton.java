package com.itkluo.demo.java.jvm.classloader;

/**
 * Created by luobingyong on 2018/12/18.
 */
public class Singleton {
    private static Singleton singleton = new Singleton();
    public static int counter1;//3)在编译时无法确定下来的静态变量(运行时常量),会对类进行初始化;
    public static int counter2 = 0;

    private Singleton() {
        System.out.println("执行构造方法 {");
        System.out.println("原始counter1=" + counter1);
        System.out.println("原始counter2=" + counter2);
        counter1++;
        counter2++;
        System.out.println("}");
    }

    public static Singleton getSingleton() {
        return singleton;
    }

    public static void main(String args[]) {
        Singleton singleton = Singleton.getSingleton();
        System.out.println("counter1=" + singleton.counter1); //counter1=1
        System.out.println("counter2=" + singleton.counter2); //counter2=0


    }

}
