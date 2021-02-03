package com.itkluo.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by luobingyong on 2019/3/20.
 */
public class RxJavaDemoAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rxjava_demo);

        //以下是flatMap、concatMap和switchMap的运行实例对比：
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flatMap操作符的运行结果
                flatMapTest();
            }
        });
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //concatMap操作符的运行结果
                concatMapTest();
            }
        });
        findViewById(R.id.textView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switchMap操作符的运行结果
                switchMapTest();
            }
        });

    }

    public void switchMapTest() {
//        Observable.just(100, 2000, 3000).switchMap(new Function<Integer, Observable<Integer>>() {
//            @Override
//            public Observable<Integer> apply(Integer integer) throws Exception {
//                //100的延迟执行时间为200毫秒、2000和3000的延迟执行时间为180毫秒
//                int delay = 200;
//                if (integer == 2000 || integer == 3000) {
//                    delay = 180;
//                }
//
//                return Observable.fromArray(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                System.out.println("switchMap Next:" + integer);
//            }
//        });

        Observable.just(100, 2000, 3000).switchMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                //100的延迟执行时间为200毫秒、2000和3000的延迟执行时间为180毫秒
                int delay = 200;
                if (integer == 2000 || integer == 3000) {
                    delay = 180;
                }

                return Observable.just(integer).delay(delay, TimeUnit.MILLISECONDS);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("switchMap Next:" + integer);
            }
        });
    }

    private void concatMapTest() {
        Observable.just(100, 2000, 3000).concatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                //100的延迟执行时间为200毫秒、2000和3000的延迟执行时间为180毫秒
                int delay = 200;
                if (integer == 2000 || integer == 3000) {
                    delay = 180;
                }

                return Observable.fromArray(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("concatMap Next:" + integer);
            }
        });
    }

    private void flatMapTest() {
        Observable.just(100, 2000, 3000).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                //100的延迟执行时间为200毫秒、2000和3000的延迟执行时间为180毫秒
                int delay = 200;
                if (integer == 2000 || integer == 3000) {
                    delay = 180;
                }

                return Observable.fromArray(new Integer[]{integer, integer / 2}).delay(delay, TimeUnit.MILLISECONDS);
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("flatMap Next:" + integer);
            }
        });
    }
}

