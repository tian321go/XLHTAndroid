package com.axeac.android.sdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ComponentActivity;
import com.axeac.android.sdk.analysis.ComponentAnalysis;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.ui.Form;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Fragment for the main interface
 * 主界面的Fragment
 * @author axeac
 * @version 1.0.0
 * */
public class MainFragment1 extends BaseFragment {

    private ScrollView mScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.axeac_fragment_main_1, null);
            mScrollView = (ScrollView) mView.findViewById(R.id.comp_layout);
            initView();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Property对象
     * */
    private Property navLists;

    /**
     * 初始化动作
     * */
    private void initView() {
        navLists = KhinfSDK.getInstance().getProperty();
        addView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticObject.MENU_CLICK_ACTION);
        filter.addAction(StaticObject.Fresh_CLICK_ACTION);
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver,
                filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(act).unregisterReceiver(receiver);
    }

    /**
     * BroadcastReceiver对象
     * */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(StaticObject.MENU_CLICK_ACTION)) {
                doLoading(intent.getStringExtra("meip"));
            }
            if (action.equals(StaticObject.Fresh_CLICK_ACTION)){
                UIHelper.send(act, intent.getStringExtra("meip"), new OnRequestCallBack() {
                    @Override
                    public void onStart() {
                        act.showProgressDialog();
                    }

                    @Override
                    public void onCompleted() {
                        act.removeProgressDialog();
                    }

                    @Override
                    public void onSuccesed(JHSPResponse response) {
                        if (response.getCode() == 0) {
                            addViewNoti(response.getData());
                        }
                    }

                    @Override
                    public void onfailed(Throwable e) {
                        act.removeProgressDialog();
                    }
                });
            }
        }
    };

    // describe:add view
    /**
     * 添加视图
     * */
    private void addView(){
        ComponentAnalysis analysis = new ComponentAnalysis(mView, getActivity(), navLists);
        Form form = analysis.analysis(navLists);
        if (form != null) {
            mScrollView.removeAllViews();
            mScrollView.addView(form.getView());
            mScrollView.setBackgroundColor(Color.rgb(242, 242, 242));

        }
    }

    // describe:add view
    /**
     * 添加视图
     * */
    private void addViewNoti(String params){
        navLists = new Property();
        navLists.setSplit("\n");
        navLists.load(params);
        ComponentAnalysis analysis = new ComponentAnalysis(mView, getActivity(), navLists);
        Form form = analysis.analysis(navLists);
        if (form != null) {
            mScrollView.removeAllViews();
            mScrollView.addView(form.getView());
            mScrollView.setBackgroundColor(Color.rgb(242, 242, 242));

        }
    }

    /**
     * 判断是否第一次进入onResume方法
     * */
    private boolean isFirst = true;

    // describe:When on resume, send request and update data
    /**
     * 恢复到焦点时，发送请求，更新数据
     * */
    @Override
    public void onResume() {
        super.onResume();
        if(isFirst){
            isFirst  = false;
            return;
        }
        UIHelper.send(act, "MEIP_LOGIN=MEIP_LOGIN", new OnRequestCallBack() {
            @Override
            public void onStart() {
                act.showProgressDialog();
            }

            @Override
            public void onCompleted() {
                act.removeProgressDialog();
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    navLists.clear();
                    navLists.setSplit("\n");
                    navLists.load(response.getData());
                    addViewNoti(response.getData());
                }
            }

            @Override
            public void onfailed(Throwable e) {
                act.removeProgressDialog();
            }
        });
    }

    // describe:send request and jump Activity
    /**
     * 发送请求并跳转界面
     * @param meip
     * 请求字段
     * */
    private void doLoading(final String meip) {

        UIHelper.send(act, meip, new OnRequestCallBack() {
            @Override
            public void onStart() {
                act.showProgressDialog();
            }

            @Override
            public void onCompleted() {
                act.removeProgressDialog();
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getReturnType().equals(StaticObject.MEIP_RETURN_FORM)) {
                    StaticObject.ismenuclick = false;
                    Intent intent = new Intent(act, ComponentActivity.class);
                    intent.putExtra("parms", response.getData());
                    startActivity(intent);
                }
            }

            @Override
            public void onfailed(Throwable e) {
                act.removeProgressDialog();
            }
        });
    }

}
