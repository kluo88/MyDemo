package com.itkluo.demo.widget.FlowLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itkluo.demo.R;

/**
 * Created by luobingyong on 2018/12/1.
 */
public class ResourceTextView extends LinearLayout {
    private TextView tv;
    private boolean isSelect=false;

    public ResourceTextView(Context context) {
        this(context, null);
    }

    public ResourceTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResourceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ResourceTextView(Context context, boolean isSelect) {
        this(context, null);
        this.isSelect = isSelect;
        mContext = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.textview_resource_layout, this, true);
        tv = (TextView) findViewById(R.id.tv_txt);
        tv.setOnClickListener(mListener);
    }


    private Context mContext;
    private OnSelectedListener mOnSelectedListener;

    public void setmOnSelectedListener(OnSelectedListener mOnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener;
    }

    public void setTextValue(String value) {
        tv.setText(value);
    }

    public void setTextColor(int color) {
        tv.setTextColor(color);
    }

    public void setBgResource(int resource) {
        tv.setBackgroundResource(resource);
    }

    public TextView getReTextView() {
        return tv;
    }

    public interface OnSelectedListener {
        public void OnSelected(boolean isSelected);
    }

    private OnClickListener mListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnSelectedListener != null) {
                if (!isSelect) {
                    tv.setTextColor(mContext.getResources().getColor(R.color.white));
                    tv.setBackgroundResource(R.drawable.bg_resource_screen__button_select);
                    isSelect = true;
                } else {
                    tv.setTextColor(mContext.getResources().getColor(R.color.custom_gray_color));
                    tv.setBackgroundResource(R.drawable.bg_resource_screen__button);
                    isSelect = false;
                }
                mOnSelectedListener.OnSelected(isSelect);
            }
        }
    };


}
