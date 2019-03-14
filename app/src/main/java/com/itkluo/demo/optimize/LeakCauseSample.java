package com.itkluo.demo.optimize;

import android.content.Context;

/**
 * 内存泄露产生示例
 * Created by luobingyong on 2019/2/23.
 */
public class LeakCauseSample {
    private static LeakCauseSample instance;
    private Context context;
    private LeakCauseSample(Context context) {
        this.context=context;
    }
    public static LeakCauseSample getInstance(Context context){
        if(null==instance){
            instance=new LeakCauseSample(context);
        }
        return instance;
    }
}
