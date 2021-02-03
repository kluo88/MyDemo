package com.itkluo.demo;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itkluo.demo.widget.FlowLayout.FlowLayout;
import com.itkluo.demo.widget.FlowLayout.ResourceTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 带收缩箭头的FlowLayout
 * Created by luobingyong on 2018/12/1.
 */
public class FlowLayoutActivity extends AppCompatActivity {
    private List<String> historyStrList = new ArrayList<>();
    private FlowLayout flowlayout;
    private EditText edit_search;
    private ImageView arrowImageView;
    private EditText edit_add_num;
    private Button btn_add_some_data;
    private TextView tv_log_tip;
    private int contractRow = 2;//flowlayout收缩时行数
    private int extendRow = 5; //flowlayout展开时最大行数
    private int historyCount = 50;
    private boolean isOverExtendRow = false;//flowlayout是否到达最大行数
    private boolean isExtend = true;//flowlayout是否展开状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowlayout);
        initData();
        edit_add_num = (EditText) findViewById(R.id.edit_add_num);
        btn_add_some_data = findViewById(R.id.btn_add_some_data);
        tv_log_tip = findViewById(R.id.tv_log_tip);
        int num = Integer.parseInt(edit_add_num.getText().toString());
        if (num == 0) {
            num = 4;
        }
        btn_add_some_data.setText(String.format("添加条%d数据", num));
        edit_add_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    int num = Integer.parseInt(edit_add_num.getText().toString());
                    btn_add_some_data.setText(String.format("添加条%d数据", num));
                } else {
                }

            }
        });


        edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchKey = edit_search.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchKey)) {
                        addHistoryList(searchKey);
                        setHistoryData(historyStrList);
                    }
                    // 先隐藏键盘
                    ((InputMethodManager) edit_search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(FlowLayoutActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    String s = String.valueOf(edit_search.getText());

                    return true;
                }
                return false;
            }
        });
        arrowImageView = findViewById(R.id.arrow);
        flowlayout = (FlowLayout) findViewById(R.id.flowLayout);
        flowlayout.setMaxRows(extendRow);
        setHistoryData(historyStrList);
        flowlayout.setFlowLayoutListener(new FlowLayout.FlowLayoutListener() {
            @Override
            public void onOverFlow() {
                tv_log_tip.setText(String.format("嗖嗖, 超过%d行了~~", extendRow));
                isOverExtendRow = true;
                arrowImageView.setVisibility(View.VISIBLE);
            }
        });
        //默认展开状态
        if (isExtend) {
            arrowImageView.animate().setDuration(500).rotation(180).start();
        }
    }

    private void initData() {
        historyStrList.clear();
        for (int i = 0; i < 4; i++) {
            historyStrList.add(String.format("第%d条数据%s", i, i / 2 == 0 ? "哈哈" : "吼吼吼吼"));
        }
    }

    private void setHistoryData(final List<String> dataS) {
        tv_log_tip.setText("");
        isOverExtendRow = false;
        arrowImageView.setVisibility(View.GONE);
        flowlayout.removeAllViews();
        for (int i = 0; i < dataS.size(); i++) {
            final ResourceTextView resourceTextView = new ResourceTextView(this, false);
            resourceTextView.getReTextView().setText(dataS.get(i));
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dip2px(this, 10), dip2px(this, 5), dip2px(this, 5), dip2px(this, 10));
            final int finalI = i;
            resourceTextView.setmOnSelectedListener(new ResourceTextView.OnSelectedListener() {
                @Override
                public void OnSelected(boolean isSelected) {
//                    String str = dataS.get(finalI);
//                    edit_search.setText(str);
                    vali(flowlayout, finalI);
                }
            });
            flowlayout.addView(resourceTextView, lp);
        }
    }

    private void vali(FlowLayout mFlowLayout, int curSelect) {
        for (int k = 0; k < mFlowLayout.getChildCount(); k++) {
            ResourceTextView button = (ResourceTextView) mFlowLayout.getChildAt(k);
            if (k != curSelect) {
                button.getReTextView().setTextColor(getResources().getColor(R.color.custom_gray_color));
                button.setBgResource(R.drawable.bg_resource_screen__button);
            } else {
                button.getReTextView().setTextColor(getResources().getColor(R.color.white));
                button.setBgResource(R.drawable.bg_resource_screen__button_select);
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void addHistoryList(String str) {
        int index = historyStrList.indexOf(str);
        if (index != -1) { //有重复先删除
            historyStrList.remove(index);
        } else if (historyStrList.size() >= historyCount) {//超出, 删除最后一个
            historyStrList.remove(historyStrList.size() - 1);
        }
        historyStrList.add(0, str);//添加在最前面
    }


    /**
     ****************************************** 点击事件 **************************************
     */

    public void refill(View view) {
        initData();
        setHistoryData(historyStrList);
    }

    public void clickArrow(View view) {
        if (isExtend) {//当前为展开状态, 点击就收缩
            flowlayout.setMaxRows(contractRow);
            setHistoryData(historyStrList);
            arrowImageView.animate().setDuration(500).rotation(0).start();
            isExtend = !isExtend;
        } else {//当前为收缩状态, 点击就展开
            flowlayout.setMaxRows(extendRow);
            setHistoryData(historyStrList);
            arrowImageView.animate().setDuration(500).rotation(180).start();
            isExtend = !isExtend;
        }
    }

    public void clickClear(View view) {
        historyStrList.clear();
        setHistoryData(historyStrList);
    }

    public void addSomeData(View view) {
        int num = Integer.parseInt(edit_add_num.getText().toString());
        for (int i = 0; i < num; i++) {
            String str = String.format("第%d条数据%s", historyStrList.size(), i / 2 == 0 ? "哈哈" : "吼吼吼吼");
            addHistoryList(str);
        }
        setHistoryData(historyStrList);
    }
}
