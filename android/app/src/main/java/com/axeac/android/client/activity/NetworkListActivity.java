package com.axeac.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.axeac.android.client.R;
import com.axeac.android.client.adapters.NetworkListAdapter;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.activity.CaptureActivity;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.NetInfo;
import com.axeac.android.sdk.utils.OrmHelper;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 显示网络地址列表的Activity
 * @author axeac
 * @version 2.3.0.0001
 * */
public class NetworkListActivity extends BaseActivity {

    private Context mContext;

    private RelativeLayout convertView;
    private SwipeMenuListView mNetworkList;

    /**
     * 存储网络名称的list集合
     * */
    private List<String> netDescList = new ArrayList<String>();
    /**
     * 存储网络地址的list集合
     * */
    private List<String> netUrlList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatus(NetworkListActivity.this);
        this.setContentView(R.layout.axeac_common_layout_normal);
        mContext = this;
        setTitle(R.string.settings_network);

        FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        convertView = (RelativeLayout) mInflater.inflate(R.layout.network_list, null);
        layout.addView(convertView);

        RelativeLayout networkAdd = (RelativeLayout) this.findViewById(R.id.menu_item_first);
        networkAdd.setVisibility(View.VISIBLE);
        networkAdd.setOnClickListener(mNetworkAddBtnClickListener());
        ImageView networkAddBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
        networkAddBtn.setImageResource(R.drawable.btn_add);
        TextView networkAddText = (TextView) this.findViewById(R.id.menu_item_first_text);
        networkAddText.setText(R.string.axeac_msg_add);

        RelativeLayout networkAdd1 = (RelativeLayout) this.findViewById(R.id.menu_item_second);
        networkAdd1.setVisibility(View.VISIBLE);
        networkAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NetworkListActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1111);
            }
        });
        ImageView networkAddBtn1 = (ImageView) this.findViewById(R.id.menu_item_second_btn);
        networkAddBtn1.setImageResource(R.drawable.axeac_scan);
        TextView networkAddText1 = (TextView) this.findViewById(R.id.menu_item_second_text);
        networkAddText1.setText(R.string.scan);

        // To determine whether it is shortcut jumps
        //判断是否快捷方式跳转
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("isShortCuts", false)) {
                Intent scanIntent = new Intent(NetworkListActivity.this, CaptureActivity.class);
                startActivityForResult(scanIntent, 1111);
            }
        }
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFuc();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    if (saveNetUrl(data.getStringExtra("result"))) {
                        reFresh();
                    } else {
                        showToast(R.string.not_useful_address);
                    }
                } catch (Exception e) {
                    showToast(R.string.not_useful_address);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    // describe:Check if the url format is correct
    /**
     * 检查url格式是否正确
     * @param url
     * url地址字符串
     * */
    public boolean checkURL(String url) {
        String regex = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
        return Pattern.matches(regex, url);
    }

    /**
     * 是否保存了设置的url
     * @param result
     * @return
     * Returns true to be saved
     * 返回true代表已保存
     * */
    private boolean saveNetUrl(String result) throws Exception {
        boolean flag = false;
        if (!result.contains(",")) {
            return flag;
        }
        String[] list = result.split("[,]");
        if (!checkURL(list[1])) {
            return flag;
        }
        if (!list[1].contains(":")) {
            return flag;
        }
        String desc = list[0];
        String ip;
        boolean isHttps = false;
        if (list[1].contains("http://")) {
            ip = list[1].substring(7, list[1].lastIndexOf(":"));
        } else if (list[1].contains("https://")) {
            isHttps = true;
            ip = list[1].substring(8, list[1].lastIndexOf(":"));
        } else {
            ip = list[1].substring(0, list[1].lastIndexOf(":"));
        }
        String name = list[1].substring(list[1].lastIndexOf("/") + 1);
        String httpport = list[1].substring(list[1].lastIndexOf(":") + 1, list[1].lastIndexOf("/"));
        if ("".equals(desc)) {
            return flag;
        }
        if ("".equals(ip)) {
            return flag;
        }
        if ("".equals(httpport)) {
            return flag;
        }
        if ("".equals(name)) {
            return flag;
        }
        String url = "";
        if ("".equals(httpport)) {
            url = (isHttps ? "https://" : "http://") + ip + "/" + name;
        } else {
            url = (isHttps ? "https://" : "http://") + ip + ":" + httpport + "/" + name;
        }

        NetInfo netinfo = new NetInfo();
        netinfo.serverdesc = desc;
        netinfo.serverurl = url;
        netinfo.servername = name;
        netinfo.serverip = ip;
        netinfo.httpport = httpport;
        netinfo.serverishttps = isHttps + "";

        List<NetInfo> desclist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                .where("serverdesc = ? ", new Object[]{desc}));
        if (desclist != null && desclist.size() > 0) {
            return flag;
        }

        List<NetInfo> urllist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                .where("serverurl = ? ", new Object[]{url}));
        if (urllist != null && urllist.size() > 0) {
            Toast.makeText(mContext, R.string.network_toast_urlexist, Toast.LENGTH_SHORT).show();
            return flag;
        }
        OrmHelper.getLiteOrm(this).save(netinfo);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        reFresh();
    }

    private void reFresh() {
        queryAllData();
        mNetworkList = (SwipeMenuListView) convertView.findViewById(R.id.network_list);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                // 设置item背景
                openItem.setWidth(DensityUtil.dip2px(NetworkListActivity.this, 72));
                openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red)));
                openItem.setTitle(getString(R.string.axeac_msg_delete));
                openItem.setTitleSize(15);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        // 设置creator
        mNetworkList.setMenuCreator(creator);
        mNetworkList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        if (netUrlList.get(position).equals(StaticObject.read.getString(StaticObject.SERVERURL, ""))) {
                            StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, "").commit();
                            StaticObject.wirte.edit().putString(StaticObject.SERVERURL, "").commit();
                        }
                        List<NetInfo> urllist = OrmHelper.getLiteOrm(mContext).query(new QueryBuilder<>(NetInfo.class)
                                .where("serverurl = ? ", new Object[]{netUrlList.get(position)}));
                        if (urllist != null && urllist.size() > 0) {
                            for (NetInfo info : urllist) {
                                OrmHelper.getLiteOrm(mContext).delete(info);
                            }
                        }
                        netDescList.remove(position);
                        netUrlList.remove(position);
                        mNetworkList.setAdapter(new NetworkListAdapter(mNetworkList, NetworkListActivity.this, netDescList, netUrlList));
                        break;
                }
                return false;
            }
        });

        mNetworkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int netId = -1;
                List<NetInfo> list = OrmHelper.getLiteOrm(mContext).query(new QueryBuilder<>(NetInfo.class)
                        .where("serverurl = ? ", new Object[]{netUrlList.get(position)}));
                if (list.size() > 0) {
                    for (NetInfo info : list) {
                        netId = info.getId();
                    }
                }

                Intent modifyIntent = new Intent(mContext, NetworkSetActivity.class);
                modifyIntent.putExtra("net_id", netId);
                startActivity(modifyIntent);
                finish();
            }
        });

        mNetworkList.setAdapter(new NetworkListAdapter(mNetworkList, this, netDescList, netUrlList));
    }

    /**
     * 搜索所有数据添加到list集合
     * */
    private void queryAllData() {
        netDescList.clear();
        netUrlList.clear();
        List<NetInfo> list = OrmHelper.getLiteOrm(this).query(NetInfo.class);

        if (list.size() > 0) {
            for (NetInfo info : list) {
                netDescList.add(info.serverdesc);
                netUrlList.add(info.serverurl);
            }
        }

    }

    /**
     * 增加按钮点击事件
     * */
    private View.OnClickListener mNetworkAddBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NetworkSetActivity.class);
                intent.putExtra("net_id", -1);
                mContext.startActivity(intent);
                finish();
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backFuc();
        }
        return false;
    }

    private void backFuc() {
        mContext.startActivity(new Intent(mContext, SettingsActivity.class));
        this.finish();
    }
}