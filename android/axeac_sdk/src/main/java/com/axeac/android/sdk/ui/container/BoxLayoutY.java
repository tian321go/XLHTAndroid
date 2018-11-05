package com.axeac.android.sdk.ui.container;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.axeac.android.sdk.ui.base.Component;

/**
 * describe:Vertical container
 * 所有组件都在组件排成一列，每个组件的宽度都是容器宽度，根据宽度计算组件高度。
 */
public class BoxLayoutY extends LinearLayout implements KHMAP5Layout {

    public BoxLayoutY(Context mContext) {
        super(mContext);
        this.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        this.setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 添加view到容器中
     * @param view
     * view 准备添加的view
     * */
    @Override
    public void addViewIn(Component view) {
        if (view != null) {
            View child = view.getView();
            if (child != null) {
                this.addView(child);
            }
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