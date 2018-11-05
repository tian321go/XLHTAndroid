package com.axeac.android.client.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.client.adapters.SettingsListAdapter;
import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.utils.ClearCacheUtils;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * 设置界面
 * @author axeac
 * @version 2.3.0.0001
 * */
public class SettingsActivity extends BaseActivity {

    private Context mContext;

    private TextView clientIdText;
    private Button btn_isdemo;
    private Button btn_thirdverify;
    private RelativeLayout setting_net;
    private RelativeLayout setting_clearCache;
    private RelativeLayout setting_checkNewversion;
    private RelativeLayout setting_about;
    private ImageView copyId;
    private boolean isdemo = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatus(SettingsActivity.this);
        this.setContentView(R.layout.settings_normal);
        setTitle(R.string.settings_label);
        mContext = this;
        clientIdText = (TextView) this.findViewById(R.id.clientId);
        btn_isdemo = (Button) this.findViewById(R.id.btn_isdemo);
        btn_thirdverify = this.findViewById(R.id.btn_thirdverify);
        setting_net = this.findViewById(R.id.settings_net);
        setting_clearCache = this.findViewById(R.id.settings_clearcache);
        setting_checkNewversion = this.findViewById(R.id.settings_checknewversion);
        setting_about = this.findViewById(R.id.settings_aboutus);
        copyId = this.findViewById(R.id.copyId);
        TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        btn_thirdverify.setOnClickListener(mBtnClickListener());
        btn_isdemo.setOnClickListener(mBtnClickListener());
        setting_net.setOnClickListener(mBtnClickListener());
        setting_clearCache.setOnClickListener(mBtnClickListener());
        setting_checkNewversion.setOnClickListener(mBtnClickListener());
        setting_about.setOnClickListener(mBtnClickListener());
        copyId.setOnClickListener(mBtnClickListener());

        if(StaticObject.read.getBoolean(StaticObject.SERVER_IS_DEMO,true)){
            btn_isdemo.setBackgroundResource(R.drawable.axeac_switch_open);
        }else{
            btn_isdemo.setBackgroundResource(R.drawable.axeac_switch_close);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String clientId = mTelephonyMgr.getDeviceId() == null ? "" : mTelephonyMgr.getDeviceId();
        clientIdText.setText(clientId);
        clientIdText.setAlpha(0.8f);
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backFuc();
            }
        });
    }

    private View.OnClickListener mBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(setting_net)){
                    mContext.startActivity(new Intent(mContext, NetworkListActivity.class));
                    finish();
                }
                if (v.equals(btn_thirdverify)) {
                    boolean thirdVerifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
                    if (thirdVerifyFlag) {
                        btn_thirdverify.setBackgroundResource(R.drawable.axeac_switch_close);
                        StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false).commit();
                    } else {
                        btn_thirdverify.setBackgroundResource(R.drawable.axeac_switch_open);
                        StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, true).commit();
                    }
                }
                if (v.equals(btn_isdemo)) {
                    if (isdemo) {
                        btn_isdemo.setBackgroundResource(R.drawable.axeac_switch_close);
                        isdemo = false;
                        StaticObject.wirte.edit().putBoolean(StaticObject.SERVER_IS_DEMO,isdemo).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_IP, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_SERVERNAME, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_HTTP_PORT, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_IP, "").commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_PORT, "").commit();
                        StaticObject.wirte.edit().putBoolean(StaticObject.SERVERURL_IS_HTTPS, false).commit();
                        KhinfSDK.deleteIp("http://10.96.6.12:8087/HttpServer",mContext);
                        KhinfSDK.deleteIp("http://10.96.6.12:8087/HttpServer",mContext);
                    } else {
                        btn_isdemo.setBackgroundResource(R.drawable.axeac_switch_open);
                        isdemo = true;
                        StaticObject.wirte.edit().putBoolean(StaticObject.SERVER_IS_DEMO,isdemo).commit();
                        KhinfSDK.getDefaultInfo(mContext);
                    }
                }
                if (v.equals(setting_clearCache)) {
                    ClearCacheUtils.clearAllCache(SettingsActivity.this);
                    Toast.makeText(mContext, R.string.clearcache, Toast.LENGTH_SHORT).show();
                }
                if (v.equals(setting_checkNewversion)){

                }
                if (v.equals(setting_about)){
                    mContext.startActivity(new Intent(mContext, AboutActivity.class));
                }
                if (v.equals(copyId)){
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(clientIdText.getText());
                    Toast.makeText(mContext, R.string.copy, Toast.LENGTH_SHORT).show();
                }
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
        this.finish();
    }
}