package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.StaticObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * describe:To-do list
 * 待办列表
 * @author axeac
 * @version 1.0.0
 */
public class BackLog extends Component {

    /**
     * 待办标题
     * */
    private String title = "";
    /**
     * 待办信息1
     * */
    private String txt1 = "";
    /**
     * 待办信息2
     * */
    private String txt2 = "";
    /**
     * 待办信息3
     * */
    private String txt3 = "";
    /**
     * 待办数量（文本字符串，都来自于服务器）
     * */
    private String count = "";
    /**
     * 点击待办框后跳转的PAGE页面ID
     * */
    private String click = "";
    /**
     * 待办框背景色
     * */
    private String color = "";
    /**
     * 待办框图标
     * */
    private String icon = "";

    private String gravity = "center_vertical";

    /**
     * 设置待办框背景色
     * @param color
     * 颜色值
     * */
    public void setColor(String color) {
        this.color = color;
    }

    private View txt;

    /**
     * 设置待办标题
     * @param title
     * 标题文本
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置待办信息1
     * @param txt1
     * 待办信息1文本
     * */
    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    /**
     * 设置待办信息2
     * @param txt2
     * 待办信息2文本
     * */
    public void setTxt2(String txt2) {
        this.txt2 = txt2;
    }

    /**
     * 设置待办信息3
     * @param txt3
     * 待办信息3文本
     * */
    public void setTxt3(String txt3) {
        this.txt3 = txt3;
    }

    /**
     * 设置待办图标
     * @param icon
     * 图标资源名称
     * */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setGravity(String gravity){
        this.gravity = gravity;
    }

    /**
     * 设置待办数量
     * @param count
     * 待办数量
     * */
    public void setCount(String count) {
        this.count = count;
    }

    public BackLog(Activity ctx) {
        super(ctx);
        this.returnable = false;
        txt = LayoutInflater.from(ctx).inflate(R.layout.axeac_backlog_new, null);
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
//        ((TextView) txt.findViewById(R.id.title)).setText(title);
//        ((TextView) txt.findViewById(R.id.txt1)).setText(txt1);
//        ((TextView) txt.findViewById(R.id.txt2)).setText(txt2);
//        ((TextView) txt.findViewById(R.id.txt3)).setText(txt3);
//        ((TextView) txt.findViewById(R.id.txt4)).setText(ctx.getString(R.string.axeac_backlog_count, count));
//        if (color != null && !"".equals(color)) {
//            int r = Integer.parseInt(color.substring(0, 3));
//            int g = Integer.parseInt(color.substring(3, 6));
//            int b = Integer.parseInt(color.substring(6, 9));
//            ((GradientDrawable) txt.findViewById(R.id.txt4).getBackground()).setColor(Color.rgb(r, g, b));
//            ((GradientDrawable) txt.findViewById(R.id.title_layout).getBackground()).setColor(Color.rgb(r, g, b));
//        }
//        txt.findViewById(R.id.layout_1).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                doClick();
//            }
//        });

        ((TextView) txt.findViewById(R.id.title)).setText(title);
        ImageView imageView = ((ImageView) txt.findViewById(R.id.imageIcon));
        if (icon!=null&&!"".equals(icon)){
            Glide.with(ctx)
                    .load(StaticObject.getImageUrl("res-img:" + icon))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        ((TextView) txt.findViewById(R.id.txt4)).setText(count);
        int gravity_ = Gravity.CENTER_VERTICAL;
        if(gravity.toLowerCase().equals("bottom")){
            gravity_ = Gravity.BOTTOM;
        }
        ((TextView) txt.findViewById(R.id.txt4)).setGravity(Gravity.RIGHT|gravity_);
        if (color != null && !"".equals(color)) {
            int r = Integer.parseInt(color.substring(0, 3));
            int g = Integer.parseInt(color.substring(3, 6));
            int b = Integer.parseInt(color.substring(6, 9));
//            ((GradientDrawable) txt.findViewById(R.id.txt4).getBackground()).setColor(Color.rgb(r, g, b));
            ((GradientDrawable) txt.findViewById(R.id.rel).getBackground()).setColor(Color.rgb(r, g, b));
        }

        if(height!=-1){
            RelativeLayout layout =  txt.findViewById(R.id.rel);
            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.height=height;
            txt.findViewById(R.id.rel).setLayoutParams(params);
        }

        txt.findViewById(R.id.rel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                doClick();
            }
        });
    }

    /**
     * 设置点击待办框后跳转的PAGE页面ID
     * @param click
     * 点击待办框后跳转的PAGE页面ID
     * */
    public void setClick(String click) {
        this.click = click;
    }

    /**
     * 点击待办框后执行的方法
     * */
    public void doClick() {
        if (click.equals("")) {
            return;
        }
        String vs[] = StringUtil.split(click, ":");
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
            } else if (vs[0].equals("MAIN")) {
                Intent intent = new Intent();
                intent.setAction(StaticObject.Fresh_CLICK_ACTION);
                intent.putExtra("meip", "MEIP_PAGE=" + vs[1] + "\r\n" + (vs.length >= 3 ? vs[2] : ""));
                LocalBroadcastManager
                        .getInstance(ctx).sendBroadcast(intent);
            }
        }
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public View getView() {
        return txt;
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