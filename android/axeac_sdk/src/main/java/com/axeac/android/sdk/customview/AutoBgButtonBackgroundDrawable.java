package com.axeac.android.sdk.customview;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * describe:The stateful LayerDrawable used by this button.
 * <br>自定义按钮使用的LayerDrawable
 * @author axeac
 * @version 1.0.0
 */
class AutoBgButtonBackgroundDrawable extends LayerDrawable {

    // The color filter to apply when the button is pressed
    /**
     * 按钮按下时使用的颜色
     * */
    protected ColorFilter _pressedFilter = new LightingColorFilter(Color.LTGRAY, 1);
    // Alpha value when the button is disabled
    /**
     * 按钮禁用时的透明度值
     * <br>默认值为100
     * */
    protected int _disabledAlpha = 100;
    // Alpha value when the button is enabled
    /**
     * 按钮可以使用时的透明度值
     * <br>默认值为255
     * */
    protected int _fullAlpha = 255;

    public AutoBgButtonBackgroundDrawable(Drawable d) {
        super(new Drawable[]{d});
    }

    @Override
    protected boolean onStateChange(int[] states) {
        boolean enabled = false;
        boolean pressed = false;

        for (int state : states) {
            if (state == android.R.attr.state_enabled)
                enabled = true;
            else if (state == android.R.attr.state_pressed)
                pressed = true;
        }

        mutate();
        if (enabled && pressed) {
            setColorFilter(_pressedFilter);
        } else if (!enabled) {
            setColorFilter(null);
            setAlpha(_disabledAlpha);
        } else {
            setColorFilter(null);
            setAlpha(_fullAlpha);
        }

        invalidateSelf();

        return super.onStateChange(states);
    }

    @Override
    public boolean isStateful() {
        return true;
    }
}
