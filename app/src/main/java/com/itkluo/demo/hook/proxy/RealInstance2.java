package com.itkluo.demo.hook.proxy;

/**
 * 真实对象
 *
 * @author luobingyong
 * @date 2020/8/6
 */
public class RealInstance2 implements CommInterface2 {

    @Override
    public void buyWomenTools(int size) {
        System.out.println("购买女性用品::size" + size);
    }
}
