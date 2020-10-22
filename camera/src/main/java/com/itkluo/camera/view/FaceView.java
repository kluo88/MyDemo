package com.itkluo.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * 绘制人脸矩形区域
 *
 * @author luobingyong
 * @date 2020/10/19
 */
public class FaceView extends View {
    public Paint mPaint;
    private String mCorlor = "#42ed45";
    private ArrayList<RectF> mFaces;

    public FaceView(Context context) {
        super(context);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(mCorlor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().getResources().getDisplayMetrics()));
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces != null) {
            for (RectF face : mFaces) {
                canvas.drawRect(face, mPaint);
            }
        }
    }

    public void setFaces(ArrayList<RectF> faces) {
        this.mFaces = faces;
        invalidate();
    }

}
