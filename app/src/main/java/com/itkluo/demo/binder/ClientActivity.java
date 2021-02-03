package com.itkluo.demo.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.itkluo.demo.R;

/**
 * 使用Binder进行IPC通信
 * <p>
 * 实现如下效果：Activity‘中有一个按钮，点击该按钮，将abc和def三个字母拼接起来，拼接的函数在另一个进程中
 */
public class ClientActivity extends AppCompatActivity {
    private boolean isBound;
    private Button btn_add;
    private IBinder mRemote = null;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemote = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        bind();
        btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = null;
                try {
                    result = strcat("abc", "def");
                } catch (RemoteException e) {
                    Toast.makeText(ClientActivity.this, "error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Toast.makeText(ClientActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind() {
        Intent intent = new Intent(ClientActivity.this, ComputeService.class);
        isBound = bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    private void unbind() {
        if (isBound) {
            ClientActivity.this.unbindService(serviceConn);
            isBound = false;
        }
    }

    private String strcat(String x, String y) throws RemoteException {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
            _data.writeString(x);
            _data.writeString(y);
            mRemote.transact(1, _data, _reply, 0);
            _result = _reply.readString();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    protected void onDestroy() {
        unbind();
        super.onDestroy();
    }
}
