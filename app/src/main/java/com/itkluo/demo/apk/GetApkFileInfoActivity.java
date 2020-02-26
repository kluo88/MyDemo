package com.itkluo.demo.apk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itkluo.demo.R;
import com.itkluo.demo.utils.ApkFileParser;

import java.io.File;

/**
 * 从一个未安装的apk文件获取apk信息, 生成json数据
 *
 * @author luobingyong
 * @date 2020/1/17
 */
public class GetApkFileInfoActivity extends AppCompatActivity {
    private static final String TAG = "GetApkFileInfoActivity";
    private TextView tv_result;
    private ImageView iv_icon;
    private String mApkFilePath = ApkFileParser.sApkFileDir + File.separator + "VEBAppStore.apk";
    private ArrayMap<String, File> mExpectingBetter = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_file_info);
        tv_result = findViewById(R.id.tv_result);
        iv_icon = findViewById(R.id.iv_icon);

        findViewById(R.id.btn_get_uninstall_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApkFileInfoData apkFileInfoData;
                apkFileInfoData = ApkFileParser.getUnInstallAPKInfo(GetApkFileInfoActivity.this, mApkFilePath);
                if (apkFileInfoData != null) {
                    Log.d(TAG, "apkFilePath: " + mApkFilePath + "-->" + apkFileInfoData.toString());
                    tv_result.setText("apkFilePath: " + mApkFilePath + "-->" + apkFileInfoData.toString());
                    iv_icon.setImageDrawable(apkFileInfoData.getAppIcon());
                }
            }
        });
        findViewById(R.id.btn_get_json).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_result.setText(ApkFileParser.getImpl().scanDirWithGetApkListJson(GetApkFileInfoActivity.this));
            }
        });
    }


}
