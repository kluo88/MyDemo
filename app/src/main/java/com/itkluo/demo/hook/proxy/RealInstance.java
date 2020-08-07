package com.itkluo.demo.hook.proxy;

/**
 * 真实对象
 *
 * @author luobingyong
 * @date 2020/8/6
 */
public class RealInstance implements CommInterface {

    @Override
    public void buyManTools(int size) {
        System.out.println("购买男性用品:size" + size);
    }
}
