package com.axeac.android.sdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.activity.ComponentActivity;
import com.axeac.android.sdk.adapters.RectangleGridAdapter;
import com.axeac.android.sdk.adapters.RectangleListAdapter;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * describe:Fragment for the navigation interface
 * 导航界面的Fragment
 * @author axeac
 * @version 1.0.0
 * */
public class MainFragment2 extends BaseFragment implements FragmentBackHandler {

    private GridView gridView;
    private ListView listview;

    /**
     * 存储页面标志的list集合
     * */
    public static List<String> navMap = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = mInflater.inflate(R.layout.axeac_fragment_main_2, null);
            gridView = (GridView) mView.findViewById(R.id.index_gridview);
            listview = (ListView) mView.findViewById(R.id.index_listview);
            initView();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        if (navMap.size()>1){
            ImageViewVisible();
        }
        return mView;
    }

    /**
     * Property对象
     * */
    private Property navLists;

    // describe:initialize
    /**
     * 初始化
     * */
    private void initView() {
        navLists = KhinfSDK.getInstance().getProperty();
        navMap.add("");
        StaticObject.isGrid = StaticObject.read.getBoolean("isGrid", false);
        if (StaticObject.isGrid) {
            showRectangleGrid();
        } else {
            showRectangleList();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticObject.CHANGE_GRID_OR_LIST_ACTION);
        filter.addAction(StaticObject.ACTION_ADD_NAV_MAP);
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver,
                filter);
    }

    /**
     * BroadcastReceiver对象
     * */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(StaticObject.CHANGE_GRID_OR_LIST_ACTION)) {
                navLists = KhinfSDK.getInstance().getProperty();
                if (StaticObject.isGrid) {
                    showRectangleGrid();
                } else {
                    showRectangleList();
                }
            } else if (action.equals(StaticObject.ACTION_ADD_NAV_MAP)) {
                navMap.add("");
            }
        }
    };

    // describe：show GrideView
    /**
     * 显示网格布局
     * */
    private void showRectangleGrid() {
        if (navMap.size()>1){
            ImageViewVisible();
        }
        gridView.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);
        LinkedHashtable lists = getNavData(navMap.get(navMap.size() - 1));
        if (lists.size() == 0) {
            return;
        }
        Vector<?> v = lists.linkedKeys();
        String[] key = new String[v.size()];
        LinkedHashtable[] value = new LinkedHashtable[v.size()];
        for (int i = 0; i < v.size(); i++) {
            key[i] = (String) v.get(i);
            value[i] = (LinkedHashtable) lists.get(v.get(i));
        }
        if (v.size() == 1 && value[0].get("type").equals("NAV")) {
            LinkedHashtable list2 = getNavData(key[0]);
            Vector<?> v2 = list2.linkedKeys();
            String[] key2 = new String[v2.size()];
            LinkedHashtable[] value2 = new LinkedHashtable[v2.size()];
            for (int i = 0; i < v2.size(); i++) {
                key2[i] = (String) v2.get(i);
                value2[i] = (LinkedHashtable) list2.get(v2.get(i));
            }
            gridView.setAdapter(new RectangleGridAdapter(act, value2));
            gridView.setOnItemClickListener(mRectangleGridListener(key2, list2));
        } else {
            gridView.setAdapter(new RectangleGridAdapter(act, value));
            gridView.setOnItemClickListener(mRectangleGridListener(key, lists));
        }
    }

    // show ListView
    /**
     *  显示列表布局
     * */
    private void showRectangleList() {
        if (navMap.size()>1){
            ImageViewVisible();
        }
        listview.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
        LinkedHashtable lists = getNavData(navMap.get(navMap.size() - 1));
        if (lists.size() == 0) {
            return;
        }
        Vector<?> v = lists.linkedKeys();
        String[] key = new String[v.size()];
        LinkedHashtable[] value = new LinkedHashtable[v.size()];
        for (int i = 0; i < v.size(); i++) {
            key[i] = (String) v.get(i);
            value[i] = (LinkedHashtable) lists.get(v.get(i));
        }
        if (v.size() == 1 && value[0].get("type").equals("NAV")) {
            LinkedHashtable list2 = getNavData(key[0]);
            Vector<?> v2 = list2.linkedKeys();
            String[] key2 = new String[v2.size()];
            LinkedHashtable[] value2 = new LinkedHashtable[v2.size()];
            for (int i = 0; i < v2.size(); i++) {
                key2[i] = (String) v2.get(i);
                value2[i] = (LinkedHashtable) list2.get(v2.get(i));
            }
            listview.setAdapter(new RectangleListAdapter(act, value2));
            listview.setOnItemClickListener(mRectangleGridListener(key2, list2));
        } else {
            listview.setAdapter(new RectangleListAdapter(act, value));
            listview.setOnItemClickListener(mRectangleGridListener(key, lists));
        }
    }

    // describe:set listener events for ListView and GrideView
    /**
     * 设置网格布局和列表布局的监听事件
     * */
    private AdapterView.OnItemClickListener mRectangleGridListener(final String[] key, final LinkedHashtable lists) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long row) {
                if (navMap.size()>0){
                    ImageViewVisible();
                }
                System.out.println(key[index]);
                System.out.println(lists);
                System.out.println(((LinkedHashtable) lists.get(key[index])));
                if (((LinkedHashtable) lists.get(key[index])).get("type").equals("PAGE")) {
                    doLoading("MEIP_PAGE=" + key[index] + "\r\n");
                } else {
                    navMap.add(key[index]);
                    LinkedHashtable list = getNavData(key[index]);
                    Vector<?> v = list.linkedKeys();
                    String[] key = new String[v.size()];
                    LinkedHashtable[] value = new LinkedHashtable[v.size()];
                    for (int i = 0; i < v.size(); i++) {
                        key[i] = (String) v.get(i);
                        value[i] = (LinkedHashtable) list.get(v.get(i));
                    }
                    if (StaticObject.isGrid) {
                        gridView.setAdapter(new RectangleGridAdapter(act, value));
                        gridView.setOnItemClickListener(mRectangleGridListener(key, list));
                    } else {
                        listview.setAdapter(new RectangleListAdapter(act, value));
                        listview.setOnItemClickListener(mRectangleGridListener(key, list));
                    }
                }
            }
        };
    }

    /**
     * 根据key值获取页面数据集合
     * @param key
     * 字符串
     * @return
     * LinkedHashtable集合
     * */
    private LinkedHashtable getNavData(String key) {
        // increase commission num logic
        //增加代办数目逻辑
        Property items = new Property();
        items.setSplit("\n");
        items.load(StaticObject.read.getString("taskcount", ""));
        Log.d("count", StaticObject.read.getString("taskcount", "").replaceAll("\r", ""));
        LinkedHashtable listscount = items.searchSubKeyDeep("var.", 1);
        String countProperty = "";
        if (listscount != null && listscount.size() > 0) {
            Vector<?> v = listscount.linkedKeys();
            for (int j = 0; j < v.size(); j++) {
                String keykey = (String) v.elementAt(j);
                countProperty += keykey + ".count = " + items.getProperty(keykey + ".text") + "\r\n";
            }
        }
        System.out.println(countProperty.replaceAll("\r", ""));
        navLists.add(new Property(countProperty));

        String[] keys = {"NAV", "PAGE"};
        LinkedHashtable lists = new LinkedHashtable();
        for (int i = 0; i < keys.length; i++) {
            String kkk = "";
            if (key.equals("")) {
                kkk = keys[i] + ".";
            } else {
                kkk = key + "." + keys[i] + ".";
            }
            LinkedHashtable list = navLists.searchSubKeyDeep(kkk, 1);
            if (list != null && list.size() > 0) {
                Vector<?> v = list.linkedKeys();
                for (int j = 0; j < v.size(); j++) {
                    String k = (String) v.elementAt(j);
                    String kkkk = "";
                    if (key.equals("")) {
                        if ("PAGE.".equals(kkk)) {
                            kkkk = "PAGE." + k + ".";
                        } else {
                            kkkk = k + ".";
                        }
                    } else {
                        if (keys[i].equals(keys[0])) {
                            kkkk = k + ".";
                        } else {
                            kkkk = keys[i] + "." + k + ".";
                        }
                    }
                    LinkedHashtable lht = navLists.searchSubKeyDeep(kkkk, 1);
                    lht.put("count", navLists.getProperty(k + ".count") == null ? "" : navLists.getProperty(k + ".count"));
                    lht.put("id", list.get(k));
                    lht.put("type", keys[i]);
                    if (keys[i].equals("PAGE")) {
                        if (lht.get("VER") != null) {
                            lht.put("ver", lht.get("VER"));
                        } else {
                            lht.put("ver", "");
                        }
                    }
                    if (keys[i].equals("PAGE")) {
                        String iconStr = (String) lht.get("PAGEICON");
                        if (iconStr != null && iconStr.startsWith("img:")) {
                            lht.put("icon", iconStr.substring(4).trim());
                        } else {
                            lht.put("icon", "");
                        }
                    } else {
                        String iconStr = (String) lht.get("ICON");
                        if (lht.get("ICON") != null && iconStr.startsWith("img:")) {
                            lht.put("icon", iconStr.substring(4).trim());
                        } else {
                            lht.put("icon", "");
                        }
                    }
                    lists.put(k, lht);
                }
            }
        }
        return lists;
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

    @Override
    public void onResume() {
        super.onResume();
        if (navMap.size()<2){
            ImageViewInVisible();
        }
    }

    // describe:Logic when handling the click Back button
    /**
     * 处理点击返回按钮时逻辑
     * */
    @Override
    public boolean onBackPressed() {
        if (navMap.size() > 0) {
            navMap.remove(navMap.size() - 1);
            if (navMap.size()==1) {
                ImageViewInVisible();
            }
        }

        if (navMap.size() == 0) {
            navMap.add("");
            return BackHandlerHelper.handleBackPress(this);
        } else if (StaticObject.isGrid) {
            showRectangleGrid();
            return true;
        } else {
            showRectangleList();
            return true;
        }

    }

    // describe:set back ImageButton InVisible
    /**
     * 设置返回图片按钮不可见
     * */
    public void ImageViewInVisible(){
        BaseActivity.backPage.setVisibility(View.INVISIBLE);
        BaseActivity.moreBtn.setVisibility(View.INVISIBLE);
    }

    // describe:set back ImageButton Visible
    /**
     * 设置返回图片按钮可见
     * */
    public void ImageViewVisible(){
        BaseActivity.backPage.setVisibility(View.VISIBLE);
        BaseActivity.moreBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(act).unregisterReceiver(receiver);
    }

}
