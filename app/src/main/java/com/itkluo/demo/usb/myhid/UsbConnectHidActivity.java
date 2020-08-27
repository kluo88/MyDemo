package com.itkluo.demo.usb.myhid;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.R;

import java.util.HashMap;

/**
 * Android USB Host与HID设备通信
 * 枚举设备->找到设备的接口->连接设备->分配相应的端点->在IN端点进行读操作，在OUT端点进行写操作
 *
 * @author luobingyong
 * @date 2020/8/27
 */
public class UsbConnectHidActivity extends AppCompatActivity {
    private static final String TAG = "UsbConnectHidActivity";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private FragmentActivity mContext;
    private FragmentActivity mActivity;
    private TextView tv_info;
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbInterface mUsbInterface;
    private UsbDeviceConnection mDeviceConnection;
    private final int mVendorId = 739;
    private final int mProductId = 1794;
    private UsbEndpoint mUsbEndpointOut;
    private UsbEndpoint mUsbEndpointIn;
    private byte[] mSendBytes;
    private boolean justEnumerate;
    private int mInMaxPacketSize;
    private int mOutMaxPacketSize;

    public UsbConnectHidActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_usb_connect_hid);
        mSendBytes = new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x05, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0xcc, (byte) 0x03};
        tv_info = (TextView) findViewById(R.id.tv_info);
        //注册usb权限广播
        IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbPermissionReceiver, permissionFilter);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    /**
     * 获取设备权限广播
     */
    private final BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant permission for this usb device
                        Log.d(TAG, "permission request get" + usbDevice);
                        if (usbDevice != null && usbDevice.equals(mUsbDevice)) {
                            openDevice();
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant permission for this usb device
//                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "permission denied for device " + usbDevice);
                    }
                }

            }//synchronized

        }
    };

    //枚举设备
    private void enumerateDevice() {
        if (mUsbManager == null) {
            return;
        }
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
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
                if (device.getVendorId() == mVendorId && device.getProductId() == mProductId) {
                    mUsbDevice = device;
                    Toast.makeText(this, "枚举设备成功", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "枚举设备成功");
                    findInterface();
                    break;
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

    //找到接口
    private void findInterface() {
        if (mUsbDevice == null) {
            return;
        }
        int interfaceCount = mUsbDevice.getInterfaceCount();
        Log.d(TAG, "interfaceCounts : " + interfaceCount);
        if (interfaceCount < 1) {
            Log.d(TAG, "Not Found Device Interface");
            Toast.makeText(this, "没找到我的设备接口", Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取设备接口，一般都是一个接口，你可以打印getInterfaceCount()方法查看接
        // 口的个数，在这个接口上有两个端点，OUT 和 IN
        for (int i = 0; ; ) {
            UsbInterface intf = mUsbDevice.getInterface(i);

            // 根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
//            if (intf.getInterfaceClass() == 3
//                    && intf.getInterfaceSubclass() == 0
//                    && intf.getInterfaceProtocol() == 0) {//HID设备的相关信息
            mUsbInterface = intf;
            Log.d(TAG, "找到我的设备接口");

//            }
            break;
        }

        if (mUsbInterface != null) {
            if (!justEnumerate) {
                openDevice();
            }
        }


    }

    //获取权限，打开设备
    private void openDevice() {
        if (mUsbInterface == null) {
            Toast.makeText(this, "请先枚举设备", Toast.LENGTH_SHORT).show();
            return;
        }

        UsbDeviceConnection conn = null;
        // 在open前判断是否有连接权限；对于连接权限可以静态分配，也可以动态分配权限，可以查阅相关资料
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        if (!mUsbManager.hasPermission(mUsbDevice)) {
            mUsbManager.requestPermission(mUsbDevice, pi);
        }
        if (mUsbManager.hasPermission(mUsbDevice)) {
            conn = mUsbManager.openDevice(mUsbDevice);
        } else {
            Toast.makeText(this, "未获得权限", Toast.LENGTH_SHORT).show();
        }

        if (conn == null) {
            return;
        }

        if (conn.claimInterface(mUsbInterface, true)) {
            mDeviceConnection = conn; // 到此你的android设备已经连上HID设备

            tv_info.setText(mUsbDevice.getDeviceName() + "\nVendorId: :" + mUsbDevice.getVendorId()
                    + "\nProductId:" + mUsbDevice.getProductId());

            Log.d(TAG, "assignEndpoint");

            Log.d(TAG, "打开设备成功");
            Toast.makeText(this, "打开设备成功", Toast.LENGTH_SHORT).show();
//                assignEndpoint();
        } else {
            conn.close();
        }
    }

    /**
     * 拿到端点，用bulkTransfer进行数据发收
     * 比如自定义设备发送/接收模式为：
     * 发送命令out（发送预发送命令+发送命令+接收发送成功信息）；接收数据in（发送预接收命令+接收数据+接收数据成功信息）。
     */
    private void assignEndpoint() {
        if (mDeviceConnection == null || mUsbInterface == null) {
            Toast.makeText(this, "先打开设备", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mUsbInterface.getEndpoint(1) != null) {
            mUsbEndpointOut = mUsbInterface.getEndpoint(1);
        }
        if (mUsbInterface.getEndpoint(0) != null) {
            mUsbEndpointIn = mUsbInterface.getEndpoint(0);
        }

        tv_info.setText(mUsbDevice.getDeviceName() + "\nInterfaceCount:" + mUsbDevice.getInterfaceCount()
                + "\nEndpointCount:" + mUsbInterface.getEndpointCount());
        Log.d(TAG, "assignEndpoint");

        if (mUsbEndpointOut == null || mUsbEndpointIn == null) {
            mDeviceConnection.close();
            Log.d(TAG, "get endpoint failed");
            Toast.makeText(this, "获取收发端口失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mInMaxPacketSize = mUsbEndpointIn.getMaxPacketSize();
        mOutMaxPacketSize = mUsbEndpointOut.getMaxPacketSize();

        int re = mDeviceConnection.bulkTransfer(mUsbEndpointOut, mSendBytes, mSendBytes.length, 3000);
        byte[] reByte = new byte[64];

        int re2 = mDeviceConnection.bulkTransfer(mUsbEndpointIn, reByte, reByte.length, 3000);
        Log.i("reByte", "re" + re + "re2" + re2 + "\n" + HexStringUtil.bytesToHexString(reByte));

        tv_info.append("\n\n发送数据：" + HexStringUtil.bytesToHexString(mSendBytes));
        tv_info.append("\n返回码：" + "re=" + re);
        tv_info.append("\n");
        tv_info.append("\n接收的数据：" + HexStringUtil.bytesToHexString(reByte));
        tv_info.append("\n返回码：" + "re2=" + re2);
    }


    /**
     * 发送包操作，发送OUT + 发送COM + 接收IN
     * 比如自定义设备发送/接收模式为：
     * 发送命令out（发送预发送命令+发送命令+接收发送成功信息）；接收数据in（发送预接收命令+接收数据+接收数据成功信息）。
     *
     * @param command
     */
    private void sendPackage(byte[] command) {
        int ret = -100;
        int len = command.length;

        // 组织准备命令
        byte[] sendOut = Commands.OUT_S;
        sendOut[8] = (byte) (len & 0xff);
        sendOut[9] = (byte) ((len >> 8) & 0xff);
        sendOut[10] = (byte) ((len >> 16) & 0xff);
        sendOut[11] = (byte) ((len >> 24) & 0xff);

        // 1,发送准备命令
        ret = mDeviceConnection.bulkTransfer(mUsbEndpointOut, sendOut, 31, 10000);
        if (ret != 31) {
            return;
        }

        // 2,发送COM
        ret = mDeviceConnection.bulkTransfer(mUsbEndpointOut, command, len, 10000);
        if (ret != len) {
            return;
        }

        // 3,接收发送成功信息
        ret = mDeviceConnection.bulkTransfer(mUsbEndpointIn, Commands.IN, 13, 10000);
        if (ret != 13) {
            return;
        }
    }

    /**
     * 枚举设备
     *
     * @param view
     */
    public void enumerateDevice(View view) {
        tv_info.setText("");
        mUsbDevice = null;
        mUsbInterface = null;
        mDeviceConnection = null;
        justEnumerate = true;
        enumerateDevice();
    }

    /**
     * 连接设备
     *
     * @param view
     */
    public void openDevice(View view) {
        openDevice();
    }

    public void transfer(View view) {
        assignEndpoint();
    }

    /**
     * 枚举并连接设备
     *
     * @param view
     */
    public void enumerateAndOpenDevice(View view) {
        tv_info.setText("");
        mUsbDevice = null;
        mUsbInterface = null;
        mDeviceConnection = null;
        justEnumerate = false;
        enumerateDevice();
    }

    private static class Commands {
        public static final byte[] OUT_S = new byte[]{(byte) 0x02, (byte) 0x00};
        public static final byte[] IN = new byte[]{(byte) 0x02, (byte) 0x00};
    }
}
