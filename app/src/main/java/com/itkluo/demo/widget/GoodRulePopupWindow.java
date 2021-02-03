package com.itkluo.demo.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itkluo.demo.R;
import com.itkluo.demo.model.GoodsDetailBean;
import com.itkluo.demo.widget.FlowLayout.GoodsRuleTagFlowLayout;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 添加商品时属性规格popupwindow
 * Created by luobingyong on 2019/1/21.
 */
public class GoodRulePopupWindow extends PopupWindow {

    private GoodsDetailBean goodsDetailBeans;
    private Context mContext;
    private View view;
    private LinearLayout rule_container;
    private LinearLayout content_container;
    private int screenHeigh;
    private NestedScrollView scrollView;
    private String tempSkuId;
    private ImageView iv_thumb;//商品展示图

    public GoodRulePopupWindow(Context context, GoodsDetailBean goodsDetailBeans) {
        this.mContext = context;
        this.goodsDetailBeans = goodsDetailBeans;
        initView(mContext);
        updateView();
    }

    private void initView(Context mContext) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_goods_rule, null);
        view.findViewById(R.id.tv_out_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(this.view);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.BottomPopupAnimation);
        //设置是否遮住状态栏
//        fitPopupWindowOverStatusBar(true);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        screenHeigh = wm.getDefaultDisplay().getHeight();

        iv_thumb = view.findViewById(R.id.iv_thumb);
        rule_container = view.findViewById(R.id.rule_container);
        content_container = view.findViewById(R.id.content_container);
        scrollView = view.findViewById(R.id.scrollView);
    }


    private void updateView() {
        addRule();
    }

    //添加规格
    private void addRule() {
        List<GoodsDetailBean.SkuInfo> list = goodsDetailBeans.getSku_info();
        for (int i = 0; i < list.size(); i++) {
            GoodsDetailBean.SkuInfo skuBean = list.get(i);
            View ruleTagLayout = getRuleTagLayout(skuBean.getSku_name(), skuBean.getSku_id(), goodsDetailBeans.getSku_info().get(i).getValue_list());
            rule_container.addView(ruleTagLayout);
        }
    }

    private View getRuleTagLayout(String tagTitle, String vid, List<GoodsDetailBean.ValueList> id) {
        View ruleTagLayout = LayoutInflater.from(mContext).inflate(R.layout.goods_rule_tag_layout, null);
        TextView tv_tag_title = ruleTagLayout.findViewById(R.id.tv_tag_title);
        tv_tag_title.setText(tagTitle);

        GoodsRuleTagFlowLayout flowLayout_tag = ruleTagLayout.findViewById(R.id.flowLayout_tag);
        for (int i = 0; i < id.size(); i++) {
            View rule_tag_item_layout = LayoutInflater.from(mContext).inflate(R.layout.goods_rule_tag_item_layout, null);
            TextView textView = rule_tag_item_layout.findViewById(R.id.tv_txt);
            textView.setText(id.get(i).getValue_name());
            textView.setTag(vid + ":" + id.get(i).getValue_id());
            textView.setTag(R.id.tag_first, goodsDetailBeans.getSku_list().get(i).getPrice());
            textView.setOnClickListener(clickListener);
            if (tempSkuId != null) {
                String[] strTemp = tempSkuId.split(";");
                for (String s : strTemp) {
                    if (s.equals(textView.getTag().toString())) {
                        textView.setSelected(true);
                    }
                }
            }
            int dis = dip2px(10);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(0, dis, dis, dis);
//            textView.setLayoutParams(marginLayoutParams);
            flowLayout_tag.addView(textView, marginLayoutParams);
        }

        return ruleTagLayout;
    }

    private String imgUrl;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (v.isSelected()) {
//                v.setSelected(false);
//                imgUrl = goodsDetailBeans.getMain_img();
//                if (!TextUtils.isEmpty(imgUrl)) {
////                        XImageLoader.load(Const.imgurl + goodsDetailBeans.getMain_img(),pic);
////                    XImageLoader.load(Const.IMAGE_HEAD + goodsDetailBeans.getMain_img(), pic);
//                    Log.d("XImageLoader", goodsDetailBeans.getMain_img() + "");
//                }
//                // txt_price.setText(Double.parseDouble(goodsDetailBeans.getSku_list().get(0).getPrice()) / 100 + "元/" + goodsDetailBeans.getGoods_unit_name());
//                //   txt_kc.setText(goodsDetailBeans.getSku_list().get(0).getQuantity()+ goodsDetailBeans.getGoods_unit_name());
//                //显示价格（未选中时显示最低价格和最高价格的区间）  当只有一个规格时显示一个价格
//                double MaxPrice = Double.parseDouble(goodsDetailBeans.getSku_list().get(0).getPrice());
//                double SmallPrice = Double.parseDouble(goodsDetailBeans.getSku_list().get(0).getPrice());
//                if (goodsDetailBeans.getSku_list().size() > 1) {
//                    for (int i = 0; i < goodsDetailBeans.getSku_list().size(); i++) {
//                        double SelectedPrice = Double.parseDouble(goodsDetailBeans.getSku_list().get(i).getPrice());
//                        if (MaxPrice < SelectedPrice) {
//                            MaxPrice = SelectedPrice;
//                        }
//                        if (SmallPrice > SelectedPrice) {
//                            SmallPrice = SelectedPrice;
//                        }
//                    }
//                    txt_price.setText(String.valueOf(SmallPrice / 100) + "-" + String.valueOf(MaxPrice / 100) + "元/" + goodsDetailBeans.getGoods_unit_name());
//                } else {
//                    txt_price.setText(String.valueOf(SmallPrice / 100) + "元/" + goodsDetailBeans.getGoods_unit_name());
//                }
//
//                //显示库存(未选中显示所有数量的和)
//                int shuliang = 0;
//                for (int i = 0; i < goodsDetailBeans.getSku_list().size(); i++) {
//                    int d = Integer.parseInt(goodsDetailBeans.getSku_list().get(i).getQuantity());
//                    shuliang += d;
//                }
//                kucun = shuliang;
//                txt_kc.setText(String.valueOf(kucun) + goodsDetailBeans.getGoods_unit_name());
//
//                return;
//            }


        }
    };

    //弹出的窗口是否覆盖状态栏
    public void fitPopupWindowOverStatusBar(boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                //利用反射重新设置mLayoutInScreen的值，当mLayoutInScreen为true时则PopupWindow覆盖全屏。
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(this, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setData(GoodsDetailBean goodsDetailBeans) {
        this.goodsDetailBeans = goodsDetailBeans;
        updateView();
    }


}
