package com.axeac.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.retrofit.DataRetrofit;
import com.axeac.android.sdk.utils.NetInfo;
import com.axeac.android.sdk.utils.OrmHelper;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 新增网络地址的Activity
 * @author axeac
 * @version 2.3.0.0001
 * */
public class NetworkSetActivity extends BaseActivity {

    private Context mContext;

    private EditText netServerDesc;
    private EditText netServerIp;
    private EditText netServerPort;
    private EditText netServerName;
    private Button btn_ishttps;
    private int netId = -1;
    /**
     * 是否为https
     * */
    private boolean isHttps = true;
    private boolean isChooseOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatus(NetworkSetActivity.this);
        this.setContentView(R.layout.axeac_common_layout_normal);
        mContext = this;
        Intent intent = this.getIntent();
        if (intent != null) {
            netId = intent.getIntExtra("net_id", -1);
        }
        if (netId > -1) {
            setTitle(R.string.settings_network_modify);
        } else {
            setTitle(R.string.settings_network_add);
        }
        FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        LinearLayout convertView = (LinearLayout) mInflater.inflate(R.layout.network_set, null);
        layout.addView(convertView);
        netServerDesc = (EditText) convertView.findViewById(R.id.network_serverdesc);
        netServerIp = (EditText) convertView.findViewById(R.id.network_serverip);
        netServerPort = (EditText) convertView.findViewById(R.id.network_serverport);
        netServerName = (EditText) convertView.findViewById(R.id.network_servername);
        btn_ishttps = (Button) convertView.findViewById(R.id.btn_ishttps);
        btn_ishttps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHttps) {
                    btn_ishttps.setBackgroundResource(R.drawable.axeac_switch_close);
                    isHttps = false;
                } else {
                    btn_ishttps.setBackgroundResource(R.drawable.axeac_switch_open);
                    isHttps = true;
                }
            }
        });
        RelativeLayout networkSave = (RelativeLayout) this.findViewById(R.id.menu_item_first);
        networkSave.setVisibility(View.VISIBLE);
        networkSave.setOnClickListener(mSaveBtnClickListener());
        ImageView networkSaveBtn = (ImageView) this.findViewById(R.id.menu_item_first_btn);
        networkSaveBtn.setImageResource(R.drawable.btn_saved);
        TextView networkSaveText = (TextView) this.findViewById(R.id.menu_item_first_text);
        networkSaveText.setText(R.string.axeac_msg_save);
        RelativeLayout networkBack = (RelativeLayout) this.findViewById(R.id.menu_item_second);
        networkBack.setVisibility(View.VISIBLE);
        networkBack.setOnClickListener(mBackBtnClickListener());
        ImageView networkBackBtn = (ImageView) this.findViewById(R.id.menu_item_second_btn);
        networkBackBtn.setImageResource(R.drawable.btn_back);
        TextView networkBackText = (TextView) this.findViewById(R.id.menu_item_second_text);
        networkBackText.setText(R.string.axeac_btn_return);

        initComp();
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFuc();
            }
        });
    }

    /**
     * 初始化
     * */
    private void initComp() {
        String desc = "";
        String ip = "";
        String servername = "HttpServer";
        String httpport = "25813";
        if (netId > -1) {

            NetInfo info = OrmHelper.getLiteOrm(mContext).queryById(netId, NetInfo.class);
            if (info != null) {
                desc = info.serverdesc;
                ip = info.serverip;
                servername = info.servername;
                httpport = info.httpport;
                isHttps = "true".equals(info.serverishttps);
                isChooseOne =  info.serverdesc.equals(StaticObject.read.getString(StaticObject.SERVERDESC, ""));
            }
        }
        netServerDesc.setText(desc);
        netServerIp.setText(ip);
        netServerPort.setText(httpport);
        netServerName.setText(servername);
        if (isHttps) {
            btn_ishttps.setBackgroundResource(R.drawable.axeac_switch_open);
        } else {
            btn_ishttps.setBackgroundResource(R.drawable.axeac_switch_close);
        }
    }

    /**
     * 保存网络地址并返回是否成功
     * @return
     * true为保存成功
     * */
    private boolean saveNetUrl() {
        boolean flag = false;
        String desc = netServerDesc.getText().toString().trim();
        String ip = netServerIp.getText().toString().trim();
        String name = netServerName.getText().toString().trim();
        String httpport = netServerPort.getText().toString().trim();
        if ("".equals(desc)) {
            netServerDesc.requestFocus();
            netServerDesc.setError(mContext.getString(R.string.network_toast_descnotnull));
            return flag;
        }
        if ("".equals(ip)) {
            netServerIp.requestFocus();
            netServerIp.setError(mContext.getString(R.string.network_toast_httpipnotnull));
            return flag;
        }
        if ("".equals(httpport)) {
            netServerPort.requestFocus();
            netServerPort.setError(mContext.getString(R.string.network_toast_httpportnotnull));
            return flag;
        }
        if ("".equals(name)) {
            netServerName.requestFocus();
            netServerName.setError(mContext.getString(R.string.network_toast_httpnamenotnull));
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
        if (netId > -1) {
            String oriDesc = "";
            String oriUrl = "";
            NetInfo info = OrmHelper.getLiteOrm(mContext).queryById(netId, NetInfo.class);
            if (info != null) {
                oriDesc = info.serverdesc;
                oriUrl = info.serverurl;
            }

            List<NetInfo> desclist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                    .where("serverdesc = ? ", new Object[]{desc}));
            if (!desc.equals(oriDesc) && desclist != null && desclist.size() > 0) {
                netServerDesc.requestFocus();
                netServerDesc.setError(mContext.getString(R.string.network_toast_descexist));
                return flag;
            }

            List<NetInfo> urllist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                    .where("serverurl = ? ", new Object[]{url}));
            if (!url.equals(oriUrl) && urllist != null && urllist.size() > 0) {
                Toast.makeText(mContext, R.string.network_toast_urlexist, Toast.LENGTH_SHORT).show();
                return flag;
            }

            netinfo.setId(netId);
            OrmHelper.getLiteOrm(this).update(netinfo);

            if (oriDesc.equals(StaticObject.read.getString(StaticObject.SERVERDESC, ""))
                    && oriUrl.equals(StaticObject.read.getString(StaticObject.SERVERURL, ""))) {
                if (!oriDesc.equals("") && !oriUrl.equals("")) {
                    StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, desc).commit();
                    StaticObject.wirte.edit().putString(StaticObject.SERVERURL, url).commit();
                    StaticObject.wirte.edit().putString(StaticObject.SERVERURL_IP, ip).commit();
                    StaticObject.wirte.edit().putString(StaticObject.SERVERURL_SERVERNAME, name).commit();
                    StaticObject.wirte.edit().putString(StaticObject.SERVERURL_HTTP_PORT, httpport).commit();
                    StaticObject.wirte.edit().putBoolean(StaticObject.SERVERURL_IS_HTTPS, isHttps).commit();
                    DataRetrofit.isNeedRefresh = true;
                }
            }
        } else {
            List<NetInfo> desclist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                    .where("serverdesc = ? ", new Object[]{desc}));
            if (desclist != null && desclist.size() > 0) {
                netServerDesc.requestFocus();
                netServerDesc.setError(mContext.getString(R.string.network_toast_descexist));
                return flag;
            }


            List<NetInfo> urllist = OrmHelper.getLiteOrm(this).query(new QueryBuilder<>(NetInfo.class)
                    .where("serverurl = ? ", new Object[]{url}));
            if (urllist != null && urllist.size() > 0) {
                Toast.makeText(mContext, R.string.network_toast_urlexist, Toast.LENGTH_SHORT).show();
                return flag;
            }
            OrmHelper.getLiteOrm(this).save(netinfo);

        }
        return true;
    }

    /**
     * 保存按钮点击事件
     * */
    private View.OnClickListener mSaveBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveNetUrl()) {
                    backFuc();
                }
            }
        };
    }

    /**
     * 返回按钮点击事件
     * */
    private View.OnClickListener mBackBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFuc();
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
        mContext.startActivity(new Intent(mContext, NetworkListActivity.class));
        this.finish();
    }
}