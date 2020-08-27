package com.itkluo.demo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author luobingyong
 * @date 2020/5/9
 */
public class TestGit {
    public static void main(String[] args) {
        //正解 https://blog.csdn.net/jakezhang1990/article/details/80827015
        String s1 = new String("aaa");
//        String s2 = new String("aaa");
        String s2 = "aaa";

        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());
        Set hashset = new HashSet();
        hashset.add(s1);
        hashset.add(s2);
        Iterator it = hashset.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        //“==”判断的是地址值，equals()方法判断的是内容。
        //这是因为String类已经重写了equals()方法和hashcode()方法，所以hashset认为它们是相等的对象，进行了重复添加。

    }

}
