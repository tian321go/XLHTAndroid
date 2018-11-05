package com.axeac.android.sdk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;

import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.R;
import com.axeac.android.sdk.customview.MyTabHost;
import com.axeac.android.sdk.fragment.MainFragment1;
import com.axeac.android.sdk.fragment.MainFragment2;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.StaticObject;


/**
 * 主界面Activity
 * @author axeac
 * @version 1.0.0
 * */
public class MainActivity1 extends BaseActivity {

    LinearLayout layout1;
    LinearLayout layout2;
    MyTabHost mTabHost;

    /**
     * Class类型数组，用来放置加载到Activity的Fragment
     * */
    private final Class[] fragments = {MainFragment1.class,
            MainFragment2.class};

    /**
     * Property对象
     * */
    private Property navLists;

    /**
     * 获取Property对象
     * @return
     * Property对象
     * */
    public Property getNavLists() {
        return navLists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.axeac_activity_main);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        mTabHost = (MyTabHost) findViewById(android.R.id.tabhost);
        initComp(getIntent());
    }

    // describe:Initializa view
    /**
     * 初始化界面
     * */
    private void initView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("0").setIndicator("0"), KhinfSDK.getInstance().getCustomMainFramentClass(navLists), null);
        mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator("1"), KhinfSDK.getInstance().getNavFramentClass(navLists), null);
        mTabHost.setCurrentTab(0);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(0);
                mTabHost.setCurrentTab(0);
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg(1);
                mTabHost.setCurrentTab(1);
            }
        });
    }

    /**
     * 初始化
     * */
    private void initComp(Intent intent) {
        navLists = new Property();
        navLists.setSplit("\n");
        navLists.load(intent.getStringExtra("parms"));
        setTitle(navLists.getProperty("Form.title", "WorkCenter"));

        initView();

        if (!"".equals(navLists.getProperty("Form", ""))) {
            changeBg(0);
            mTabHost.setCurrentTab(0);
        } else {
            changeBg(1);
            mTabHost.setCurrentTab(1);
        }
    }


    @Override
    protected void onResume() {
        StaticObject.ismenuclick = true;
        super.onResume();
    }

    /**
     * 更改底部背景
     * @param arg0
     * 标签值
     * */
    public void changeBg(int arg0) {
        int imgId;
        int tvId;
        ImageView iv;
        TextView tv;
        for (int i = 0; i < fragments.length; i++) {
            imgId = getResources().getIdentifier("img_icon" + (i + 1),
                    "id", MainActivity1.this.getPackageName());
            tvId = getResources().getIdentifier("tv" + (i + 1),
                    "id", MainActivity1.this.getPackageName());
            iv = (ImageView) findViewById(imgId);
            tv = (TextView) findViewById(tvId);
            int resId = getResources().getIdentifier("axeac_index0" + (i + 1),
                    "drawable", MainActivity1.this.getPackageName());
            int resId2 = getResources().getIdentifier("axeac_index" + (i + 1),
                    "drawable", MainActivity1.this.getPackageName());
            if (arg0 == i) {
                iv.setImageResource(resId);
                tv.setTextColor(getResources().getColor(R.color.tv_main_bottom1));
            } else {
                iv.setImageResource(resId2);
                tv.setTextColor(getResources().getColor(R.color.tv_main_bottom));
            }
        }
    }

    /**
     * 点击返回按钮时执行
     * */
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            backFuc();
        }
    }

    /**
     * 退出方法
     * */
    private void backFuc() {

        showDialog(R.string.axeac_msg_prompt, R.string.axeac_msg_loginout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                StaticObject.loginFlag = false;
                finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                LocalBroadcastManager.getInstance(MainActivity1.this).sendBroadcast(new Intent(StaticObject.ACTION_ADD_NAV_MAP));
            }
        });
    }


}
