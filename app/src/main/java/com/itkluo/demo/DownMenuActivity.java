package com.itkluo.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 点击箭头显示下拉菜单
 * Created by luobingyong on 2018/11/26.
 */
public class DownMenuActivity extends AppCompatActivity {
    private ImageView arrowImageView;
    private TextView chooseText;
    private LinearLayout linearLayout;
    private StateAdapter adapter;
    private ListPopupWindow listPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_menu);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        arrowImageView = (ImageView) findViewById(R.id.arrow);
        chooseText = (TextView) findViewById(R.id.chooseText);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListPopupWindow(linearLayout);
            }
        });
    }

    private class StateAdapter extends ArrayAdapter {
        private String[] strs = {"最新", "推荐", "全部", "最热"};
        private LayoutInflater inflater;
        private int res;

        public StateAdapter(Context context, int resource) {
            super(context, resource);
            inflater = LayoutInflater.from(context);
            res = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(res, null);
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(getItem(position));
            text.setTextColor(Color.WHITE);
            text.setTextSize(10);
            convertView.setBackgroundColor(Color.RED);
            return convertView;
        }

        @Override
        public String getItem(int position) {
            return strs[position];
        }

        @Override
        public int getCount() {
            return strs.length;
        }
    }

    public void showListPopupWindow(View view) {
        if (listPopupWindow == null)
            listPopupWindow = new ListPopupWindow(this);

        if (adapter == null) {
            adapter = new StateAdapter(this, android.R.layout.simple_list_item_1);
            listPopupWindow.setAdapter(adapter);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    Toast.makeText(getApplicationContext(), adapter.getItem(pos), Toast.LENGTH_SHORT).show();
                    chooseText.setText(adapter.getItem(pos));
                    listPopupWindow.dismiss();
                }
            });

            listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //旋转0度是复位ImageView
                    arrowImageView.animate().setDuration(500).rotation(0).start();
                }
            });
        }
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setVerticalOffset(dip2px(this, 5));
        // 对话框的宽高
        listPopupWindow.setWidth(view.getWidth());
        listPopupWindow.setModal(true);

        listPopupWindow.show();
        arrowImageView.animate().setDuration(500).rotation(180).start();
    }


    public static int dip2px(Context context, float dipValue) {
        float sDensity = context.getResources().getDisplayMetrics().density;
        final float scale = sDensity;
        return (int) (dipValue * scale + 0.5f);
    }

}
