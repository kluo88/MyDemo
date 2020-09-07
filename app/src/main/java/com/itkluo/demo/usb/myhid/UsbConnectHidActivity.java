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
    private final int mVendorId = 21832;
    private final int mProductId = 26214;
    private UsbEndpoint mUsbEndpointOut;
    private UsbEndpoint mUsbEndpointIn;
    private byte[] mSendBytes;
    private boolean justEnumerate;
    private int mInMaxPacketSize;
    private int mOutMaxPacketSize;

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
            UsbDevice findDevice = null;
            for (UsbDevice device : deviceList.values()) {
//                tv_info.append(device.getDeviceName() + "\nVendorId: :" + device.getVendorId()
//                        + "\nProductId:" + device.getProductId()+"\n");
                // 输出设备信息
                Log.d(TAG, "DeviceInfo: " + device.getVendorId() + " , "
                        + device.getProductId());

                // 枚举到设备
                if (device.getVendorId() == mVendorId && device.getProductId() == mProductId) {
                    findDevice = device;
                    break;
                } else {
//
                }
            }

            if (findDevice != null) {
                mUsbDevice = findDevice;
                Toast.makeText(this, "枚举设备成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "枚举设备成功");
                findInterface();
            } else {
//                Toast.makeText(this, "Not Found VID and PID", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Not Found VID and PID");
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

        for (int i = 0; ; i++) {
            UsbInterface intf = mUsbDevice.getInterface(i);

            // 根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
//            if (intf.getInterfaceClass() == 3
//                    && intf.getInterfaceSubclass() == 0
//                    && intf.getInterfaceProtocol() == 0) {
            mUsbInterface = intf;
            Log.d(TAG, "找到我的设备接口");
            if (!justEnumerate) {
                openDevice();
            }
//            }
            break;
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
            assignEndpoint();
        } else {
            conn.close();
        }
    }

    /**
     * 拿到端点，用bulkTransfer进行数据发收
     */
    private void assignEndpoint() {
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

    /**
     * ***************************************** 发送命令  **************************************
     */
    public void connect_dev(View view) {
        if (!isConnectValid()) {
            return;
        }
        Log.d(TAG, "connect_dev");
        String cmdStr = "5302a634040000000700000000000000fffefdfc000000000000000000000000";
        transport(cmdStr);
    }

    public void disconnect_dev(View view) {
        if (!isConnectValid()) {
            return;
        }
        Log.d(TAG, "disconnect_dev");
        String cmdStr = "5302a634040000000700000000000000fcfdfeff000000000000000000000000";
        transport(cmdStr);
    }

    public void get_session_key(View view) {
        if (!isConnectValid()) {
            return;
        }
        Log.d(TAG, "disconnect_dev");
        String cmdStr = "5302a6348600000006000000000000000047474b80f83670762e1bececa26fc3064682173ba60251291c27d85728a1d8859f23bc2b9073af1f3a21eca2caa222605f7dbb0e79e6bfa11bc43e2b785a7d0c72e9fa3b9a3f2c5143a448e85743e38e68025ca8855e69e624ab89681489a16e7c5b4ae820a7960fb77d32fcf441192eb53afb6d27d969dea48f53c085756ffaa7cf692580";
        transport(cmdStr);
    }

    public void readData(View view) {
        if (!isConnectValid()) {
            return;
        }
        Log.d(TAG, "readData");
        byte[] reByte = new byte[1024];
        int re2 = mDeviceConnection.bulkTransfer(mUsbEndpointIn, reByte, reByte.length, 3000);
        Log.i("reByte", "re2" + re2 + "\n" + HexStringUtil.bytesToHexStringForShow(reByte));
        Toast.makeText(UsbConnectHidActivity.this, "接收" + re2, Toast.LENGTH_SHORT).show();
        tv_info.setText("");
        tv_info.append("\n接收的数据：" + HexStringUtil.bytesToHexStringForShow(reByte));
        tv_info.append("\n返回码：" + "re2=" + re2);
    }

    private void transport(String cmdStr) {
        byte[] comm;
        byte[] resp = new byte[256];
        comm = HexStringUtil.hexStringToByte(cmdStr);
//        int wCmdLen = comm.length / 2;//写入命令的长度
//        byte[] ucOutBuf = new byte[65];//分段写入的数据
        int re = mDeviceConnection.bulkTransfer(mUsbEndpointOut, comm, comm.length, 3000);
        if (re < 0) {
            return;
        }

        byte[] reByte = new byte[mInMaxPacketSize];//1024
        int re2 = mDeviceConnection.bulkTransfer(mUsbEndpointIn, reByte, reByte.length, 3000);
        Log.i("reByte", "re" + re + "re2" + re2 + "\n" + HexStringUtil.bytesToHexStringForShow(reByte));
        Toast.makeText(UsbConnectHidActivity.this, "接收" + re2 +" "+ mInMaxPacketSize, Toast.LENGTH_SHORT).show();

        tv_info.setText("");
        tv_info.append("\n发送数据：" + HexStringUtil.bytesToHexStringForShow(comm));
        tv_info.append("\n返回码：" + "re=" + re);
        tv_info.append("\n");
        tv_info.append("\n接收的数据：" + HexStringUtil.bytesToHexStringForShow(reByte));
        tv_info.append("\n返回码：" + "re2=" + re2);
    }

    private boolean isConnectValid() {
        if (mUsbEndpointOut == null || mUsbEndpointIn == null) {
            if (mDeviceConnection != null) {
                mDeviceConnection.close();
            }
            Log.d(TAG, "get endpoint failed");
            Toast.makeText(this, "获取端口失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private static class Commands {
        public static final byte[] OUT_S = new byte[]{(byte) 0x02, (byte) 0x00};
        public static final byte[] IN = new byte[]{(byte) 0x02, (byte) 0x00};
    }
}
