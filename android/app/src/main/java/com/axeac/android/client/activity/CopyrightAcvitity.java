package com.axeac.android.client.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.axeac.android.client.R;
import com.axeac.android.client.adapters.CopyrightListAdapter;
import com.axeac.android.sdk.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2018/11/5.
 */

public class CopyrightAcvitity extends BaseActivity{

    private ListView mListView;
    private LinearLayout headerLayout;
    private List<String> referenceList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acvitity_copyright);
        setTitle(R.string.copyrightTitle);
        mListView = findViewById(R.id.copyright_listview);
        headerLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.copyright_listview_header,null);
        init();
        mListView.addHeaderView(headerLayout);
        mListView.setAdapter(new CopyrightListAdapter(this,referenceList));
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFuc();
            }
        });
    }

    private void init(){
        referenceList.add("com.android.support:appcompat-v7");
        referenceList.add("com.tbruyelle.rxpermissions:rxpermissions");
        referenceList.add("com.github.zhaokaiqiang.klog:library");
        referenceList.add("com.jakewharton:butterknife");
        referenceList.add("com.baoyz.swipemenulistview:library");
        referenceList.add("com.tencent.xinge:xinge");
        referenceList.add("com.tencent.wup:wup");
        referenceList.add("com.tencent.mid:mid");
        referenceList.add("com.github.bumptech.glide:glide");
        referenceList.add("com.squareup.retrofit2:retrofit");
        referenceList.add("com.squareup.okhttp3:logging-interceptor");
        referenceList.add("com.squareup.retrofit2:converter-scalars");
        referenceList.add("com.squareup.retrofit2:adapter-rxjava");
        referenceList.add("io.reactivex:rxandroid");
        referenceList.add("com.jph.takephoto:takephoto_library");
        referenceList.add("com.github.ikidou:FragmentBackHandler");
        referenceList.add("com.artemzin.rxjava:proguard-rules");
        referenceList.add("com.umeng.analytics:analytics:latest.integration");
        referenceList.add("com.commons.net");
        referenceList.add("AMap2DMap_4.2.0_AMapSearch_5.1.0_AMapLocation_3.4.0_20170517");
        referenceList.add("com.youth.banner:banner");
    }

    /**
     * 结束Activity
     * @see android.view.KeyEvent.Callback
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backFuc();
        }
        return false;
    }

    /**
     * 结束Activity
     * */
    private void backFuc() {
        this.finish();
    }
}
