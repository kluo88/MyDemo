package com.itkluo.demo.exam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.itkluo.demo.R;

/**
 * •页面上现有ProgressBar控件progressBar，请用书写线程以10秒的的时间完成其进度显示工作
 */
public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        progressBar = findViewById(R.id.progressBar);
        thread = new Thread(new ProgressBarRunnable());
        thread.start();


    }

    private class ProgressBarRunnable implements Runnable {

        @Override
        public void run() {
            int maxProgress = progressBar.getMax();

            int stepProgress = maxProgress / 10;
            while (progressBar.getProgress() < maxProgress) {
                int curProgress = progressBar.getProgress();
                progressBar.setProgress(curProgress + stepProgress);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }
}
