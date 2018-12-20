package com.itkluo.demo.java.jvm.classloader;

/**
 * https://www.jianshu.com/p/8c8d6cba1f8e
 * Created by luobingyong on 2018/12/18.
 */
public class FinalTest {
    public static final int x =6/3; //2)在编译的时候能确定下来的静态变量(编译常量),不会对类进行初始化;
//    将final去掉之后呢？输出又是什么呢？
//    这就是对类的首次主动使用，引用类的静态变量，输出的当然是：
//    FinalTest static block
//    2

    static {
        System.out.println("FinalTest static block");
    }

    public static void main(String args[]){
        System.out.println(FinalTest.x); // 2

    }
}
