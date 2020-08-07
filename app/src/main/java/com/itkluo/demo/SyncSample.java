package com.itkluo.demo;

import android.os.Looper;

/**
 * 参考WindowManagerService使用的同步方式
 *
 * @author luobingyong
 * @date 2020/7/30
 */
public class SyncSample {
    public static SyncSample main() {
        WMThread thr = new WMThread();
        thr.start();

        synchronized (thr) {
            while (thr.mSyncSample == null) {
                try {
                    thr.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return thr.mSyncSample;
        }

    }

    static class WMThread extends Thread {
        SyncSample mSyncSample;

        @Override
        public void run() {
            Looper.prepare();
            SyncSample s = new SyncSample();

            synchronized (this) {
                mSyncSample = s;
                notifyAll();
            }

            Looper.loop();
        }
    }

}
