package com.itkluo.demo.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;

import com.itkluo.demo.utils.DebugLog;

import java.util.HashMap;

/**
 * @author luobingyong
 * @date 2020/7/9
 */
public class UsbDevPermissionUtil {
    private static final String TAG = "UsbDevUtil";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";//可自定义
    private UsbManager mUsbManager;
    private Context mContext;
    private int mVendorId = -1;
    private int mProductId = -1;
    private UsbDevListener mUsbDevListener;
    private UsbDeviceConnection mDeviceConnection;
    private final PendingIntent mPermissionIntent;
    private UsbDevice mUsbDevice;
    private boolean isOpen;
    private boolean isReading;


    public UsbDevPermissionUtil(Context context) {
        mContext = context;
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //注册usb权限广播
        IntentFilter permissionFilter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbPermissionReceiver, permissionFilter);
        //注册插拔广播
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbFilter.addAction(VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
        mContext.registerReceiver(mUsbReceiver, usbFilter);
    }

    public void unUsbDevPermission() {
        if (mContext != null) {
            mContext.unregisterReceiver(mUsbReceiver);
            mContext.unregisterReceiver(mUsbPermissionReceiver);
        }
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
                        DebugLog.d(TAG, "permission request get" + usbDevice);
                        if (usbDevice != null && usbDevice.equals(mUsbDevice)) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant permission for this usb device
//                        Toast.makeText(context, String.valueOf("Permission denied for device" + usbDevice), Toast.LENGTH_LONG).show();
                        DebugLog.d(TAG, "permission denied for device " + usbDevice);
                        if (mUsbDevListener != null) {
                            mUsbDevListener.onDevDenial(usbDevice);
                        }
                    }
                }

            }//synchronized

        }
    };

    // 注1：UsbManager.ACTION_USB_DEVICE_ATTACHED对应的广播在USB每次插入时都能监听到，所以用这个就可以监听USB插入。
    // 注2：UsbManager.ACTION_USB_DEVICE_DETACHED用来监听USB拔出广播。

    private static int sVolumeState = -1;

    /**
     * 获取usb插拔广播
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            synchronized (this) {
                UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                switch (intent.getAction()) {
                    //USB设备插入
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        //网上说要等设备初始化和挂载完毕，大概在2-3s左右，再去读写，
                        // TODO: 2020/7/9
//                        searchUsb();
                        break;
                    //USB设备拔出
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
//                        closeUsbService();
                        if (usbDevice != null && usbDevice.equals(mUsbDevice)) {
                            if (mUsbDevListener != null) {
                                mUsbDevListener.onUsbDevDetached();
                            }
                        }
                        break;
                    //USB设备绑定
                    case VolumeInfo.ACTION_VOLUME_STATE_CHANGED:
                        int state = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE, VolumeInfo.STATE_UNMOUNTED);
                        if (sVolumeState == VolumeInfo.STATE_UNMOUNTED && state == VolumeInfo.STATE_MOUNTED) {
                        }
                        sVolumeState = state;
                        break;
                }

            }//synchronized

        }
    };

    /**
     * 搜索usb设备，授予权限
     */
    public int searchUsb() {

        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            DebugLog.i(TAG, "UsbManager is null");
            return -1;
        }

        //here do emulation to ask all connected usb device for permission
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.isEmpty()) {
            DebugLog.i(TAG, "No UsbDevice");
            return -RetCode.ERR_NOT_FOUND_USBDEVICE;
        }
        for (final UsbDevice usbDevice : deviceList.values()) {
            //add some conditional check if necessary
            if (isWeCaredUsbDevice(usbDevice)) {
                if (mUsbManager.hasPermission(usbDevice)) {
                    //if has already got permission, just goto connect it
                    //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
                    //and also choose option: not ask again
                    afterGetUsbPermission(usbDevice);
                    // TODO: 2020/7/10 只取第一个
                    return 1;
                } else {
                    //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
                    mUsbDevice = usbDevice;
                    mUsbManager.requestPermission(usbDevice, mPermissionIntent);
                    // TODO: 2020/7/10 只取第一个
                    return 1;
                }
            }
        }
        return -RetCode.ERR_NOT_FOUND_USBDEVICE;
    }

    private boolean isWeCaredUsbDevice(UsbDevice usbDevice) {
        if (usbDevice.getVendorId() == mVendorId) {
            if (mProductId == -1 || usbDevice.getProductId() == mProductId) {
                return true;
            }
        }
        return false;
    }

    private void afterGetUsbPermission(UsbDevice usbDevice) {
        //call method to set up device communication
        //Toast.makeText(this, String.valueOf("Got permission for usb device: " + usbDevice), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();
        mUsbDevice = usbDevice;
        if (mUsbDevListener != null) {
            mUsbDevListener.onDevPermit(usbDevice);
        }
//        doYourOpenUsbDevice(usbDevice);
    }

//    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
//        //now follow line will NOT show: User has not given permission to device UsbDevice
//        UsbDeviceConnection conn = null;
//        try {
//            conn = mUsbManager.openDevice(usbDevice);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (conn != null) {
//            mDeviceConnection = conn;
//            if (mUsbDevListener != null) {
//                //add your operation code here
//                mUsbDevListener.onOpenDevSuccess();
//    isOpen = true;
//            }
//        } else {
//            if (mUsbDevListener != null) {
//                //add your operation code here
//                mUsbDevListener.onOpenDevFail();
//            }
//        }
//    }

//    public int CloseHIDReader() {
//        if (mDeviceConnection != null) {
//            mDeviceConnection.close();
//            mDeviceConnection = null;
//            isOpen = false;
//        }
//        return 0;
//    }

//    public boolean isOpen() {
//        if ((mUsbManager != null) && (mUsbDevice != null)
//                /* && (mInterface != null) */
//                && (mDeviceConnection != null) && (mUsbManager.hasPermission(mUsbDevice))) {
//            return true;
//        }
//        return false;
//    }


    //初始化设备
//    private void initDevice(UsbDevice usbDevice) {
//        UsbInterface usbInterface = usbDevice.getInterface(0);
//        UsbEndpoint ep = usbInterface.getEndpoint(0);
//        if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
//
//            if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
//                mUsbEndpointIn = ep;
//            } else {
//                mUsbEndpointOut = ep;
//            }
//            if ((null == mUsbEndpointIn)) {
//                mUsbEndpointIn = null;
//                mUsbInterface = null;
//            } else {
//                mUsbInterface = usbInterface;
//                mUsbDeviceConnection = mUsbManager.openDevice(usbDevice);
//
//                startReading();
//            }
//        }
//    }
//
//    //开线程读取数据
//    private void startReading() {
//        mUsbDeviceConnection.claimInterface(mUsbInterface, true);
//
//        isReading = true;
//
//        final StringBuffer qr = new StringBuffer();
//
//        mReadingthread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isReading) {
//                    synchronized (this) {
//                        byte[] bytes = new byte[mUsbEndpointIn.getMaxPacketSize()];
//                        int ret = mUsbDeviceConnection.bulkTransfer(mUsbEndpointIn, bytes, bytes.length, 100);
//
//
//                        if (ret > 0) {
//                            StringBuilder stringbuilder = new StringBuilder(bytes.length);
//                            for (byte b : bytes) {
//                                if (b != 0) {
//                                    if (b == 2) {
//                                        stringbuilder.append("da");
//                                    }
//                                    stringbuilder.append(Integer.toHexString(b));
//
//                                }
//                            }
//
//                            //最终处理数据
//                            DebugLog.d(TAG, stringbuilder.toString());
//                        }
//                    }
//
//                }
//                mUsbDeviceConnection.close();
//            }
//
//        });
//
//        mReadingthread.start();
//    }

    //关闭usb服务
    private void closeUsbService() {
        if (isReading == true) {
            isReading = false;
        }
    }

    public void setVendorId(int vendorId) {
        mVendorId = vendorId;
    }

    public void setProductId(int productId) {
        mProductId = productId;
    }

    public UsbManager getUsbManager() {
        return mUsbManager;
    }

    public void setUsbDevListener(UsbDevListener usbDevListener) {
        mUsbDevListener = usbDevListener;
    }

    public interface UsbDevListener {

        /**
         * 设备权限允许
         */
        void onDevPermit(UsbDevice usbDevice);

        /**
         * 设备权限拒绝
         */
        void onDevDenial(UsbDevice usbDevice);

//        void onOpenDevSuccess();
//
//        void onOpenDevFail();

        void onUsbDevDetached();
    }

    public static class VolumeInfo {
        /**
         * Unmounted
         */
        public static final int STATE_UNMOUNTED = 0;

        /**
         * Checking
         */
        public static final int STATE_CHECKING = 1;

        /**
         * Mounted
         */
        public static final int STATE_MOUNTED = 2;

        /**
         * Read only
         */
        public static final int STATE_MOUNTED_READ_ONLY = 3;

        /**
         * Formatting
         */
        public static final int STATE_FORMATTING = 4;

        /**
         * Ejecting
         */
        public static final int STATE_EJECTING = 5;
        /**
         * Not mountable
         */
        public static final int STATE_UNMOUNTABLE = 6;

        /**
         * Removed
         */
        public static final int STATE_REMOVED = 7;

        /**
         * Remove fail
         */
        public static final int STATE_BAD_REMOVAL = 8;

        /**
         * Volume state changed broadcast action.
         */
        public static final String ACTION_VOLUME_STATE_CHANGED =
                "android.os.storage.action.VOLUME_STATE_CHANGED";

        public static final String EXTRA_VOLUME_ID =
                "android.os.storage.extra.VOLUME_ID";

        public static final String EXTRA_VOLUME_STATE =
                "android.os.storage.extra.VOLUME_STATE";
    }

}
