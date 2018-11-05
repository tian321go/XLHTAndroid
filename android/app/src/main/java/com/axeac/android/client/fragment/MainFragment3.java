package com.axeac.android.client.fragment;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.axeac.android.client.R;
import com.axeac.android.client.activity.AboutActivity;
import com.axeac.android.client.activity.MainActivity;
import com.axeac.android.client.activity.NetworkListActivity;
import com.axeac.android.client.activity.PwdUpdateActivity;
import com.axeac.android.client.activity.SettingsActivity;
import com.axeac.android.client.adapters.SettingsListAdapter;
import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.fragment.BaseFragment;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.ClearCacheUtils;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe: setting interface fragment
 * <br>设置界面fragment
 * @author axeac
 * @version 2.3.0.0001
 * */

public class MainFragment3 extends BaseFragment {

    @Bind(R.id.settings_updatepwd)
    RelativeLayout updatepwd;

    @Bind(R.id.settings_gridview)
    RelativeLayout sgridview;

    @Bind(R.id.settings_listview)
    RelativeLayout slistview;

    @Bind(R.id.settings_fresh)
    RelativeLayout fresh;

    @Bind(R.id.settings_clearcache)
    RelativeLayout clearcache;

    @Bind(R.id.settings_checknewversion)
    RelativeLayout checknewversion;

    @Bind(R.id.settings_aboutus)
    RelativeLayout aboutus;

    @Bind(R.id.settings_exit)
    RelativeLayout exit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.fragment_main_3, null);
            ButterKnife.bind(this, mView);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        return mView;
    }

    /**
     * 初始化
     * */
    private void initView() {
        updatepwd.setOnClickListener(mBtnClickListener());
        sgridview.setOnClickListener(mBtnClickListener());
        slistview.setOnClickListener(mBtnClickListener());
        fresh.setOnClickListener(mBtnClickListener());
        clearcache.setOnClickListener(mBtnClickListener());
        checknewversion.setOnClickListener(mBtnClickListener());
        aboutus.setOnClickListener(mBtnClickListener());
        exit.setOnClickListener(mBtnClickListener());
    }

    private void freshIcon(String data) {
        final String username = StaticObject.read.getString(StaticObject.USERNAME,"");
        String password = StaticObject.read.getString(StaticObject.PASSWORD,"");
        UIHelper.init(username, password);
        UIHelper.sendRequest(act, username, password, data, new OnRequestCallBack() {
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
                    Property navLists = new Property();
                    navLists.setSplit("\n");
                    navLists.load(response.getData());
                    KhinfSDK.getInstance().setProperty(navLists);
                }
                LocalBroadcastManager.getInstance(act).sendBroadcast(new Intent(StaticObject.CHANGE_GRID_OR_LIST_ACTION));
                ((MainActivity) getActivity()).showMidView();
            }

            @Override
            public void onfailed(Throwable e){
                act.removeProgressDialog();
            }
        });

    }
    // describe:Check if apk is present
    /**
     * 检查apk是否存在
     * @param context
     * Context对象
     * @param packageName
     * The package name of the apk to check
     * <br>检查的apk的包名
     * */
    boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return false;
        }
        return true;
    }

    private View.OnClickListener mBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(updatepwd)){
                    startActivity(new Intent(act, PwdUpdateActivity.class));
                }
                if (v.equals(sgridview)) {
                    StaticObject.isGrid = true;
                    StaticObject.wirte.edit().putBoolean("isGrid", true).commit();
                    LocalBroadcastManager.getInstance(act).sendBroadcast(new Intent(StaticObject.CHANGE_GRID_OR_LIST_ACTION));
                    ((MainActivity) getActivity()).showMidView();
                }
                if (v.equals(slistview)) {
                    StaticObject.isGrid = false;
                    StaticObject.wirte.edit().putBoolean("isGrid", false).commit();
                    LocalBroadcastManager.getInstance(act).sendBroadcast(new Intent(StaticObject.CHANGE_GRID_OR_LIST_ACTION));
                    ((MainActivity) getActivity()).showMidView();
                }
                if (v.equals(fresh)) {
                    freshIcon("MEIP_LOGIN=MEIP_LOGIN");
                }
                if (v.equals(clearcache)){
                    ClearCacheUtils.clearAllCache(act);
                    Toast.makeText(act, R.string.clearcache, Toast.LENGTH_SHORT).show();
                }
                if (v.equals(checknewversion)){

                }
                if (v.equals(aboutus)){
                    startActivity(new Intent(act, AboutActivity.class));
                }
                if (v.equals(exit)){
                    ((MainActivity) getActivity()).backFuc();
                }
            }
        };
    }



    String test = "Form = Form\n" +
            "Form.id = Form1\n" +
            "Form.title = PI图\n" +
            "Form.layout = BoxLayoutY\n" +
            "var.HtmlListView1 = HtmlListView\n" +
            "HtmlListView1.headHtml = <body<spa>style<equ>\"height:auto;\">\n" +
            "HtmlListView1.bottomHtml = </body>\n" +
            "HtmlListView1.template = <table<spa>align<equ>\"left\"<spa>width<equ>\"100%\"<spa>height<equ>\"70px\"><tr<spa>height<equ>\"50\"><td<spa>width<equ>\"30%\"><img<spa>src<equ>\"http://10.1.16.151:8086//img/pi.png\"<spa>width<equ>\"50px\"/><input<spa>type<equ>\"hidden\"<spa>text<equ>\"@@ID@@\"/></td><td>@@NAME@@</td></tr></table>\n" +
            "HtmlListView1.addRowData100 = ID,div_pi||NAME,自定义PI图\n" +
            "HtmlListView1.addRowClick0 = ID||NAME||ICON||PAGE:@ID:id<equ>0\n" +
            "Form.btn_add.HtmlListView1 = true\n" +
            "HtmlListView1.addRowData2 = ID,pi_ghhb||NAME,全国华环保指标\n" +
            "HtmlListView1.addRowData3 = ID,pi_ghmap||NAME,全国华实时负荷\n" +
            "HtmlListView1.addRowData4 = ID,pi_guolu1||NAME,＃1锅炉\n" +
            "HtmlListView1.addRowData5 = ID,pi_guolu2||NAME,#2锅炉\n" +
            "HtmlListView1.addRowData6 = ID,pi_qiji1||NAME,＃1汽机\n" +
            "HtmlListView1.addRowData7 = ID,pi_qiji2||NAME,＃2汽机\n" +
            "HtmlListView1.addRowData8 = ID,pi_tongxintu||NAME,#值班人员实时数据检查\n" +
            "HtmlListView1.addRowData9 = ID,pi_xitong1||NAME,＃1系统图\n" +
            "HtmlListView1.addRowData10 = ID,pi_xitong2||NAME,#2系统图\n" +
            "HtmlListView1.addRowData11 = ID,pi_yanqi2||NAME,烟气排放图\n" +
            "HtmlListView1.addRowData11 = ID,pi_yanqi3||NAME,烟气排放图\n" +
            "HtmlListView1.addRowData11 = ID,pi_yanqi4||NAME,烟气排放图\n" +
            "HtmlListView1.addRowData11 = ID,pi_yanqi5||NAME,烟气排放图\n" +
            "Form.id = new_pi\n" +
            "Form.buildDate = 2016-10-20 13:27:55";


}
