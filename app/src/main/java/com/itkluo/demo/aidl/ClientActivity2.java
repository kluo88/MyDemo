package com.itkluo.demo.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itkluo.demo.R;

/**
 * 使用aidl进行IPC通信
 * <p>
 * 实现如下效果：Activity‘中有一个按钮，点击该按钮，将abc和def三个字母拼接起来，拼接的函数在另一个进程中
 * https://blog.csdn.net/cauchyweierstrass/article/details/50701102
 */
public class ClientActivity2 extends AppCompatActivity {
    private ICompute compute = null;
    private boolean isBound;
    private Button btn_add;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            compute = ICompute.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = null;
                try {
                    result = compute.strcat("abc", "def");
                } catch (Exception e) {
                    Toast.makeText(ClientActivity2.this, "error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Toast.makeText(ClientActivity2.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind() {
        Intent intent = new Intent(ClientActivity2.this, ComputeService2.class);
        isBound = bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
    }

    private void unbind() {
        if (isBound) {
            ClientActivity2.this.unbindService(serviceConn);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        unbind();
        super.onDestroy();
    }
}
