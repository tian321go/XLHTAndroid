package com.axeac.android.sdk.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * describe:Custom WebView and override the onTouchEvent method to solve the ScrollView nested WebView sliding conflict problem
 * <br>自定义WebView，重写onTouchEvent方法，解决ScrollView嵌套WebView的滑动冲突问题
 * @author axeac
 * @version 1.0.0
 */

public class ScrollWebView extends WebView {
    public ScrollWebView(Context context) {
        super(context);
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public HitTestResult getHitTestResult() {
        return super.getHitTestResult();
    }

    public interface ITouch {
        void onTouchPointerMult();
    }

    private ITouch touch;
    public void setITouch(ITouch touch) {
        this.touch = touch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //  Multi touch
        //多点触控
        if (event.getPointerCount() >= 2) {
            if (touch != null) {
                touch.onTouchPointerMult();
            }
        } else {
            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE:

                        if (touch != null) {
                            touch.onTouchPointerMult();
                        }
                    break;
            }

        }
        return super.onTouchEvent(event);
    }

    //intercept touch events?
    /**
     * 是否拦截触摸事件
     * @param ev
     * MotionEvent对象
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}