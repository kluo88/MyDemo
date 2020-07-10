package com.itkluo.demo.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itkluo.demo.R;


/**
 * @author luobingyong
 * @date 2020/4/20
 */
public class TitleBar extends RelativeLayout {
    private ViewGroup title_llyt;
    private LinearLayout backLlyt;
    private ImageView back_iv;
    private ImageView rightIv1;
    private ImageView rightIv2;
    private TextView rightTv;
    private TextView titleTv;
    private TextView leftTv;
    private View title_h_line;
    private Context mContext;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = inflate(context, R.layout.title_bar, this);
        initTitleLayout(view);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            if (attr == R.styleable.TitleBar_title) {
                titleTv.setText(array.getString(attr));
            } else if (attr == R.styleable.TitleBar_show_h_line) {
                title_h_line.setVisibility(array.getBoolean(attr, false) ? VISIBLE : GONE);
            }
        } //释放资源
        array.recycle();
    }

    private void initTitleLayout(View view) {
        title_llyt = view.findViewById(R.id.title_bar_layout);
        title_h_line = view.findViewById(R.id.title_h_line);
        backLlyt = (LinearLayout) view.findViewById(R.id.back_llyt);
        back_iv = (ImageView) view.findViewById(R.id.back_iv);
        titleTv = (TextView) view.findViewById(R.id.titile_tv);
        rightIv1 = (ImageView) view.findViewById(R.id.right1_iv);
        rightIv2 = (ImageView) view.findViewById(R.id.right2_iv);
        rightTv = (TextView) view.findViewById(R.id.right_tv);
        leftTv = (TextView) view.findViewById(R.id.left_tv);
        backLlyt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null || !mListener.onClickBackBtn()) {
                    if (getContext() instanceof Activity) {
                        Activity activity = (Activity) getContext();
                        if (!activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            }
        });

    }


    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void setTitle(int resId) {
        if (resId != 0) {
            titleTv.setText(mContext.getString(resId));
        }
    }

    public void setBackIv(int resId) {
        back_iv.setImageResource(resId);
    }

    public void setRightIv1(int resId) {
        rightIv1.setVisibility(View.VISIBLE);
        rightIv1.setImageResource(resId);
        rightIv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRightIv1();
                }
            }
        });

    }

    public void setRightIv2(int resId) {
        rightIv2.setVisibility(View.VISIBLE);
        rightIv2.setImageResource(resId);
        rightIv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRightIv2();
                }
            }
        });
    }

    public void setRightTv(int resId) {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(resId);
        rightTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRightTv();
                }
            }
        });
    }

    public void setRightTv(String str) {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(str);
        rightTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickRightTv();
                }
            }
        });
    }

    public void setLeftTv(int resId) {
        leftTv.setVisibility(View.VISIBLE);
        leftTv.setText(resId);
    }

    public void setLeftTv(String str) {
        leftTv.setVisibility(View.VISIBLE);
        leftTv.setText(str);
    }

    public void setShow_h_line(boolean b) {
        title_h_line.setVisibility(b ? VISIBLE : GONE);
    }

    public TitleBar setOnTitleBarListener(OnTitleBarListener l) {
        mListener = l;
        return this;
    }

    private OnTitleBarListener mListener;

    public interface OnTitleBarListener {
        /**
         * 左项被点击
         *
         * @return
         */
        boolean onClickBackBtn();

        /**
         * 右项文字被点击
         */
        void onClickRightTv();

        /**
         * 右项图标被点击
         */
        void onClickRightIv1();

        /**
         * 右项图标被点击
         */
        void onClickRightIv2();
    }

    public abstract static class TitleBartListenerAdapter implements OnTitleBarListener {
        /**
         * 如要自己处理返回事件就复写并返回true
         *
         * @return
         */
        @Override
        public boolean onClickBackBtn() {
            return false;
        }

        @Override
        public void onClickRightTv() {

        }

        @Override
        public void onClickRightIv1() {

        }

        @Override
        public void onClickRightIv2() {

        }
    }
}
