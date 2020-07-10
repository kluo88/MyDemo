package com.itkluo.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理工具类
 * 参照了 https://blog.csdn.net/weixin_43115440/article/details/90479752
 * 线程池原理 https://blog.csdn.net/u013293125/article/details/93163404
 *
 * @author luobingyong
 * @date 2020/1/20
 */
public class AppExecutors {
    private static final String TAG = "AppExecutors";
    /**
     * 磁盘IO线程池
     **/
    private final ExecutorService mDiskIO;
    /**
     * 网络IO线程池
     **/
    private final ExecutorService mNetworkIO;
    /**
     * UI线程
     **/
    private final Executor mMainThread;
    /**
     * 定时任务线程池
     **/
    private final ScheduledExecutorService mScheduledExecutor;

    private volatile static AppExecutors appExecutors;

    public static AppExecutors getInstance() {
        if (appExecutors == null) {
            synchronized (AppExecutors.class) {
                if (appExecutors == null) {
                    appExecutors = new AppExecutors();
                }
            }
        }
        return appExecutors;
    }


    private AppExecutors() {
        this(diskIoExecutor(), networkExecutor(), new MainThreadExecutor(), scheduledThreadPoolExecutor());
    }

    /**
     * 提供创建非单例模式
     *
     * @param diskIo
     * @param networkIo
     * @param mainThread
     * @param scheduledExecutor
     */
    public AppExecutors(ExecutorService diskIo, ExecutorService networkIo, Executor mainThread, ScheduledExecutorService scheduledExecutor) {
        this.mDiskIO = diskIo;
        this.mNetworkIO = networkIo;
        this.mMainThread = mainThread;
        this.mScheduledExecutor = scheduledExecutor;
    }


    /**
     * 定时(延时)任务线程池
     * <p>
     * 替代Timer,执行定时任务,延时任务
     */
    public ScheduledExecutorService scheduledExecutor() {
        return mScheduledExecutor;
    }

    /**
     * 磁盘IO线程池（单线程）
     * <p>
     * 和磁盘操作有关的进行使用此线程(如读写数据库,读写文件)
     * 禁止延迟,避免等待
     * 此线程不用考虑同步问题
     */
    public ExecutorService diskIo() {
        return mDiskIO;
    }

    /**
     * 网络IO线程池
     * <p>
     * 网络请求,异步任务等适用此线程
     * 不建议在这个线程 sleep 或者 wait
     */
    public ExecutorService networkIo() {
        return mNetworkIO;
    }

    /**
     * UI线程
     * <p>
     * Android 的MainThread
     * UI线程不能做的事情这个都不能做
     */
    public Executor mainThread() {
        return mMainThread;
    }

    /**
     * 线程加名称标识
     */
    static class MyThreadFactory implements ThreadFactory {

        private final String name;
        private int count = 0;

        MyThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            count++;
            return new Thread(r, name + "-" + count + "-Thread");
        }
    }

    private static ScheduledExecutorService scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(16, new MyThreadFactory("scheduled_executor")
                , new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                LogUtils.e(TAG, "rejectedExecution: scheduled executor queue overflow");
            }
        });
    }

    private static ExecutorService diskIoExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new MyThreadFactory("disk_executor"),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        LogUtils.e(TAG, "rejectedExecution: disk io executor queue overflow");
                    }
                });
    }

    private static ExecutorService networkExecutor() {
        return new ThreadPoolExecutor(3, 6, 1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(6),
                new MyThreadFactory("network_executor"),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        LogUtils.e(TAG, "rejectedExecution: network executor queue overflow");
                    }
                });
    }


    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
