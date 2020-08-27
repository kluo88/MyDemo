package com.itkluo.demo.usb.myhid;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.R;

import java.util.HashMap;

/**
 * Android USB Host与HID设备通信
 *
 * @author luobingyong
 * @date 2020/8/27
 */
public class UsbConnectHidActivity extends AppCompatActivity {
    private static final String TAG = "UsbConnectHidActivity";
    private FragmentActivity mContext;
    private FragmentActivity mActivity;
    private UsbManager myUsbManager;
    private UsbDevice myUsbDevice;
    private UsbInterface myInterface;
    private UsbDeviceConnection myDeviceConnection;
    private final int VendorID = 739;
    private final int ProductID = 1794;
    private TextView tv_info;

    private UsbEndpoint epOut;
    private UsbEndpoint epIn;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    byte[] mybuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_usb_connect_hid);
        mybuffer = new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x05, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        Log.i("mybuffer", bytesToHexString(mybuffer));
        tv_info = (TextView) findViewById(R.id.tv_info);
        myUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        enumerateDevice();

    }

    //枚举设备
    private void enumerateDevice() {
        if (myUsbManager == null)
            return;

        HashMap<String, UsbDevice> deviceList = myUsbManager.getDeviceList();
        if (!deviceList.isEmpty()) { // deviceList不为空
            StringBuffer sb = new StringBuffer();
            for (UsbDevice device : deviceList.values()) {
                sb.append(device.toString());
                sb.append("\n");
                tv_info.append(sb);
                // 输出设备信息
                Log.d(TAG, "DeviceInfo: " + device.getVendorId() + " , "
                        + device.getProductId());

                // 枚举到设备
                if (device.getVendorId() == VendorID && device.getProductId() == ProductID) {
                    myUsbDevice = device;
                    Toast.makeText(this, "枚举设备成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "枚举设备成功");
                    findInterface();
                } else {
//                    Toast.makeText(this, "Not Found VID and PID", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Not Found VID and PID");
                }
            }
        } else {
            new AlertDialog.Builder(this).setTitle("未枚举到设备！")
                    .setMessage("请先连接设备")
                    .setCancelable(false)
                    .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    }).show();
        }
    }

    //   找到接口
    private void findInterface() {
        if (myUsbDevice == null) {
            return;
        }
        Log.d(TAG, "interfaceCounts : " + myUsbDevice.getInterfaceCount());
        for (int i = 0; i < myUsbDevice.getInterfaceCount(); i++) {
            UsbInterface intf = myUsbDevice.getInterface(i);
            // 根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
            if (intf.getInterfaceClass() == 3
                    && intf.getInterfaceSubclass() == 0
                    && intf.getInterfaceProtocol() == 0) {
                myInterface = intf;
                Log.d(TAG, "找到我的设备接口");
                openDevice();
            }
            break;
        }

    }

    //获取权限，打开设备
    private void openDevice() {
        if (myInterface != null) {
            UsbDeviceConnection conn = null;
            // 在open前判断是否有连接权限；对于连接权限可以静态分配，也可以动态分配权限，可以查阅相关资料
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            if (!myUsbManager.hasPermission(myUsbDevice)) {
                myUsbManager.requestPermission(myUsbDevice, pi);
            }
            if (myUsbManager.hasPermission(myUsbDevice)) {
                conn = myUsbManager.openDevice(myUsbDevice);
            } else {
                Toast.makeText(this, "未获得权限", Toast.LENGTH_SHORT).show();
            }

            if (conn == null) {
                return;
            }

            if (conn.claimInterface(myInterface, true)) {
                myDeviceConnection = conn; // 到此你的android设备已经连上HID设备
                Log.d(TAG, "打开设备成功");
                Toast.makeText(this, "打开设备成功", Toast.LENGTH_SHORT).show();
                assignEndpoint();
            } else {
                conn.close();
            }
        }
    }

    //拿到端点，用bulkTransfer进行数据发收
    private void assignEndpoint() {
        if (myInterface.getEndpoint(1) != null) {
            epOut = myInterface.getEndpoint(1);
        }
        if (myInterface.getEndpoint(0) != null) {
            epIn = myInterface.getEndpoint(0);
        }
        tv_info.setText(myUsbDevice.getDeviceName() + "\nInterfaceCount:" + myUsbDevice.getInterfaceCount() + "\nEndpointCount:" + myInterface.getEndpointCount());
        Log.d(TAG, "assignEndpoint");


        int re = myDeviceConnection.bulkTransfer(epOut, mybuffer, mybuffer.length, 3000);
        byte[] reByte = new byte[64];
        int re2 = myDeviceConnection.bulkTransfer(epIn, reByte, reByte.length, 3000);
        Log.i("reByte", "re" + re + "re2" + re2 + "\n" + bytesToHexString(reByte));
        Toast.makeText(this, bytesToHexString(reByte), Toast.LENGTH_LONG).show();

    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


}
