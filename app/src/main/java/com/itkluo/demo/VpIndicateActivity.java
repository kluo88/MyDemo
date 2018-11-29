package com.itkluo.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by luobingyong on 2018/11/16.
 */
public class VpIndicateActivity extends AppCompatActivity implements BGABanner.Delegate<ImageView, String>, BGABanner.Adapter<ImageView, String> {
    private BGABanner banner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vp_indicator);

        banner = (BGABanner) findViewById(R.id.banner);
        banner.setDelegate(this);
        updateUI();
    }

    private String[] images = new String[]{
            "http://res.qhhznt.com/rmadmin75/jpg/1540435312_3083.jpg"
//            , "http://res.qhhznt.com/rmadmin75/png/1540435268_4949.png"
//            , "http://res.qhhznt.com/rmadmin75/png/1540353097_6681.png"
//            , "http://res.qhhznt.com/rmadmin75/png/1540287662_1406.png"
//            , "http://res.qhhznt.com/rmadmin75/png/1540352831_4606.png"
//            , "http://res.qhhznt.com/rmadmin75/png/1540287677_795.png"
//            , "http://res.qhhznt.com/rmadmin75/jpg/1540263689_9531.jpg"
//            , "http://res.qhhznt.com/rmadmin75/jpg/1540263771_5758.jpg"
    };

    private void updateUI() {
        List<String> list = Arrays.asList(images);
        banner.setAutoPlayAble(list.size() > 1);
        banner.setAdapter(this);
        banner.setData(list, null);

    }

    @Override
    public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable String model, int position) {
        itemView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(itemView.getContext())
                .load(model)
                .placeholder(R.drawable.not_show_bg).error(R.drawable.not_show_bg).dontAnimate().fitCenter()
                .into(itemView);
    }

    @Override
    public void onBannerItemClick(BGABanner banner, ImageView itemView, @Nullable String model, int position) {
    }

}
