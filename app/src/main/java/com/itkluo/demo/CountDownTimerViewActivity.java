package com.itkluo.demo;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.itkluo.demo.widget.timer.CountDownTimerView;

/**
 * Created by luobingyong on 2019/1/11.
 */
public class CountDownTimerViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer_view);
        CountDownTimerView countDownTimerView = findViewById(R.id.countDownTimerView);
        countDownTimerView.setTime(0, 0, 5);
        countDownTimerView.start();
        countDownTimerView.setListener(new CountDownTimerView.CountdownTimerListener() {
            @Override
            public void onFinish() {

            }
        });
    }
}
