package com.itkluo.demo.widget.cascadingmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.itkluo.demo.R;
import com.itkluo.demo.widget.cascadingmenu.adapter.MenuItemAdapter;
import com.itkluo.demo.widget.cascadingmenu.contract.CascadingMenuContract;
import com.itkluo.demo.widget.cascadingmenu.interfaces.CascadingMenuViewOnSelectListener;
import com.itkluo.demo.widget.cascadingmenu.model.MenuData;

import java.util.ArrayList;
import java.util.List;

/**
 * 三级级联动ListView 使用MVP模式分离业务逻辑
 * Created by luobingyong on 2018/11/20.
 */
public class CascadingMenuView extends LinearLayout implements CascadingMenuContract.View {
    private static final String TAG = CascadingMenuView.class.getSimpleName();
    // 三级菜单选择后触发的接口，即最终选择的内容
    private CascadingMenuViewOnSelectListener mOnSelectListener;
    private ListView firstMenuListView;
    private ListView secondMenuListView;
    private ListView thirdMenuListView;

    // 每次选择的子菜单内容
    private List<MenuData> thirdItem = new ArrayList<MenuData>();
    private List<MenuData> secondItem = new ArrayList<MenuData>();
    private List<MenuData> menuItem = new ArrayList<MenuData>();

    private MenuItemAdapter firstMenuListViewAdapter;
    private MenuItemAdapter secondMenuListViewAdapter;
    private MenuItemAdapter thirdMenuListViewAdapter;

    private int firstPosition = -1;
    private int secondPosition = -1;
    private int thirdPosition = -1;

    private CascadingMenuContract.Presenter presenter;
    private Context context;

    private int itemSelectedDrawableId = R.drawable.choose_item_selected;
    private int itemSelector = R.drawable.choose_item_selector;
    private int mHeight;//菜单最大高度
    private boolean isAutoLoadNext;//开始是否自动加载下一级

    public enum EMenuType {
        One, Two, Three
    }

    private EMenuType mType = EMenuType.One;

    @Override
    public void setFirstMenuList(List<MenuData> menuList, int selectPosition) {
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        if (firstMenuListViewAdapter != null) {
            // 选择主菜单，清空原本子菜单内容，增加新内容
            menuItem.clear();
            menuItem = menuList;
            firstMenuListViewAdapter.setSelectedPositionNoNotify(selectPosition, menuItem);
            firstMenuListViewAdapter.notifyDataSetChanged();
            //自动加载下一级
            if (isAutoLoadNext && secondMenuListViewAdapter != null && menuList.size() > 0) {
                getSecondItem(menuList.get(0));
            }
        }
    }

    @Override
    public void setSecondMenuList(List<MenuData> menuList) {
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        if (secondMenuListViewAdapter != null) {
            // 选择主菜单，清空原本子菜单内容，增加新内容
            secondItem.clear();
            secondItem = menuList;
            secondMenuListViewAdapter.setSelectedPositionNoNotify(secondPosition, secondItem);
            secondMenuListViewAdapter.notifyDataSetChanged();
            //自动加载下一级
            if (isAutoLoadNext && thirdMenuListViewAdapter != null && menuList.size() > 0) {
                getThirdItem(menuList.get(0));
            }
        }
    }

    @Override
    public void setThirdMenuList(List<MenuData> menuList) {
        if (menuList == null) {
            menuList = new ArrayList<>();
        }
        if (thirdMenuListViewAdapter != null) {
            thirdItem.clear();
            thirdItem = menuList;
            thirdMenuListViewAdapter.setSelectedPositionNoNotify(thirdPosition, thirdItem);
            thirdMenuListViewAdapter.notifyDataSetChanged();
        }
    }


    public CascadingMenuView(Context context) {
        this(context, null);
    }

    public CascadingMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CascadingMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
    }

    private void initDefaultAttrs() {
        itemSelectedDrawableId = R.drawable.choose_item_selected;
        itemSelector = R.drawable.choose_item_selector;
        isAutoLoadNext = false;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CascadingMenuView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.CascadingMenuView_itemSelectedDrawableId) {
            itemSelectedDrawableId = typedArray.getResourceId(attr, R.drawable.choose_item_selected);
        } else if (attr == R.styleable.CascadingMenuView_EMenuType) {
            int ordinal = typedArray.getInt(attr, EMenuType.One.ordinal());
            mType = EMenuType.values()[ordinal];
        }
    }

    private void initView() {
        if (mHeight > 0) {//通过代码设定了高度
            LinearLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            layoutParams.height = mHeight;
            setLayoutParams(layoutParams);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.view_casting, this, false);
        addView(view);
        firstMenuListView = findViewById(R.id.listView);
        secondMenuListView = findViewById(R.id.listView2);
        thirdMenuListView = findViewById(R.id.listView3);
        // 初始化一级主菜单
        initFirstList();
        // 初始化二级主菜单
        if (mType == EMenuType.Two || mType == EMenuType.Three) {
            secondMenuListView.setVisibility(VISIBLE);
            initSecondList();
        }
        // 初始化三级主菜单
        if (mType == EMenuType.Three) {
            thirdMenuListView.setVisibility(VISIBLE);
            initThirdList();
        }
        // 设置默认选择
        setDefaultSelect();
    }

    /**
     * 直接通过new对象要设置菜单列数
     *
     * @param type
     */
    public CascadingMenuView setMenuType(@NonNull EMenuType type) {
        this.mType = type;
        return this;
    }

    public void startLoad(CascadingMenuContract.Presenter presenter) {
        this.presenter = presenter;
        presenter.setView(this);
        initView();
        presenter.start();
    }

    private void initFirstList() {
        firstMenuListViewAdapter = new MenuItemAdapter(context, menuItem, itemSelectedDrawableId, itemSelector);
//        firstMenuListViewAdapter.setTextSize(17);
        firstMenuListViewAdapter.setSelectedPositionNoNotify(firstPosition, menuItem);
        firstMenuListView.setAdapter(firstMenuListViewAdapter);
        firstMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (thirdMenuListViewAdapter != null && thirdItem != null) {//清空第3级的数据
                    thirdItem.clear();
                    thirdMenuListViewAdapter.notifyDataSetChanged();
                }
                if (secondMenuListViewAdapter != null) {
                    getSecondItem(menuItem.get(position));
                } else {
                    MenuData bean = menuItem.get(position);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.getValue(bean);
                    }
                }

            }
        });
    }

    private void initSecondList() {
        secondMenuListViewAdapter = new MenuItemAdapter(context, secondItem, itemSelectedDrawableId, itemSelector);
//        secondMenuListViewAdapter.setTextSize(15);
        secondMenuListViewAdapter.setSelectedPositionNoNotify(secondPosition, secondItem);
        secondMenuListView.setAdapter(secondMenuListViewAdapter);
        secondMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, final int position) {
                if (thirdMenuListViewAdapter != null && secondItem.size() > 0) {
                    getThirdItem(secondItem.get(position));
                } else {
                    MenuData bean = secondItem.get(position);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.getValue(bean);
                    }
                }

            }
        });

    }

    private void initThirdList() {
        thirdMenuListViewAdapter = new MenuItemAdapter(context, thirdItem, itemSelectedDrawableId, itemSelector);
//        thirdMenuListViewAdapter.setTextSize(13);
        thirdMenuListViewAdapter.setSelectedPositionNoNotify(thirdPosition, thirdItem);
        thirdMenuListView.setAdapter(thirdMenuListViewAdapter);
        thirdMenuListViewAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, final int position) {
                MenuData menuItem = thirdItem.get(position);
                if (mOnSelectListener != null) {
                    mOnSelectListener.getValue(menuItem);
                }
            }
        });
    }

    private void getSecondItem(MenuData parentMenuData) {
        if (presenter != null) {
            presenter.requestSecondMenuList(parentMenuData);
        }
    }

    private void getThirdItem(MenuData parentMenuData) {
        if (presenter != null) {
            presenter.requestThirdMenuList(parentMenuData);
        }
    }

    private void setDefaultSelect() {
        firstMenuListView.setSelection(firstPosition);
        secondMenuListView.setSelection(secondPosition);
        thirdMenuListView.setSelection(thirdPosition);
    }

    public void setFirstPosition(int firstPosition) {
        this.firstPosition = firstPosition;
    }

    public CascadingMenuView setCascadingMenuViewOnSelectListener(CascadingMenuViewOnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
        return this;
    }

    public CascadingMenuView setItemSelectedDrawableId(int itemSelectedDrawableId) {
        this.itemSelectedDrawableId = itemSelectedDrawableId;
        return this;
    }

    public CascadingMenuView setItemSelector(int itemSelector) {
        this.itemSelector = itemSelector;
        return this;
    }

    public CascadingMenuView setHeightDp(int heightDp) {
        this.mHeight = dp2px(context, heightDp);
        return this;
    }

    private static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    private static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

}
