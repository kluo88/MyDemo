package com.itkluo.demo.tts2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.itkluo.demo.R;
import com.itkluo.demo.tts.AudioCode;
import com.itkluo.demo.tts.SoundPoolUtil;

/**
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
    private VoiceBuilder mVoiceBuilder;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    void initClick() {
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = editText.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
//                    Toast.makeText(VoicePlayActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
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
                            .isOriginNum(false)
//                        .end(AudioCode.audio_idcheck_pass)
                            .builder();
                    VoicePlayService.getInstance().play(mVoiceBuilder);

                    llMoneyList.addView(getTextView(amount), 0);
                } else {//SoundPool播放
                    mVoiceBuilder = new VoiceBuilder.Builder()
                            .start(AudioCode.audio_idcheck_temp_hint)
                            .number(amount)
                            .unit(AudioCode.audio_unit_du)
                            .isOriginNum(false)
                            .end(AudioCode.audio_idcheck_temp_low, AudioCode.audio_idcheck_not_pass)
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

    }

    TextView getTextView(String amount) {
        StringBuffer text = new StringBuffer()
                .append("角标: ").append(llMoneyList.getChildCount())
                .append("\n")
                .append("输入金额: ").append(amount)
                .append("\n");
        if (mCheckNum) {
            text.append("全数字式: ").append(VoiceTextTemplate.genVoiceList(mVoiceBuilder).toString());
        } else {
            text.append("中文样式: ").append(VoiceTextTemplate.genVoiceList(mVoiceBuilder).toString());
        }

        TextView view = new TextView(VoicePlayActivity.this);
        view.setPadding(0, 8, 0, 0);
        view.setText(text.toString());
        return view;
    }
}
