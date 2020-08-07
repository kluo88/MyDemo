package com.itkluo.demo.exam.customview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现自己的ViewGroup，效果为：
 * 所有子view，一律以纵向往下排布，每行一个的规律放置。
 * 并且，每放置一个，下一个就往右边锁紧一定距离。
 */
public class MyViewGroup extends ViewGroup {

    private static int OFFSET = 10;// 注意，源码中直接出现的距离，都是以px为单位，只有在定义了单位为dp的距离的时候，才是dp

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        OFFSET = Utils.dip2px(10, context);//这里直接把它变成10dp
    }

    /**
     * 为了在121行 获取LayoutParam时 得到的是MarginLayoutParam，这里要重写此方法
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 注意，这两个参数，是自身的MeasureSpec int值
     * <p>
     * 如果考虑margin和padding的话，那具体指的就是 子view的margin，和自身的padding，不要弄错了。
     *
     * <p>
     * 自身的margin是自己的父去使用的，子view的padding是子View自己的。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //获得自身的padding,它最终影响自身的可绘制区域
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        //从MeasureSpec中分离出size和mode
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //3、把宽高限制信息传给子，让子完成自己的测量
        final int childCount = getChildCount();
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() != GONE) {//如果子可见，才执行测量
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
                final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

        //获取子测量之后的尺寸，然后根据子的尺寸决定 自己的尺寸
        int width = 0, height = 0;
        switch (widthMode) {//自身的测量mode
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                int xOffset = 0;
                for (int i = 0; i < childCount; i++) {
                    child = getChildAt(i);
                    //由于我这里加了一个缩进，所以要计算缩进之后的距离
                    //child的左右margin也要计算在内,一起参与viewGroup的测量
                    int widthAddOffsetAddPadding = i * OFFSET + child.getMeasuredWidth() + paddingLeft + paddingRight;
                    MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                    xOffset += lp.leftMargin + lp.rightMargin;
                    int widthAddOffsetAddPaddingAddChildMargin = widthAddOffsetAddPadding + xOffset;
                    width = Math.max(width, widthAddOffsetAddPaddingAddChildMargin);//这里考虑到缩进也有可能是负数，但是自身的宽度不能容不下子，所以要max
                }
                break;
            default:
                break;
        }

        switch (heightMode) {//自身的测量mode
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                for (int i = 0; i < childCount; i++) {
                    child = getChildAt(i);
                    height += child.getMeasuredHeight() + paddingTop + paddingBottom;
                }
                break;
            default:
                break;
        }

        //保存自身宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();

        int topOffset = 0;//top偏移量
        int leftOffset = 0;//left偏移量
        for (int i = 0; i < childCount; i++) {//布局，就是要确定子的左上右下
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int left = leftOffset + lp.leftMargin + paddingLeft;//为什么在这里获取的paddingLeft是空的?
            int right = left + child.getMeasuredWidth();
            int top = topOffset + paddingTop + lp.topMargin;
            int bottom = top + child.getMeasuredHeight();

            child.layout(left, top, right, bottom);

            topOffset += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            leftOffset += OFFSET + lp.leftMargin;//左偏移量也计算在内
        }

    }

    static class Utils {

        /**
         * 屏幕dip值转换为像素值
         *
         * @param dipValue 屏幕dip值
         * @return int 屏幕像素值
         */
        public static int dip2px(float dipValue, Context activity) {
            return (int) (dipValue * getScreenDensity(activity) + 0.5f);
        }

        private static float getScreenDensity(Context activity) {
            try {
                return activity.getResources().getDisplayMetrics().density;
            } catch (Exception e) {
                return 1;
            }
        }
    }
}