package com.axeac.android.sdk.customview;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.axeac.android.sdk.utils.DensityUtil;

/**
 * describe:custom Toolbar
 * <br>自定义Toolbar
 * @author axeac
 * @version 1.0.0
 * */
public class MToolbar extends Toolbar {


    public Toolbar.LayoutParams layoutParamsMid = null;
    public Toolbar.LayoutParams layoutParamsRight = null;

    private Context mContext;
    private int paddingValue;

    public MToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public MToolbar(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }


    private void init() {
        paddingValue = DensityUtil.dip2px(mContext, 10);
        layoutParamsMid = new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsMid.gravity = Gravity.CENTER;
        layoutParamsRight = new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParamsRight.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
    }

    public void addMiddleCustomView(View view) {
        addView(view, layoutParamsMid);
    }

    public void setMiddleTitle(int res, int color) {
        TextView tv = new TextView(mContext);
        tv.setText(res);
        tv.setTextColor(getResources().getColor(color));
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        addView(tv, layoutParamsMid);
    }

    public View addRightView(int resId) {
        ImageView btn = new ImageView(mContext);
        btn.setImageResource(resId);
        layoutParamsRight.rightMargin = paddingValue;
        addView(btn, layoutParamsRight);
        return btn;
    }


}
