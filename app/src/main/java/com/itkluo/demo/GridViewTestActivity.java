package com.itkluo.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.itkluo.demo.utils.UIUtils;

public class GridViewTestActivity extends AppCompatActivity {
    private static final String TAG = "GridViewTestActivity";
    int[] colors = new int[]
            {
                    R.color.c1, R.color.c2, R.color.c3,
                    R.color.c4, R.color.c5, R.color.c6,
                    R.color.c7, R.color.c8, R.color.c9
            };
    int[] texts = new int[]
            {
                    R.string.c1, R.string.c2, R.string.c3,
                    R.string.c4, R.string.c5, R.string.c6,
                    R.string.c7, R.string.c8, R.string.c9
            };
    Button button1;
    GridView gridview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view_test);
        button1 = (Button) findViewById(R.id.button1);
        gridview1 = (GridView) findViewById(R.id.gridview1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = gridview1.getColumnWidth();
                int widthSpace = gridview1.getHorizontalSpacing();
                Toast.makeText(GridViewTestActivity.this, "columnWidth:" + width + ",widthSpace:" + widthSpace, Toast.LENGTH_LONG).show();

                int widthDp = UIUtils.px2dip(width);
                int widthSpaceDp = UIUtils.px2dip(widthSpace);

                Log.d(TAG, "columnWidthDp:" + widthDp + ",widthSpaceDp:" + widthSpaceDp);
            }

        });

        final BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return texts.length;
            }

            @Override
            public Object getItem(int arg0) {
                return getResources().getString(texts[arg0]);
            }

            @Override
            public long getItemId(int arg0) {
                return arg0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {

                TextView textView = new TextView(GridViewTestActivity.this);
                textView.setText(getItem(position).toString());
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                textView.setBackgroundResource(colors[position]);
                textView.setWidth(60);
                textView.setHeight(60);
                return textView;

            }

        };

        gridview1.setAdapter(baseAdapter);


    }
}