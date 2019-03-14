package com.itkluo.demo.widget.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itkluo.demo.R;

import java.util.ArrayList;

/**
 * 标题按钮上的弹窗
 * Created by luobingyong on 2019/3/6.
 */
public class TitlePopupMenu extends PopupWindow {
    private Context mContext;
    //列表弹窗的间隔
    protected final int LIST_PADDING = 10;
    //实例化一个矩形
    private Rect mRect = new Rect();
    //坐标的位置（x、y）
    private final int[] mLocation = new int[2];
    //屏幕的宽度和高度
    private int mScreenWidth, mScreenHeight;
    //判断是否需要添加或更新列表子类项
    private boolean mIsDirty;
    //位置不在中心
    private int popupGravity = Gravity.NO_GRAVITY;
    //弹窗子类项选中时的监听
    private OnMenuItemOnClickListener mItemOnClickListener;
    //定义列表对象
    private ListView mListView;
    //定义弹窗子类项列表
    private ArrayList<PopupMenuItem> mMenuItems = new ArrayList<PopupMenuItem>();
    private int textPaddingTop;
    private int textPaddingBottom;
    private int drawablePadding;

    public TitlePopupMenu(Context context) {
        //设置布局的参数
        this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public TitlePopupMenu(Context context, int width, int height) {
        this.mContext = context;
        //设置可以获得焦点
        setFocusable(true);
        //设置弹窗内可点击
        setTouchable(true);
        //设置弹窗外可点击
        setOutsideTouchable(true);
        //获得屏幕的宽度和高度
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        //设置弹窗的宽度和高度
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        //设置弹窗的布局界面
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.title_popup_menu, null));
        initUI();
        textPaddingTop = dip2px(mContext, 16);
        textPaddingBottom = dip2px(mContext, 16);
        drawablePadding = dip2px(mContext, 12);

    }

    /**
     * 初始化弹窗列表
     */
    private void initUI() {
        mListView = (ListView) getContentView().findViewById(R.id.title_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                //点击子类项后，弹窗消失
                dismiss();
                if (mItemOnClickListener != null)
                    mItemOnClickListener.onMenuItemItemClick(mMenuItems.get(index), index);
            }
        });
    }

    /**
     * 显示弹窗列表界面
     */
    public void show(View view) {
        //获得点击屏幕的位置坐标
        view.getLocationOnScreen(mLocation);
        //设置矩形的大小
        mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());
        //判断是否需要添加或更新列表子类项
        if (mIsDirty) {
            populateItems();
        }
        //显示弹窗的位置
        showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth() / 2), mRect.bottom);
    }

    /**
     * 设置弹窗列表子项
     */
    private void populateItems() {
        mIsDirty = false;


        //设置列表的适配器
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    textView = new TextView(mContext);
                    textView.setTextColor(Color.parseColor("#161616"));
                    textView.setTextSize(14);
                    textView.setGravity(Gravity.LEFT);
                    textView.setPadding(0, textPaddingTop, 0, textPaddingBottom);
                    textView.setSingleLine(true);
                } else {
                    textView = (TextView) convertView;
                }
                PopupMenuItem item = mMenuItems.get(position);
                //设置文本文字
                textView.setText(item.title);
                //设置文字与图标的间隔
                textView.setCompoundDrawablePadding(drawablePadding);
                //设置在文字的左边放一个图标
                Drawable mDrawable = mContext.getResources().getDrawable(item.drawableId);
                textView.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
                return textView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return mMenuItems.get(position);
            }

            @Override
            public int getCount() {
                return mMenuItems.size();
            }
        });
    }

    /**
     * 添加子类项
     */
    public void addItem(PopupMenuItem item) {
        if (item != null) {
            mMenuItems.add(item);
            mIsDirty = true;
        }
    }

    /**
     * 清除子类项
     */
    public void cleanItem() {
        if (mMenuItems.isEmpty()) {
            mMenuItems.clear();
            mIsDirty = true;
        }
    }

    /**
     * 根据位置得到子类项
     */
    public PopupMenuItem getItem(int position) {
        if (position < 0 || position > mMenuItems.size())
            return null;
        return mMenuItems.get(position);
    }

    /**
     * 设置监听事件
     */
    public void setListener(OnMenuItemOnClickListener onItemOnClickListener) {
        this.mItemOnClickListener = onItemOnClickListener;
    }

    /**
     * 弹窗子类项按钮监听事件
     */
    public interface OnMenuItemOnClickListener {
        void onMenuItemItemClick(PopupMenuItem item, int position);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
