package com.itkluo.demo.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 使用Binder进行IPC通信
 * Created by luobingyong on 2018/8/30.
 */
public class ComputeService2 extends Service {

    private IBinder binder = new Binder() {
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                String _arg0;
                _arg0 = data.readString();
                String _arg1;
                _arg1 = data.readString();
                String _result = this.strcat(_arg0, _arg1);
                reply.writeString(_result);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        public String strcat(String x, String y) {
            return x + y;
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }
}
