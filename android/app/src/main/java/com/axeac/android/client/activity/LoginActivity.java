package com.axeac.android.client.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.client.service.ToyVpnService;
import com.axeac.android.client.utils.update.KHDownloadProgressEvent;
import com.axeac.android.client.utils.update.KHUpgradeUtils;
import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.DataRetrofit;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DeviceMessage;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.NetInfo;
import com.axeac.android.sdk.utils.OrmHelper;
import com.axeac.android.sdk.utils.StaticObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * 用户登录界面
 * @author axeac
 * @version 2.3.0.0001
 *
 * */
public class LoginActivity extends BaseActivity {

    /** 无最新版本  9901 */
    private static final int DOWNLOAD_APK_FAILTOLOGIN = 9901;
    /** 有新版本，提示更新  9902 */
    private static final int DOWNLOAD_APK_DOWNLOADPROMPT = 9902;
    /** 开始下载apk  9903*/
    private static final int DOWNLOAD_APK_DOWNLOADSTART = 9903;
    /** 为下载设置进度  9904*/
    private static final int DOWNLOAD_APK_DOWNLOADSETPROGRESS = 9904;
    /** apk下载完成  9905*/
    private static final int DOWNLOAD_APK_DOWNLOADDONE = 9905;
    /** apk下载提示对话框对象 */
    private ProgressDialog downloadApkDialog;
    /** apk下载线程对象 */
    private Thread downloadApkThread;
    private ImageView logoImg;
    /** 设置按钮*/
    private Button mSettingsBtn;
    /** 服务器选择布局*/
    private RelativeLayout rl_chooseServer;
    /** 登录名输入框 */
    private EditText mUsernameEdit;
    /** 登录密码输入框*/
    private EditText mPasswordEdit;
    /** 服务器选择TextView*/
    private TextView mServerInf;
    /** 登录按钮*/
    private Button mLoginBtn;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        setStatusBarTint(this, android.R.color.transparent);

        if (getIntent() != null) {
            String aciton = getIntent().getAction();
            if ("android.intent.action.VIEW".equals(aciton)) {
                KhinfSDK.init(LoginActivity.this);
                Intent scanIntent = new Intent(LoginActivity.this, NetworkListActivity.class);
                scanIntent.putExtra("isShortCuts", true);
                startActivity(scanIntent);
            }
        }

        DeviceMessage.getParams().clear();
        StaticObject.clearCurRXTX();
        this.registerBoradcastReceiver();
        mSettingsBtn = (Button) this.findViewById(R.id.login_btn_setting);
        mSettingsBtn.setAlpha(0.3f);
        logoImg = (ImageView) this.findViewById(R.id.login_logo);
        mUsernameEdit = (EditText) this.findViewById(R.id.login_input_username);
        mPasswordEdit = (EditText) this.findViewById(R.id.login_input_password);
        mServerInf = (TextView) this.findViewById(R.id.login_select_serverinf);
        mLoginBtn = (Button) this.findViewById(R.id.login_btn_login);
        rl_chooseServer = (RelativeLayout) this.findViewById(R.id.login_select_serverinf_layout);

        mSettingsBtn.setOnClickListener(mSettingsBtnClickListener());
        mLoginBtn.setOnClickListener(mButtonClickListener());
        rl_chooseServer.setOnClickListener(mChooseUrlBtnClickListener());

        mUsernameEdit.setText(StaticObject.read.getString(StaticObject.USERNAME, ""));

        mServerInf.setText(StaticObject.read.getString(StaticObject.SERVERDESC, ""));
        loadBannger();
    }

    /**
     * describe:Use Glide to load the head logo image
     *
     * 加载头部logo图片
     *
     * */
    private void loadBannger() {
        String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
        if (url!=null&&!"".equals(url)){
        String url1 = url.substring(0, url.lastIndexOf("/")) + "/ResourceServer?id=loginbanner";
        Glide.with(getApplicationContext())
                .load(url1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(logoImg);
        }
    }

    /**
     * 服务器选择监听
     * */
    private View.OnClickListener mChooseUrlBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> serverDescs = queryServerDesc();
                final String[] strs = serverDescs.keySet().toArray(new String[0]);

                //select service dialog
                //服务器选择dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getString(R.string.server_list))
                        .setIcon(R.mipmap.icon)
                        .setItems(strs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mServerInf.setText(strs[which]);
                                mServerInf.invalidate();
                                writeServerInfo(strs[which], serverDescs.get(strs[which]));
                                DataRetrofit.isNeedRefresh = true;
                                loadBannger();
                            }
                        });
                builder.create();
                builder.show();
            }
        };
    }

    /**
     * describe:Write the service information to SharedPreferences
     *
     * 将服务器信息保存到SharedPreferences
     *
     * @param desc
     * 服务器名称
     * @param url
     * 服务器地址
     * */
    private void writeServerInfo(String desc, String url) {
        List<NetInfo> list = OrmHelper.getLiteOrm(LoginActivity.this).query(new QueryBuilder<>(NetInfo.class)
                .where("serverdesc = ? and serverurl = ?", new Object[]{desc, url}));

        String ip = "";
        String name = "";
        String httpport = "";
        String vpnip = "";
        String vpnport = "";
        String isHttps = "";

        if (list.size() != 0) {
            for (NetInfo data : list) {
                ip = data.serverip;
                name = data.servername;
                httpport = data.httpport;
                vpnip = data.vpnip;
                vpnport = data.vpnport;
                isHttps = data.serverishttps;
            }
        }
        StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, desc).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL, url).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_IP, ip).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_SERVERNAME, name).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_HTTP_PORT, httpport).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_IP, vpnip).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_PORT, vpnport).commit();
        StaticObject.wirte.edit().putBoolean(StaticObject.SERVERURL_IS_HTTPS, "true".equals(isHttps)).commit();
    }

    /**
     * Toast展示
     * @param str
     * Toast展示的文字
     * */
    private void displayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    /**
     * describe:Set the download prompt dialog
     *
     * 初始化apk下载对话框
     * */
    private void initDownloadApkDialog() {
        downloadApkDialog = new ProgressDialog(this);
        downloadApkDialog.setTitle(R.string.download_prompt);
        downloadApkDialog.setMessage(this.getString(R.string.download_prompt_msg));
        downloadApkDialog.setCancelable(true);
        downloadApkDialog.setCanceledOnTouchOutside(false);
        downloadApkDialog.setIndeterminate(false);
        downloadApkDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }


    /**
     * describe:check new version
     * 检测apk新版本
     * */
    private void checkNewVersion() {
        String username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        UIHelper.sendRequestCom(this, username, password, "MEIP_ACTION = khmap5.action.version", new OnRequestCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    Property data = new Property(response.getData());
                    String url = StaticObject.read.getString(StaticObject.SERVERURL, "").replace(StaticObject.HTTPSERVER, StaticObject.COMMONSERVER);
                    url = url.replace(StaticObject.COMMONSERVER, "");
                    String path = url + data.getProperty("DOWNLOADURL");
                    String ISFORCE = data.getProperty("ISFORCE");
                    Message msg = new Message();
                    msg.what = DOWNLOAD_APK_DOWNLOADPROMPT;
                    Bundle bundle = new Bundle();
                    bundle.putString("path", path);
                    bundle.putString("ISFORCE", ISFORCE);
                    bundle.putString("msg", getString(R.string.found_new));
                    msg.setData(bundle);
                    downloadApkUIHandler.sendMessage(msg);
                } else {
                    downloadApkUIHandler.sendEmptyMessage(DOWNLOAD_APK_FAILTOLOGIN);
                }
            }

            @Override
            public void onfailed(Throwable e) {
                downloadApkUIHandler.sendEmptyMessage(DOWNLOAD_APK_FAILTOLOGIN);
            }
        });
    }

    /**
     * describe:Download the new version of apk
     * 下载新版本apk
     * @param path
     * 下载路径
     * */
    public void downloadNewVerApk(final String path) {
        KHUpgradeUtils.getDownloadProgressEventObservable()
                .subscribe(new Action1<KHDownloadProgressEvent>() {
                    @Override
                    public void call(KHDownloadProgressEvent downloadProgressEvent) {
                        if (downloadApkDialog != null && downloadApkDialog.isShowing() && downloadProgressEvent.isNotDownloadFinished()) {
                            downloadApkDialog.setProgress((int) downloadProgressEvent.getProgress());
                            downloadApkDialog.setMax((int) downloadProgressEvent.getTotal());
                        }
                    }
                });
        downloadApkDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (KHUpgradeUtils.getRequsetCall() != null)
                    KHUpgradeUtils.getRequsetCall().cancel();
                KHUpgradeUtils.deleteOldApk(LoginActivity.this);

            }
        });

        String mNewVersion = DeviceMessage.obtainAppVersion(LoginActivity.this) + "1";

        if (KHUpgradeUtils.isApkFileDownloaded(LoginActivity.this, mNewVersion)) {
            return;
        }

        KHUpgradeUtils.downloadApkFile(LoginActivity.this, path, mNewVersion)
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onStart() {
                        if (downloadApkDialog != null && !downloadApkDialog.isShowing()) {
                            downloadApkDialog.show();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (downloadApkDialog != null) {
                            downloadApkDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (downloadApkDialog != null) {
                            downloadApkDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(File apkFile) {
                        if (apkFile != null) {
                            KHUpgradeUtils.installApk(LoginActivity.this, apkFile);
                        }
                    }
                });
    }

    /**
     * 处理apk下载消息的Handler
     * */
    private Handler downloadApkUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DOWNLOAD_APK_FAILTOLOGIN) {
                displayToast(getString(R.string.no_new));
                unregisterBoradcastReceiver();
                startActivity(intent);
                finish();
            }
            if (msg.what == DOWNLOAD_APK_DOWNLOADPROMPT) {
                Bundle bundle = msg.getData();
                final String path = bundle.getString("path");

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(bundle.getString("msg"));
                builder.setTitle(R.string.download_apk_prompt);
                builder.setPositiveButton(R.string.download_apk_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (FileUtils.checkSDCard()) {
                            downloadNewVerApk(path);
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.download_apk_nosdcard, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                String ISFORCE = bundle.getString("ISFORCE");
                if ("F".equals(ISFORCE) || "f".equals(ISFORCE)) {
                    builder.setNegativeButton(R.string.download_apk_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                builder.create().show();
            }
        }

        ;
    };

    /**
     * 查找服务器信息
     * @return
     * 包含服务器名称和url的Map集合
     * */
    private Map<String, String> queryServerDesc() {
        List<NetInfo> list = OrmHelper.getLiteOrm(LoginActivity.this).query(NetInfo.class);
        Map<String, String> urlList = new LinkedHashMap<>();
        if (list.size() > 0) {
            for (NetInfo info : list) {
                urlList.put(info.serverdesc, info.serverurl);
            }
        }
        return urlList;
    }

    /**
     * 设置按钮的监听，点击跳转至SettingsActivity
     * */
    private View.OnClickListener mSettingsBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        };
    }

    /**
     * describe:click listener to log in button
     * 登录按钮的监听，当用户名、密码以及服务器都正确时执行登录
     * */
    private View.OnClickListener mButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEdit.getText().toString().trim();
                String password = mPasswordEdit.getText().toString().trim();
                String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
                if (mUsernameEdit.getText() == null || "".equals(username)) {
                    mUsernameEdit.setError(LoginActivity.this.getResources().getString(R.string.login_usernotnull));
                    return;
                } else if (mPasswordEdit.getText() == null || "".equals(password)) {
                    mPasswordEdit.setError(LoginActivity.this.getResources().getString(R.string.login_pwdnotnull));
                    return;
                }

                else if ("".equals(url)) {
                    Toast.makeText(LoginActivity.this, R.string.login_urlnotnull, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    XGPushManager.registerPush(LoginActivity.this);
                    String token = XGPushConfig.getToken(LoginActivity.this);
                    StaticObject.wirte.edit().putString(StaticObject.TOKEN, String.valueOf(token)).commit();
                    Log.d("TPush", "注册成功，设备token为：" + token);
                    login("MEIP_LOGIN=MEIP_LOGIN");
                }
            }
        };
    }

    /**
     * describe:save password，easy to sign in next time
     * 保存登录密码，方便下次登录
     * */
    private void savePassWord() {
        String username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        StaticObject.wirte.edit().putString(StaticObject.USERNAME, username).commit();
        StaticObject.wirte.edit().putString(StaticObject.PASSWORD, password).commit();
        StaticObject.wirte.edit().putLong(StaticObject.LOGINTIME, System.currentTimeMillis()).commit();
    }

    @Override
    protected void onResume() {
        mServerInf.setText(StaticObject.read.getString(StaticObject.SERVERDESC, ""));
        super.onResume();
    }

    /**
     * 广播接收者，处理广播
     * */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(StaticObject.BUTTON_ACTION)) {
                String name = intent.getStringExtra("name");
                if (name.equals("curUsersSavedButton")) {
                    login("MEIP_LOGIN=MEIP_LOGIN");
                }
            }
        }
    };

    /**
     * describe:register BroadcastReceiver
     * 注册BroadcastReceiver
     * */
    public void registerBoradcastReceiver() {
        IntentFilter ifr = new IntentFilter();
        ifr.addAction(StaticObject.BUTTON_ACTION);
        registerReceiver(mBroadcastReceiver, ifr);
    }

    /**
     * describe:unregister BroadcastReceiver
     * 注销BroadcastReceiver
     * */
    public void unregisterBoradcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DeviceMessage.getParams().clear();
            this.unregisterBoradcastReceiver();
            System.exit(0);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * describe:After the request data is successful, log in to the main interface
     * 请求数据方法，成功后登录到主界面
     *
     * @param data
     * 请求字段
     **/
    private void login(String data) {
        final String username = mUsernameEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        UIHelper.init(username, password);
        UIHelper.sendRequest(this, username, password, data, new OnRequestCallBack() {
            @Override
            public void onStart() {
                showProgressDialog();
            }

            @Override
            public void onCompleted() {
                removeProgressDialog();
            }

            @Override
            public void onSuccesed(JHSPResponse response) {

                if (response.getCode() == 0) {
                    if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                        Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (DeviceMessage.getParams().get("MEIP_CURRENT_USER") == null || DeviceMessage.getParams().get("MEIP_CURRENT_USER").equals("")) {
                        DeviceMessage.getParams().put("MEIP_CURRENT_USER", mUsernameEdit.getText().toString());
                    }
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setAction("loginActivity");
                    intent.putExtra("parms", response.getData());
                    savePassWord();
                    StaticObject.loginFlag = true;
                    StaticObject.wirte.edit().putString(StaticObject.CUR_USERNAME, username).commit();
//                    if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false)) {
//                        initDownloadApkDialog();
//                        checkNewVersion();
//                    } else {
                        startActivity(intent);
                        unregisterBoradcastReceiver();
                        finish();
//                    }
                } else if (response.getCode() == 502) {
                    StaticObject.wirte.edit().putString(StaticObject.CUR_USERNAME, response.getUsername()).commit();
                    Intent intent = new Intent(LoginActivity.this, CheckCurUsersActivity.class);

                    intent.putExtra(StaticObject.CURUSERSDATA, response.getData());
                    startActivity(intent);
                } else {
                    StaticObject.loginFlag = false;
                    DeviceMessage.getParams().clear();
                    showToast(response.getMessage());
                }
            }

            @Override
            public void onfailed(Throwable e) {
                removeProgressDialog();
            }
        });

    }

    /**
     * 开启vpn服务
     * */
    public void vpn() {
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            startActivityForResult(intent, 0);
        } else {
            onActivityResult(0, RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if (result == RESULT_OK) {
            String prefix = getPackageName();
            Intent intent = new Intent(this, ToyVpnService.class)
                    .putExtra(prefix + ".ADDRESS", "107.191.39.25")
                    .putExtra(prefix + ".PORT", "24")
                    .putExtra(prefix + ".SECRET", "test0");
            startService(intent);
        }
    }


    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void setupShortcuts() {
        ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);

        List<ShortcutInfo> infos = new ArrayList<>();
        Intent intent = new Intent(this, NetworkListActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("status", 1);

        ShortcutInfo info = new ShortcutInfo.Builder(this, "scan")
                .setShortLabel("扫描")
                .setLongLabel("扫描添加地址")
                .setIcon(Icon.createWithResource(this, R.drawable.axeac_scan))
                .setIntent(intent)
                .build();
        infos.add(info);

        mShortcutManager.setDynamicShortcuts(infos);
    }

}