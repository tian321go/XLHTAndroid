package com.axeac.android.client.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.client.utils.update.KHDownloadProgressEvent;
import com.axeac.android.client.utils.update.KHUpgradeUtils;
import com.axeac.android.sdk.KhinfSDK;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DeviceMessage;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.StaticObject;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.File;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * describe:welcome interface
 * 欢迎界面
 * @author axeac
 * @version 2.3.0.0001
 *
 * */
public class InitActivity extends BaseActivity {
    private Intent intent;
    private Handler loginHandler;
    private Handler failureHandler;
    private Runnable loginRunnable;
    private Runnable failureRunnable;
    private ProgressDialog downloadApkDialog;
    /** 无最新版本  9901 */
    private static final int DOWNLOAD_APK_FAILTOLOGIN = 9901;
    /** 有新版本，提示更新  9902 */
    private static final int DOWNLOAD_APK_DOWNLOADPROMPT = 9902;
    private boolean overtime = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_init);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                    return;
                }
            }
        }

        // Request permission
        //请求权限
        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .subscribe(new Action1<Boolean>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            KhinfSDK.init(InitActivity.this);
                            loginHandler = new Handler();
                            loginRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    XGPushManager.registerPush(InitActivity.this);
                                    String token = StaticObject.read.getString(StaticObject.TOKEN,"");
                                    if("".equals(StaticObject.read.getString(StaticObject.TOKEN,""))||StaticObject.read.getString(StaticObject.TOKEN,"").equals("TOKEN")) {
                                        token = XGPushConfig.getToken(InitActivity.this);
                                        StaticObject.wirte.edit().putString(StaticObject.TOKEN, String.valueOf(token)).commit();
                                    }
                                    Log.d("TPush", "设备信鸽token为：" + token);
                                    if(StaticObject.read.getString(StaticObject.PASSWORD, "")!=null&&!"".equals(StaticObject.read.getString(StaticObject.PASSWORD, ""))){
                                         login("MEIP_LOGIN=MEIP_LOGIN");
                                    }else{
                                        intent = new Intent(InitActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            };
                            loginHandler.postDelayed(loginRunnable, 1000);

                            if(StaticObject.read.getString(StaticObject.PASSWORD, "")!=null&&!"".equals(StaticObject.read.getString(StaticObject.PASSWORD, ""))){

                                failureHandler = new Handler();
                                failureRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        loginHandler.removeCallbacks(loginRunnable);
                                        intent = new Intent(InitActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(InitActivity.this,R.string.init_loading,Toast.LENGTH_SHORT).show();
                                        overtime = true;
                                        finish();
                                    }
                                };
                                failureHandler.postDelayed(failureRunnable, 8000);
                            }

                        } else {
                            finish();
                        }
                    }
                });
    }

    /**
     * describe:log in
     * 请求数据，并在成功后执行跳转操作
     *
     * @param data
     * Network request field
     * 网络请求字段
     * */
    private void login(String data) {
        final String username = StaticObject.read.getString(StaticObject.USERNAME, "");
        String password = StaticObject.read.getString(StaticObject.PASSWORD, "");
        UIHelper.init(username, password);
        UIHelper.sendRequest(this, username, password, data, new OnRequestCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                failureHandler.removeCallbacks(failureRunnable);
                if(!overtime) {
                    if (response.getCode() == 0) {

                        if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                            Toast.makeText(InitActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (DeviceMessage.getParams().get("MEIP_CURRENT_USER") == null || DeviceMessage.getParams().get("MEIP_CURRENT_USER").equals("")) {
                            DeviceMessage.getParams().put("MEIP_CURRENT_USER", username);
                        }
                        intent = new Intent(InitActivity.this, MainActivity.class);
                        intent.setAction("initActivity");
                        intent.putExtra("parms", response.getData());
                        StaticObject.loginFlag = true;
                        StaticObject.wirte.edit().putString(StaticObject.CUR_USERNAME, username).commit();
//                    if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false)) {
//                        initDownloadApkDialog();
//                        checkNewVersion();
//                    } else {
                        startActivity(intent);
                        finish();
//                    }
                    } else if (response.getCode() == 502) {
                        StaticObject.wirte.edit().putString(StaticObject.CUR_USERNAME, response.getUsername()).commit();
                        Intent intent = new Intent(InitActivity.this, CheckCurUsersActivity.class);
                        intent.putExtra(StaticObject.CURUSERSDATA, response.getData());
                        startActivity(intent);
                        finish();
                    } else {
                        StaticObject.loginFlag = false;
                        DeviceMessage.getParams().clear();
                        showToast(response.getMessage());
                        Intent intent = new Intent(InitActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onfailed(Throwable e) {

            }
        });

    }

    /**
     * describe:dialog of download apk
     * apk下载对话框
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
     * describe:Detect new versions
     *
     * 检测新版本，有新版本就提示下载更新，没有则提示无最新版本
     * */
    private void checkNewVersion() {
        String username = StaticObject.read.getString(StaticObject.USERNAME, "");
        String password = StaticObject.read.getString(StaticObject.PASSWORD, "");
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
     * 显示Toast
     * @param str
     * Toast显示的文字
     * */
    private void displayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    /**
     * 处理apk下载消息的Handler
     * */
    private Handler downloadApkUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DOWNLOAD_APK_FAILTOLOGIN) {
                displayToast(getString(R.string.no_new));
                startActivity(intent);
                finish();
            }
            if (msg.what == DOWNLOAD_APK_DOWNLOADPROMPT) {
                Bundle bundle = msg.getData();
                final String path = bundle.getString("path");

                AlertDialog.Builder builder = new AlertDialog.Builder(InitActivity.this);
                builder.setMessage(bundle.getString("msg"));
                builder.setTitle(R.string.download_apk_prompt);
                builder.setPositiveButton(R.string.download_apk_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (FileUtils.checkSDCard()) {
                            downloadNewVerApk(path);
                        } else {
                            Toast.makeText(InitActivity.this, R.string.download_apk_nosdcard, Toast.LENGTH_SHORT).show();
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
     * describe:Download the new version of apk
     * 下载新版本apk
     *
     * @param path
     * apk下载地址
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
                KHUpgradeUtils.deleteOldApk(InitActivity.this);

            }
        });

        String mNewVersion = DeviceMessage.obtainAppVersion(InitActivity.this) + "1";

        if (KHUpgradeUtils.isApkFileDownloaded(InitActivity.this, mNewVersion)) {
            return;
        }

        KHUpgradeUtils.downloadApkFile(InitActivity.this, path, mNewVersion)
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
                            KHUpgradeUtils.installApk(InitActivity.this, apkFile);
                        }
                    }
                });
    }

}