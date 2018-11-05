package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.GlideImageLoader;
import com.axeac.android.sdk.utils.StaticObject;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2018/10/31.
 */

public class Image extends Component {

    private Context ctx;

    private Banner banner;

    private LinearLayout layout;

    private boolean isCycle = true;

    private int delayTime = 3000;

    private List<String> imageList = new ArrayList<>();

    private List<String> titleList = new ArrayList<>();

    private String indicator = "point";

    int flag = 0;

    private HashMap<String,String> clicks = new HashMap<>();

    private String size = "";

    public Image(Activity ctx){
        super(ctx);
        this.ctx = ctx;
        layout = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_banner,null);
        banner = layout.findViewById(R.id.banner_banner);
    }



    private void init(){
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
               doClick(String.valueOf(position));
            }
        });
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenheight = dm.heightPixels;
        int percent = 30;
        if (!"".equals(size)&&null!=size){
            if (size.endsWith("%")){
                percent = Integer.parseInt(size.substring(0,size.length()-1));
            }
            height = (int) (screenheight * percent/100);
            banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
        }else{
            if (height != -1){
                banner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
            }else{
                height = (int) (screenheight * percent/100);
            }
        }

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(imageList);
        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage);
        if (titleList.size()>0&&titleList.size()==imageList.size()) {
            //设置标题集合（当banner样式有显示title时）
            banner.setBannerTitles(titleList);
            if (indicator.toLowerCase().equals("num")){
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
            }else{
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
            }
        }else{
            if (indicator.toLowerCase().equals("num")){
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
            }else {
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            }
        }
        //设置自动轮播，默认为true
        banner.isAutoPlay(isCycle);
        //设置轮播时间
        banner.setDelayTime(delayTime);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);

        banner.start();
    }

    public void doClick(String key) {
        if (clicks.size()<=0) {
            return;
        }
        if (clicks.containsKey(key)) {
            String vs[] = StringUtil.split(clicks.get(key), ":");
            if (vs.length >= 2) {
                if (vs[0].equals("PAGE")) {
                    Intent intent = new Intent();
                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                    intent.putExtra("meip", "MEIP_PAGE=" + vs[1] + "\r\n" + (vs.length >= 3 ? vs[2] : ""));
                    LocalBroadcastManager
                            .getInstance(ctx).sendBroadcast(intent);
                } else if (vs[0].equals("OP")) {
                    Intent intent = new Intent();
                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                    intent.putExtra("meip", "MEIP_ACTION=" + vs[1] + "\r\n" + (vs.length >= 3 ? vs[2] : ""));
                    LocalBroadcastManager
                            .getInstance(ctx).sendBroadcast(intent);
                }
            }
        }
    }

    public void setCycle(String isCycle){
        this.isCycle = Boolean.parseBoolean(isCycle);
    }

    public void setDelayTime(String delayTime){
        this.delayTime = Integer.parseInt(delayTime) * 1000;
    }

    public void setSize(String size){
        this.size = size;
    }

    public void setIndicator(String indicator){
        this.indicator = indicator;
    }

    public void addData(String data){
        if(data.startsWith("http")){
            imageList.add(data);
        }else{
            String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
            if (url!=null&&!"".equals(url)) {
                imageList.add(url.substring(0, url.lastIndexOf("/")) + "/ResourceServer?id="+data);
            }
        }
    }

    public void addTitle(String title){
        titleList.add(title);
    }

    public void addClick(String click){
        clicks.put(String.valueOf(flag),click);
        flag++;
    }

    @Override
    public void execute() {
        init();
    }

    @Override
    public View getView() {
        return layout;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void starting() {

    }

    @Override
    public void end() {

    }
}
