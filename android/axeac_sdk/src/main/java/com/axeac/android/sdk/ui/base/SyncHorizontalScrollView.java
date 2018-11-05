package com.axeac.android.sdk.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class SyncHorizontalScrollView extends HorizontalScrollView {

    /**
     * View对象
     * */
	private View mView;  
    public SyncHorizontalScrollView(Context context) {  
        super(context);  
    }  
    public SyncHorizontalScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }

    /**
     * 当发生滚动变化时，调用此方法，将视图滚动到当前位置
     *@param l 滚动后横坐标
     * @param t 滚动后纵坐标
     * @param oldl
     * 滚动前横坐标
     * @param oldt
     * 滚动前纵坐标
     * */
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {    
        super.onScrollChanged(l, t, oldl, oldt);    
        if(mView!=null){  
            mView.scrollTo(l, t);  
        }    
    }

    /**
     * 对view初始化赋值
     * @param view
     * View对象
     * */
    public void setScrollView(View view){  
        mView = view;    
    }  
}
