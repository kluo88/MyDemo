package com.itkluo.demo.usb.wdreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itkluo.demo.BaseActivity;
import com.itkluo.demo.MyApplication;
import com.itkluo.demo.R;
import com.itkluo.demo.usb.UsbDevPermissionUtil;
import com.itkluo.demo.usb.wdreader.readerlib.HIDReader;
import com.itkluo.demo.utils.DebugLog;

/**
 * 握奇 读社保卡
 *
 * @author luobingyong
 * @date 2020/7/10
 */
public class WdSSCardActivity extends BaseActivity implements UsbDevPermissionUtil.UsbDevListener {
    private static final String TAG = "WdSSCardActivity";
    private LinearLayout llsvMainShow = null;
    private TextView resultInfo;
    private ImageView imagePhoto;
    private UsbDevPermissionUtil mUsbDevUtil;
    private static final int WD_VENDOR_ID = 5692;
    private int mVendorId;
    private int mProductId;
    private boolean isGetUsbDev;
    private boolean isOpen;
    private HIDReader wdhidreader;
    private UsbDevice mReaderDev;

    public WdSSCardActivity() {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sscard);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // 文本输入框默认不获得焦点
        llsvMainShow = (LinearLayout) findViewById(R.id.svNewMainShow);
        resultInfo = findViewById(R.id.resultInfo);
        imagePhoto = findViewById(R.id.imagePhoto);


        wdhidreader = new HIDReader();
        mVendorId = WD_VENDOR_ID;
        mUsbDevUtil = new UsbDevPermissionUtil(mContext);
        mUsbDevUtil.setProductId(mVendorId);
        connectDev();
    }

    private void connectDev() {
        mUsbDevUtil.setUsbDevListener(this);
        int retCode = mUsbDevUtil.searchUsb();

        if (retCode < 0) {
            showErr("连接失败，请重试", 0);
        } else {
            UsbManager usbManager = mUsbDevUtil.getUsbManager();
            if (usbManager != null) {
                wdhidreader.setMmanager(usbManager);
            }
            //设定定时搜索usb设备时间，超时判断为无连接
            MyApplication.sHandler.postDelayed(mNoDeviceTimer, 5000);
        }
    }

    private Runnable mNoDeviceTimer = new Runnable() {
        @Override
        public void run() {
            if (!isGetUsbDev) {
                isGetUsbDev = true;
                DebugLog.i(TAG, "没找到USB设备，请重连接！");
            }
        }
    };

    public void disconnectDev() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mUsbDevUtil.unUsbDevPermission();
            disconnectDev();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDevPermit(UsbDevice usbDevice) {
        isGetUsbDev = true;
        try {
            new OpenTask().execute(usbDevice);
        } catch (Exception e) {
            String mStrMessage = "Get Exception : " + e.getMessage();
            Log.e(TAG, mStrMessage);
        }
    }

    @Override
    public void onDevDenial(UsbDevice usbDevice) {
        isGetUsbDev = false;
    }

    @Override
    public void onUsbDevDetached() {
        closeReaderUp();
    }

    private class OpenTask extends AsyncTask<UsbDevice, Void, Integer> {

        @Override
        protected Integer doInBackground(UsbDevice... params) {
            int status = wdhidreader.OpenHIDReader(params[0]);
//			if (status == 0)
//			{
//				ReadVerinfText.setText("读写器连接成功");
//			} else
//			{
//				ReadVerinfText.setText("读写器连接失败");
//			}
            return status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != 0) {
                isOpen = false;
//                ReadVerinfText.setText("Open fail: " + Integer.toString(result));
                Log.e(TAG, "Open fail: " + Integer.toString(result));
            } else {
                isOpen = true;
                readSCInfo();
                Log.e(TAG, "Open successfully");
            }
        }
    }

    /**
     * 读社保卡信息
     */
    public void readSCInfo() {
        byte[] comm = new byte[256];
        String[] output = new String[20];
        int clen = 0;
        byte[] resp = new byte[256];
        int[] rlen = new int[1];
        String strtmp = "";
        int ret = 0;
        //wdhidreader.SetNad((byte) 0x12);
        //comm = HexString.hexStringToByte("0019000000");
        //clen = 5;
        //ret = wdhidreader.HIDTransmit(1, comm, clen, resp, rlen);
        //if(0x9000 != ret)
        //{
        //	wdhidreader.SetNad((byte) 0x00);
        //				comm = HexString.hexStringToByte("0019000000");
        //				clen = 5;
        //				ret = wdhidreader.HIDTransmit(1, comm, clen, resp, rlen);
        //			}
        //			if (0x9000 == ret)
        //			{
        //				for (int i = 0; i < rlen[0]; i++)
        //				{
        //					strtmp += (char) resp[i];
        //				}
        //
        //			} else
        //			{
        //				strtmp = "读版本信息失败";
        //			}
        ret = wdhidreader.ReadSSCard((byte) 0x15, output);
        if (ret == 0) {
            resultInfo.setText(output[0] + "|" + output[1] + "|" + output[2] + "|" + output[3] + "|" + output[4] + "|" + output[5] + "|" + output[6]);
        } else {
            resultInfo.setText("读社保卡信息失败" + ret);
        }


        if ("3".equals(output[7])) {
            String base64 = output[8];
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imagePhoto.setImageBitmap(bitmap);
            Log.d("fragment1 readerver", strtmp);
        }
    }

    /**
     * 卡号|身份证号|姓名|性别|民族|出生日期
     */
//    private void showResult(String curSCInfo) {
//        if (TextUtils.isEmpty(curSCInfo)) {
//            return;
//        }
//        String[] split = curSCInfo.split("\\|");
//
//        if (split.length < 6) {
//            return;
//        }
//        resultInfo.setText("社保卡卡号: " + split[0] + "\n"
//                        + "身份证号: " + split[1] + "\n"
//                        + "姓名: " + split[2] + "\n"
//                        + "性别: " + split[3] + "\n"
//                        + "民族: " + split[4] + "\n"
//                        + "出生日期: " + split[5] + "\n"
//                        + "银行卡号: " + mCardNoTip + "\n"
////                + "持卡人姓名: " + mNameTip + "\n"
//        );
//    }
    private int closeReaderUp() {
        Log.d(TAG, "Closing reader...");
        int ret = 0;
        if (wdhidreader != null) {
            ret = wdhidreader.CloseHIDReader();
            isGetUsbDev = false;
            isOpen = false;
        }
        return ret;
    }

    public void showErr(String str, int flag) {
    }

}
