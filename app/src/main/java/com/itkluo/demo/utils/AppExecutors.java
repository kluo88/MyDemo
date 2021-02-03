package com.itkluo.demo.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

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
 * <ul>
 *
 * <li>核心数 corePoolSize
 * 线程池中核心线程的数量。
 * <li>最大容量 maximumPoolSize
 * 线程池最大允许保留多少线程。
 * <li>超时时间 keepAliveTime
 * 线程池中普通线程的存活时间。
 *
 * </ul>
 * <p>
 * 当调用ThreadPoolExecutor.execute(runnable)的时候，会进行以下判断（这里不考虑延时任务）：
 * <p>
 * 1.如果线程池中，运行的线程数少于核心线程数（corePoolSize），那么就新建一个线程，并执行该任务。
 * 2.如果线程池中，运行的线程数大于等于corePoolSize，将线程添加到待执行队列中，等待执行；
 * 3.如果2中添加到队列失败，那么就新建一个非核心线程，并在该线程执行该任务；
 * 4.如果当前线程数已经达到最大线程数（maximumPoolSize），那么拒绝这个任务。
 * <p>
 * <p>
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

    /**
     * ScheduledThreadPool 定时线程池 核心线程数自定，最大线程数无上限。使用场景：处理延时任务。
     *
     * @return
     */
    private static ScheduledExecutorService scheduledThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(16, new MyThreadFactory("scheduled_executor")
                , new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                LogUtils.e(TAG, "rejectedExecution: scheduled executor queue overflow");
            }
        });
    }

    /**
     * 类似SingleThreadExecutor 线程池中的线程数固定为1。使用场景：当多个任务都需要访问同一个资源的时候。
     *
     * @return
     */
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

    /**
     * 类似FixedThreadPool 核心线程数为n，最大线程数为n。使用场景：明确同时执行任务数量时。
     *
     * @return
     */
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
