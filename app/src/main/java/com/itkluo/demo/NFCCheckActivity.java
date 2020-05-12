package com.itkluo.demo;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author luobingyong
 * @date 2020/5/8
 */
public class NFCCheckActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tvUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_check);
        tvUid = (TextView) findViewById(R.id.tv_uid);
        //获取NfcAdapter实例
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //获取通知
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcAdapter == null) {
            Toast.makeText(NFCCheckActivity.this, "设备不支持NFC", Toast.LENGTH_LONG).show();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(NFCCheckActivity.this, "请在系统设置中先启用NFC功能", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            finish();
            return;
        }
        //因为启动模式是singleTop，于是会调用onNewIntent方法
        //预防Activity被清掉, onCreate方法和onNewIntent方法中都执行getIntent操作
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resolveIntent(intent);
    }

    void resolveIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            processTag(intent);
        }
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"};
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private String flipHexStr(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= s.length() - 2; i = i + 2) {
            result.append(new StringBuilder(s.substring(i, i + 2)));
        }
        return result.toString();
    }

    public void processTag(Intent intent) {//处理tag
        //获取到卡对象
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //获取卡id这里即uid
        byte[] aa = tagFromIntent.getId();
        String str = ByteArrayToHexString(aa);
        str = flipHexStr(str);
        tvUid.setText(str);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            //设置程序不优先处理
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            //设置程序优先处理
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    null, null);
    }

    public void onClickBack(View view) {
        finish();
    }
}
