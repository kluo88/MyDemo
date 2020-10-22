package com.itkluo.demo.hook.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 文章 [彻底理解动态代理]{https://www.jianshu.com/p/992dfcecff34}
 *
 * 被代理对象的处理对象
 * <p>
 * 所谓，动态代理，它的优势，就是我使用之后，获取代理对象，不再依赖外部传入的接口，外部接口变化，我只需要改变真实对象的实例即可。
 *
 * @author luobingyong
 * @date 2020/8/6
 */
public class DynamicProxyInstance implements InvocationHandler {

    // 动态代理，这里不限制具体使用的接口是什么，而是使用Object表示真实对象
    private Object realInstance;

    /**
     * 传入 被代理的对象
     * @param realInstance
     */
    public void init(Object realInstance) {
        this.realInstance = realInstance;
    }

    /**
     * 创建代理对象
     *
     * @return
     */
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(realInstance.getClass().getClassLoader(), realInstance.getClass().getInterfaces(),
                this);
        // 那么这个对象到底是如何创建出来的呢？

    }

    /**
     * 售前
     */
    private void doBefore() {
        System.out.println("售前");
    }

    /**
     * 售后
     */
    private void doAfter() {
        System.out.println("售后");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore();
        Object res = method.invoke(realInstance, args);
        doAfter();
        return res;
    }

    public static void main(String[] args) {

        // 动态代理和静态代理的唯一区别，就是在获取代理对象的时候，静态代理获取的是一个不可变功能的对象，一旦获取到，那么它的功能也就确定了
        // 而，动态代理，

        //创建被代理对象的处理对象
        DynamicProxyInstance instance = new DynamicProxyInstance();
        //创建并传入被代理的对象
        instance.init(new RealInstance());
        //创建代理对象
        CommInterface commInterface = (CommInterface) instance.getProxyInstance();
        // 先看看这个对象在debug的时候，是怎么样的一个对象引用
        commInterface.buyManTools(30);

        // 现在，出现了一个 购买女性用品的接口，和它的实现类，也要用代理来访问
        // 这样做
        instance.init(new RealInstance2());
        CommInterface2 commInterface2 = (CommInterface2) instance.getProxyInstance();
        commInterface2.buyWomenTools(60);

        // 看到了吧，同样的一个代理对象，可以实现多个真实对象的代理功能，而不是像静态代理一样，需要改动代理类
    }
}
