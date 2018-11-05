package com.axeac.android.sdk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;

import com.axeac.android.sdk.activity.BaseActivity;


public class BaseFragment extends Fragment {

    /**
     * describe:The global LayoutInflater object has already been initialized
     * 全局的LayoutInflater对象，已经完成初始化.
     */
    public LayoutInflater mInflater;
    /**
     * describe:screen width
     * 屏幕宽度.
     */
    public int displayWidth = 320;

    /**
     * describe:screen height
     * 屏幕高度.
     */
    public int displayHeight = 480;
    protected BaseActivity act;
    protected View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = (BaseActivity) getActivity();
        mInflater = LayoutInflater.from(act);
        Display display = act.getWindowManager().getDefaultDisplay();
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
    }

    /**
     * activity跳转
     * @param cls
     * 跳转对象
     * */
    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(act, cls);
        act.startActivity(intent);
    }

}
