package com.axeac.android.sdk.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

    /**
     * ScrollView监听对象，初始化为null
     * */
    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 对scrollViewListener赋值
     * @param scrollViewListener
     * ScrollView监听对象
     * */
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    /**
     * 当ScrollView滚动时调用的方法，为ScrollView设置监听
     * @param x 滚动后横坐标
     * @param y 滚动后纵坐标
     * @param oldx
     * 滚动前横坐标
     * @param oldy
     * 滚动前纵坐标
     * */
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
