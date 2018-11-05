package com.axeac.android.sdk.customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;
/**
 * 自定义按钮
 * */
public class AutoButtonView extends AppCompatButton {

    public AutoButtonView(Context context) {
        super(context);
        setMinHeight(0);
    }

    public AutoButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMinHeight(0);
    }

    public AutoButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMinHeight(0);
    }

    @Override
    public void setBackgroundDrawable(Drawable d) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            AutoBgButtonBackgroundDrawable layer = new AutoBgButtonBackgroundDrawable(
                    d);
            super.setBackgroundDrawable(layer);
        } else {
            super.setBackgroundDrawable(d);
        }

    }
}
