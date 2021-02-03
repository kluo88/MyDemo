package com.itkluo.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itkluo.demo.R;
import com.itkluo.demo.model.MenuModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 顶部分页列表标签
 * Created by luobingyong on 2018/11/16.
 */
public class ViewpagerGridMenu extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final int RMP = RelativeLayout.LayoutParams.MATCH_PARENT;
    private static final int RWC = RelativeLayout.LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final int L_WRAP = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final int L_MATCH = LayoutParams.MATCH_PARENT;
    private ViewPager mViewPager;
    private List<View> mViews;//存储每页显示的View
    private List<List<MenuModel>> pageModels;//分页的数据
    private LinearLayout mPointRealContainerLl;
    private int mPointGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    private int mPointLeftRightMargin;
    private int mPointTopBottomMargin;
    private int mPointContainerLeftRightPadding;
    private int mPointDrawableResId = R.drawable.bga_banner_selector_point_solid;
    private int mColumnsOnePage = 4;//每页显示的列数
    private int mRowsOnePage = 2;//每页显示的行数
    private int mCountOnePage;//每页显示的总数 mColumnsOnePage * mRowsOnePage
    private int mTotalPage;//总页数
    private Drawable mPointContainerBackgroundDrawable;
    private List<MenuModel> mModels;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mContentBottomMargin;
    private boolean mIsNeedShowIndicatorOnOnlyOnePage;
    private boolean mIsFirstInvisible = true;


    public ViewpagerGridMenu(Context context) {
        this(context, null);
    }

    public ViewpagerGridMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewpagerGridMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);
        initView(context);
    }

    private void initDefaultAttrs(Context context) {
        mPointLeftRightMargin = dp2px(context, 3);
        mPointTopBottomMargin = dp2px(context, 6);
        mPointContainerLeftRightPadding = dp2px(context, 10);
        mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#44aaaaaa"));

        mContentBottomMargin = 0;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGABanner);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGABanner_banner_pointDrawable) {
            mPointDrawableResId = typedArray.getResourceId(attr, R.drawable.bga_banner_selector_point_solid);
        } else if (attr == R.styleable.BGABanner_banner_pointContainerBackground) {
            mPointContainerBackgroundDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.BGABanner_banner_pointLeftRightMargin) {
            mPointLeftRightMargin = typedArray.getDimensionPixelSize(attr, mPointLeftRightMargin);
        } else if (attr == R.styleable.BGABanner_banner_pointContainerLeftRightPadding) {
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(attr, mPointContainerLeftRightPadding);
        } else if (attr == R.styleable.BGABanner_banner_pointTopBottomMargin) {
            mPointTopBottomMargin = typedArray.getDimensionPixelSize(attr, mPointTopBottomMargin);
        } else if (attr == R.styleable.BGABanner_banner_indicatorGravity) {
            mPointGravity = typedArray.getInt(attr, mPointGravity);
        } else if (attr == R.styleable.BGABanner_banner_contentBottomMargin) {
            mContentBottomMargin = typedArray.getDimensionPixelSize(attr, mContentBottomMargin);
        } else if (attr == R.styleable.BGABanner_banner_isNeedShowIndicatorOnOnlyOnePage) {
            mIsNeedShowIndicatorOnOnlyOnePage = typedArray.getBoolean(attr, mIsNeedShowIndicatorOnOnlyOnePage);
        }
    }

    private void initView(Context context) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
        setOrientation(VERTICAL);


        View rootView = View.inflate(getContext(), R.layout.viewpager_grid_menu, null);
        mViewPager = rootView.findViewById(R.id.viewPager);
        mPointRealContainerLl = rootView.findViewById(R.id.ll_point_container);


        if (Build.VERSION.SDK_INT >= 16) {
            mPointRealContainerLl.setBackground(mPointContainerBackgroundDrawable);
        } else {
            mPointRealContainerLl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
        }
        mPointRealContainerLl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomMargin, mPointContainerLeftRightPadding, mPointTopBottomMargin);
        addView(rootView);
    }

    /**
     * 设置每一页的控件、数据模型
     * 每一页是一个GridView
     *
     * @param models 每一页的数据模型集合
     */
    public void setData(List<MenuModel> models) {
        mViews = new ArrayList<>();
        if (models == null) {
            models = new ArrayList<>();
        }
        int itemCount = models.size();
        mCountOnePage = mColumnsOnePage * mRowsOnePage;
        mTotalPage = itemCount / mCountOnePage;
        mTotalPage = itemCount % mCountOnePage > 0 ? mTotalPage + 1 : mTotalPage;

        //拆分到每一页数据
        pageModels = new ArrayList<>();

        for (int i = 0; i < mTotalPage; i++) {
            List<MenuModel> subList;
            if (mTotalPage == 1) {
                subList = models;
            } else if (i == mTotalPage - 1) {
                subList = models.subList(i * mCountOnePage, itemCount);
            } else {
                subList = models.subList(i * mCountOnePage, i * mCountOnePage + mCountOnePage);
            }
            pageModels.add(subList);

            View pageItemView = View.inflate(getContext(), R.layout.viewpager_item_gridview, null);
            GridView gridView = pageItemView.findViewById(R.id.gv);
            gridView.setNumColumns(mColumnsOnePage);
            MygridviewAdapter mygridviewAdapter = new MygridviewAdapter(getContext(), subList);
            gridView.setAdapter(mygridviewAdapter);
            mViews.add(pageItemView);
        }

        initViewPager();
        initIndicator();
    }


    private void initIndicator() {
        if (mPointRealContainerLl == null) {
            LinearLayout.LayoutParams indicatorLp = new LinearLayout.LayoutParams(L_MATCH, LWC);
            mPointRealContainerLl = new LinearLayout(getContext());
            mPointRealContainerLl.setId(R.id.viewpager_grid_Menu_indicatorId);
            mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
            mPointRealContainerLl.setGravity(Gravity.CENTER_VERTICAL);
            addView(mPointRealContainerLl, indicatorLp);
        } else {
            mPointRealContainerLl.removeAllViews();
        }
        // 是否显示指示点
        if (mIsNeedShowIndicatorOnOnlyOnePage || mViews.size() > 1) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
            lp.setMargins(mPointLeftRightMargin, 0, mPointLeftRightMargin, 0);
            ImageView imageView;
            for (int i = 0; i < mViews.size(); i++) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setImageResource(mPointDrawableResId);
                mPointRealContainerLl.addView(imageView);
            }
        }
    }

    private void initViewPager() {
        if (mViewPager != null && this.equals(mViewPager.getParent())) {
            this.removeView(mViewPager);
            mViewPager = null;
        }

        mViewPager = new ViewPager(getContext());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new PageAdapter());
        mViewPager.addOnPageChangeListener(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(L_MATCH, L_MATCH);
        layoutParams.setMargins(0, 0, 0, mContentBottomMargin);
        addView(mViewPager, 0, layoutParams);

        switchToPoint(0);
    }

    private void switchToPoint(int newCurrentPoint) {

        if (mPointRealContainerLl != null) {
            if (mViews != null && mViews.size() > 0 && newCurrentPoint < mViews.size() && (mIsNeedShowIndicatorOnOnlyOnePage || mViews.size() > 1)) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
                for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
                    mPointRealContainerLl.getChildAt(i).setEnabled(i == newCurrentPoint);
                    // 处理指示器选中和未选中状态图片尺寸不相等
                    mPointRealContainerLl.getChildAt(i).requestLayout();
                }
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        position = position % mViews.size();
        switchToPoint(position);

        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position % mViews.size(), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * 添加ViewPager滚动监听器
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 获取当前选中界面索引
     */
    public int getCurrentItem() {
        if (mViewPager == null || isCollectionEmpty(mViews)) {
            return -1;
        } else {
            return mViewPager.getCurrentItem() % mViews.size();
        }
    }

    /**
     * 设置当只有一页数据时是否显示指示器
     */
    public void setIsNeedShowIndicatorOnOnlyOnePage(boolean isNeedShowIndicatorOnOnlyOnePage) {
        mIsNeedShowIndicatorOnOnlyOnePage = isNeedShowIndicatorOnOnlyOnePage;
    }

    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViews == null ? 0 : mViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (isCollectionEmpty(mViews)) {
                return null;
            }

            final int finalPosition = position % mViews.size();

            View view = mViews.get(finalPosition);

            ViewParent viewParent = view.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }


    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static boolean isCollectionEmpty(Collection collection, Collection... args) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        for (Collection arg : args) {
            if (arg == null || arg.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public class MygridviewAdapter extends BaseAdapter {

        public List<MenuModel> data;
        private LayoutInflater _inflater;
        private Context context;

        public MygridviewAdapter(Context context, List<MenuModel> data) {
            this.data = data;
            this.context = context;
            _inflater = LayoutInflater.from(context);
        }

        public void updateList(List<MenuModel> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = _inflater.inflate(R.layout.grildview_adapter_item, null);
                holder = new ViewHolder();
                holder.tv_menu = (TextView) convertView.findViewById(R.id.tv_menu);
                holder.iv_menu = (ImageView) convertView.findViewById(R.id.iv_menu);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertGridView(context, holder, data.get(position));
//            holder.tv_menu.setText(data.get(position).getName());
//            Glide.with(context)
//                    .load(data.get(position).getImgUrl())
//                    .placeholder(R.drawable.not_show_bg).error(R.drawable.not_show_bg).dontAnimate().fitCenter()
//                    .into(holder.iv_menu);
////            holder.iv_menu.setImageResource(data.get(position).getId());
            return convertView;
        }

        private class ViewHolder {
            private TextView tv_menu;// 菜单文字
            private ImageView iv_menu;//菜单图标
        }
    }

    private void convertGridView(Context context, MygridviewAdapter.ViewHolder holder, MenuModel model) {
        holder.tv_menu.setText(model.getName());
        Glide.with(context)
                .load(model.getImgUrl())
                .placeholder(R.drawable.not_show_bg).error(R.drawable.not_show_bg).dontAnimate().fitCenter()
                .into(holder.iv_menu);
//            holder.iv_menu.setImageResource(data.get(position).getId());
    }




}
