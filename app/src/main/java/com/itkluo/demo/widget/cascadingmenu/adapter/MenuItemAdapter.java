package com.itkluo.demo.widget.cascadingmenu.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itkluo.demo.R;
import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.ArrayList;
import java.util.List;

/**
 * 级联菜单列表list适配器
 * Created by luobingyong on 2018/11/20.
 */
public class MenuItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<MenuData> mListData;
    private int selectedPos = -1;
    private String selectedText = "";
    private int normalDrawbleId;
    private Drawable selectedDrawble;
    private float textSize;
    private View.OnClickListener onClickListener;
    private OnItemClickListener mOnItemClickListener;

    public MenuItemAdapter(Context context, List<MenuData> listData, int selectedDrawableId, int normalDrawbleId) {
        mContext = context;
        mListData = listData == null ? new ArrayList<MenuData>() : listData;
        this.selectedDrawble = mContext.getResources().getDrawable(selectedDrawableId);
        this.normalDrawbleId = normalDrawbleId;
        init();
    }

    private void init() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPos = (Integer) view.getTag();
                setSelectedPosition(selectedPos);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, selectedPos);
                }
            }
        };
    }

    /**
     * 设置选中的position,并通知刷新其它列表
     */
    private void setSelectedPosition(int pos) {
        if (mListData != null && pos < mListData.size()) {
            selectedPos = pos;
            selectedText = mListData.get(pos).getMenu_name();
            notifyDataSetChanged();
        }
    }

    /**
     * 设置选中的position,但不通知刷新
     */
    public void setSelectedPositionNoNotify(int pos, List<MenuData> listData) {
        selectedPos = pos;
        mListData = listData == null ? new ArrayList<MenuData>() : listData;
        if (pos >= 0 && pos < mListData.size()) {
            selectedText = mListData.get(pos).getMenu_name();
        }
    }

    /**
     * 获取选中的position
     */
    public int getSelectedPosition() {
        if (mListData != null && selectedPos < mListData.size()) {
            return selectedPos;
        }
        return -1;
    }

    /**
     * 设置列表字体大小
     */
    public void setTextSize(float tSize) {
        textSize = tSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.choose_item, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setTag(position);
        String mString = "";
        if (mListData != null) {
            if (position < mListData.size()) {
                mString = mListData.get(position).getMenu_name();
            }
        }
        view.setText(mString);
        if (textSize != 0) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }

        if (selectedText != null && selectedText.equals(mString)) {
            view.setBackground(selectedDrawble);//设置选中的背景图
        } else {
            view.setBackground(mContext.getResources().getDrawable(normalDrawbleId));//设置未选中中状态背景图
        }
//        view.setPadding(20, 0, 0, 0);
        view.setOnClickListener(onClickListener);
        return view;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    /**
     * 重新定义菜单选项单击接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
