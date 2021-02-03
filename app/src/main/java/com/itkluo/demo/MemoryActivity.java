package com.itkluo.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.itkluo.demo.model.memorytest.MemorySingleton;

/**
 * 内存分析 MAT
 * Created by luobingyong on 2018/12/3.
 */
public class MemoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        MemorySingleton.getInstance();
    }
}
