package com.itkluo.demo.widget.timer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 抢购倒计时view
 * Created by luobingyong on 2019/1/11.
 */
public class CountDownTimerView extends LinearLayout {
    private TextView tv_hour_decade;
    private TextView tv_hour_unit;
    private TextView tv_min_decade;
    private TextView tv_min_unit;
    private TextView tv_sec_decade;
    private TextView tv_sec_unit;
    private Context context;
    private int hour_decade;
    private int hour_unit;
    private int min_decade;
    private int min_unit;
    private int sec_decade;
    private int sec_unit;

    private Timer timer;

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            countDown();
        }
    };

    public CountDownTimerView(Context context) {
        this(context, null);
    }

    public CountDownTimerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.timer_countdown_layout, this);
        tv_hour_decade = (TextView) view.findViewById(R.id.tv_hour_decade);
        tv_hour_unit = (TextView) view.findViewById(R.id.tv_hour_unit);
        tv_min_decade = (TextView) view.findViewById(R.id.tv_min_decade);
        tv_min_unit = (TextView) view.findViewById(R.id.tv_min_unit);
        tv_sec_decade = (TextView) view.findViewById(R.id.tv_sec_decade);
        tv_sec_unit = (TextView) view.findViewById(R.id.tv_sec_unit);

//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountDownTimerView);
//        int size = array.getInteger(R.styleable.CountDownTimerView_timer_textSize, 12);

//        tv_hour_decade.setTextSize(size);
//        tv_hour_unit.setTextSize(size);
//        tv_min_decade.setTextSize(size);
//        tv_min_unit.setTextSize(size);
//        tv_sec_decade.setTextSize(size);
//        tv_sec_unit.setTextSize(size);
//        ((TextView)view.findViewById(R.id.colon_minute)).setTextSize(size);
//        ((TextView)view.findViewById(R.id.colon_hour)).setTextSize(size);
    }

    public void start() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000);
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setTime(int hour, int min, int sec) {

        if (hour >= 60 || min >= 60 || sec >= 60 || hour < 0 || min < 0
                || sec < 0) {
            throw new RuntimeException("时间格式错误,请检查你的代码");
        }

        hour_decade = hour / 10;
        hour_unit = hour - hour_decade * 10;

        min_decade = min / 10;
        min_unit = min - min_decade * 10;

        sec_decade = sec / 10;
        sec_unit = sec - sec_decade * 10;

        tv_hour_decade.setText(hour_decade + "");
        tv_hour_unit.setText(hour_unit + "");
        tv_min_decade.setText(min_decade + "");
        tv_min_unit.setText(min_unit + "");
        tv_sec_decade.setText(sec_decade + "");
        tv_sec_unit.setText(sec_unit + "");
    }

    private void countDown() {
        if (isCarry4Unit(tv_sec_unit)) {
            if (isCarry4Decade(tv_sec_decade)) {
                if (isCarry4Unit(tv_min_unit)) {
                    if (isCarry4Decade(tv_min_decade)) {
                        if (isCarry4Unit(tv_hour_unit)) {
                            if (isCarry4Decade(tv_hour_decade)) {
                                Toast.makeText(context, "计数完成", Toast.LENGTH_SHORT).show();
                                stop();
                                setTime(0, 0, 0);//重置为0
                                if (listener != null) {
                                    listener.onFinish();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCarry4Decade(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 5;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    private boolean isCarry4Unit(TextView tv) {
        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(time + "");
            return true;
        } else {
            tv.setText(time + "");
            return false;
        }
    }

    public void setListener(CountdownTimerListener listener) {
        this.listener = listener;
    }

    private CountdownTimerListener listener;

    public interface CountdownTimerListener {
        void onFinish();
    }


}
