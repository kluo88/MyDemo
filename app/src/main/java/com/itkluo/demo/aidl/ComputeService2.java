package com.itkluo.demo.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * 使用AIDL进行IPC通信
 * Created by luobingyong on 2018/8/30.
 */
public class ComputeService2 extends Service {

    private IBinder binder = new ICompute.Stub() {
        @Override
        public String strcat(String x, String y) throws RemoteException {
            return x+y;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }
}
