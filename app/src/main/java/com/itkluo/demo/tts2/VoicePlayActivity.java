package com.itkluo.demo.tts2;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.itkluo.demo.MyApplication;
import com.itkluo.demo.R;
import com.itkluo.demo.tts.SoundPoolUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 拼接文字tts功能，总结一下:
 * 1.MediaPlayer播放下一段音频间会有延迟
 * 2.后来改SoundPool办法，但没有一段音频播完接口，所以利用MediaPlayer获取播放时长，定时播放下一段音频。
 * 3.前两天参考支付宝的做法，先把几段音频用流合成，再MediaPlayer播放。
 * 最后，2，3方式能流畅播放，2缺点是加速播放不好控制
 * 4.缓存两个MediaPlayer循环交替播放方式，应该可以没试
 * @author luobingyong
 * @date 2020/6/18
 */
public class VoicePlayActivity extends AppCompatActivity {
    private boolean mCheckNum;

    private EditText editText;
    private Button btPlay;
    private Button btDel;
    private LinearLayout llMoneyList;
    private Switch switchView;
    private Button btnStreamPlay;
    private VoiceBuilder mVoiceBuilder;
    private int type;
    private Context mContext;

    InputStream f71773e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_voice_play);
        type = getIntent().getIntExtra("type", 0);
        initView();
        initClick();
    }

    void initView() {
        editText = findViewById(R.id.edittext);
        btPlay = findViewById(R.id.bt_play);
        btDel = findViewById(R.id.bt_del);
        llMoneyList = findViewById(R.id.ll_money_list);
        switchView = findViewById(R.id.switch_view);
        btnStreamPlay = findViewById(R.id.btnStreamPlay);
    }

    void initClick() {
        if (type == 0) {
            btPlay.setText("MediaPlayer方式播放");
        } else {
            btPlay.setText("SoundPool方式播放");
        }

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = editText.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
//                    Toast.makeText(VoicePlayActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
//                    return;
                    amount = "128";
                    editText.setText(amount);
                }


                if (type == 0) {//MediaPlayer播放
//            VoicePlay.with(MainActivity.this).play(amount, mCheckNum);

                    mVoiceBuilder = new VoiceBuilder.Builder()
//                        .start(AudioCode.audio_idcheck_main, AudioCode.audio_idcheck_temp_hint)
                            .number(amount)
//                        .unit(AudioCode.audio_unit_du)
                            .isOriginNum(mCheckNum)
//                        .end(AudioCode.audio_idcheck_pass)
                            .builder();
                    VoicePlayService.getInstance().play(mVoiceBuilder);

                    llMoneyList.addView(getTextView(amount), 0);
                } else {//SoundPool播放
                    mVoiceBuilder = new VoiceBuilder.Builder()
//                            .start(AudioCode.audio_idcheck_temp_hint)
                            .number(amount)
//                            .unit(AudioCode.audio_unit_du)
                            .isOriginNum(mCheckNum)
//                            .end(AudioCode.audio_idcheck_temp_low, AudioCode.audio_idcheck_not_pass)
                            .builder();
                    SoundPoolUtil.getInstance().play(mVoiceBuilder);

                    llMoneyList.addView(getTextView(amount), 0);
                }


//            editText.setText("");
            }
        });


        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMoneyList.removeAllViews();
            }
        });

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckNum = isChecked;
            }
        });

        //Java合并流方式，拼接几段音频为一段音频文件
        mergeSoundFile();

    }

    private TextView getTextView(String amount) {
        StringBuffer text = new StringBuffer()
                .append("索引: ").append(llMoneyList.getChildCount())
                .append("\n")
                .append("输入数字: ").append(amount)
                .append("\n");
        if (mCheckNum) {
            text.append("全数字读法: ").append(VoiceTextTemplate.genVoiceList(mVoiceBuilder).toString());
        } else {
            text.append("中文读法: ").append(VoiceTextTemplate.genVoiceList(mVoiceBuilder).toString());
        }

        TextView view = new TextView(VoicePlayActivity.this);
        view.setPadding(0, 8, 0, 0);
        view.setText(text.toString());
        return view;
    }

    /**
     * 另一个播放方式：Java合并流方式，拼接几段音频为一段音频文件
     * 和SoundPool的播放速度一样，两个音频间基本没什么延迟。
     * 但不像SoundPoolUtil是没完成的回调（自己计算每个音频播放时间接着播放下一个）
     * 当需要变速播放时，这个简单就可以达到
     */
    private void mergeSoundFile() {
        btnStreamPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                List<String> fileNames = new ArrayList<>();
                fileNames.add("tts/0/mp3/tts_8.mp3");
                fileNames.add("tts/0/mp3/tts_1.mp3");
                fileNames.add("tts/0/mp3/tts_2.mp3");
                fileNames.add("tts/0/mp3/tts_3.mp3");
                fileNames.add("tts/0/mp3/tts_dot.mp3");
                fileNames.add("tts/0/mp3/tts_6.mp3");
                fileNames.add("tts/0/mp3/tts_9.mp3");

//                fileNames.add("tts/0/copymymp3/mp3_key_code_1.mp3");
//                fileNames.add("tts/0/copymymp3/mp3_key_code_2.mp3");
//                fileNames.add("tts/0/copymymp3/mp3_key_code_3.mp3");
//                fileNames.add("tts/0/copymymp3/mp3_key_code_dot.mp3");
//                fileNames.add("tts/0/copymymp3/mp3_key_code_6.mp3");
//                fileNames.add("tts/0/copymymp3/mp3_key_code_9.mp3");


                for (String name : fileNames) {
                    try {
                        FileInputStream createInputStream = mContext.getAssets().openFd(name).createInputStream();
                        if (f71773e == null) {
                            f71773e = new BufferedInputStream(createInputStream);
                        } else {
//                            createInputStream.skip(6);
                            f71773e = new SequenceInputStream(f71773e, new BufferedInputStream(createInputStream));
                        }
                    } catch (IOException e) {
                        Log.e("SoundFileMergeAmr", e.toString());
                        Log.e("SoundFileMergeAmr", "合并流失败,合并字符：");
                    }
                }
                Log.i("SoundFileMergeAmr", "合并流结束");
                Log.i("SoundFileMergeAmr", "开始合并成File文件");
                int read = 0;
                File file = new File(mContext.getCacheDir(), "mediaCache");
                if (!file.exists() && !file.mkdirs()) {
                    Log.e("SoundFileMergeAmr", "Path not exist but fail to create: " + file);
                }
                File file2 = new File(file, "voice_merged.amr");
                BufferedOutputStream bufferedOutputStream = null;
                try {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] bArr = new byte[8192];
                do {
                    try {
                        read = f71773e.read(bArr, 0, 8192);
                        if (read != -1) {
                            bufferedOutputStream.write(bArr, 0, read);
                            continue;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } while (read != -1);
                try {
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (file2.exists()) {
                    Log.i("SoundFileMergeAmr", "合并成File文件成功");
                    Log.i("SoundFileMergeAmr", "开始播放合并的音频File文件");
                    try {
                        startPlayFile(file2);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("SoundFileMergeAmr", e.toString());
                        Log.i("SoundFileMergeAmr", "播放合并的音频File文件失败");
                    }

                } else {
                    Log.i("SoundFileMergeAmr", "合并成File文件失败");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPlayFile(File file) throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(MyApplication.getInstance(), 1);
        Log.i("Spokesman", "Play voice use wakeup lock!");
        Log.i("Spokesman", "real play with sound by buffer");
        MediaDataSource r0;
        //转换File为byte
        byte[] bytes = new byte[0];
        final byte[] f71821c = bytes;

        r0 = new MediaDataSource() {
            public final long getSize() {
                return (long) f71821c.length;
            }

            public final int readAt(long j, byte[] bArr, int i, int i2) {
                if (bArr == null) {
                    throw new NullPointerException();
                } else if (i < 0 || i2 < 0 || i2 > bArr.length - i) {
                    throw new IndexOutOfBoundsException();
                } else if (j < 0) {
                    throw new IndexOutOfBoundsException();
                } else if (j >= ((long) f71821c.length)) {
                    return -1;
                } else {
                    long length = ((long) f71821c.length) - j;
                    long j2 = (long) i2;
                    if (j2 <= length) {
                        length = j2;
                    }
                    if (length <= 0) {
                        return 0;
                    }
                    System.arraycopy(f71821c, (int) j, bArr, i, (int) length);
                    return (int) length;
                }
            }

            public final void close() {
            }
        };
//        mediaPlayer.setDataSource(r0);

        String path = file.getPath();
        Log.i("Spokesman", "startPlayFile: " + path);
        mediaPlayer.setDataSource(path);

        mediaPlayer.prepare();
//        mediaPlayer.setOnCompletionListener();
        mediaPlayer.start();

    }

}
