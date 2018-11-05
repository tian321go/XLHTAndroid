package com.axeac.android.sdk.ui.container;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.axeac.android.sdk.ui.base.Component;

/**
 * describe:Horizontal container
 * 所有组件都在组件一行排列，每个组件的高度都不受限制，根据宽度计算自适应，整个容器的高度为最高的组件的高度，所有组件的宽度都重新设置。
 *<br>已设置宽度值的宽度不变，当宽度为百分比时，根据容器宽度计算出宽度。当没有设置组件宽度时，把所有未设置的组件宽度=容器宽度-已有组件宽度（包括百分比），将调用calWidth方法获取到宽度值，然后根据所有未设置的宽度之和按各自占用百分比及未设置组件宽度比例重新设置宽度。
 *<br>无论什么情况，最后一个控件的宽度为总宽度-其他宽度之和。当只有一个控件时，该控件宽度为容器宽度，高度自适应。
 *
 */
public class BoxLayoutX extends LinearLayout implements KHMAP5Layout {

    public BoxLayoutX(Context mContext) {
        super(mContext);
        this.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 添加view到容器中
     * @param view
     * view 准备添加的view
     * */
    @Override
    public void addViewIn(Component view) {
        if (view != null && view.getView() != null) {
            this.addView(view.getView(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
        }
    }

    /**
     * 返回当前容器
     * @return
     * 当前容器
     * */
    @Override
    public ViewGroup getLayout() {
        return this;
    }

    /**
     * 移除容器中所有view
     * */
    @Override
    public void removeAll() {
        this.removeAllViews();
    }

    /**
     * 移除容器中指定view
     * @param view
     * view 准备移除的view
     * */
    @Override
    public void removeAll(View view) {
        this.removeView(view);
    }
}