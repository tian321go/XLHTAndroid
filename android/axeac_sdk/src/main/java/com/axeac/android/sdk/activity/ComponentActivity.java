package com.axeac.android.sdk.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.analysis.ComponentAnalysis;
import com.axeac.android.sdk.customview.LoadingViewLayout;
import com.axeac.android.sdk.customview.MToolbar;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.Form;
import com.axeac.android.sdk.ui.HtmlListView;
import com.axeac.android.sdk.ui.PhotoSelector;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.NavCompBean;
import com.axeac.android.sdk.utils.StaticObject;
import com.axeac.android.sdk.utils.WaterMarkImage;
import com.jph.takephoto.app.TakePhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 显示组件视图的Activity
 * @author axeac
 * @version 1.0.0
 * */
public class ComponentActivity extends BaseActivity {

    public static ComponentActivity instance=null;
    /**
     * ProgressDialog对象
     * */
    private ProgressDialog mProgressDialog;
    /**
     * Property对象
     * */
    private Property items;
    /**
     * ComponentAnalysis对象
     * */
    private ComponentAnalysis analysis;
    /**
     * 存储页面标志的list集合
     * */
    private List<NavCompBean> navMap = new ArrayList<NavCompBean>();

    /**
     * LoadingViewLayout对象
     * */
    private LoadingViewLayout loadingView;

    protected MToolbar toolbar;
    protected TextView toolbarTitle;
    protected RelativeLayout backPage;
    protected ImageButton backPageX;

    // describe:initializa ToolBar
    /**
     * 初始化toolbar
     * */
    protected void initToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (MToolbar) v;
            toolbarTitle = (TextView) v.findViewById(R.id.layout_title);
            backPage = (RelativeLayout) v.findViewById(R.id.layout_button);
            setTitle("");
        }
    }

    // describe:When the title changes, re-set the title for the toolbar
    /**
     * 当标题变化时，为toolbar重新设置标题
     * @param title
     * 标题文本
     * @param color
     * 颜色值
     * */
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransStatus(ComponentActivity.this);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setContentView(R.layout.axeac_layout_normal);
        instance = this;
        this.registerBoradcastReceiver();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ScrollView rl = (ScrollView) findViewById(R.id.comp_layout);
        backPageX = (ImageButton)findViewById(R.id.layout_buttonX);
        loadingView = new LoadingViewLayout(this, rl);
        if (getIntent() != null) {
            loadingView.showLoading();
            getWindow().getDecorView().post(new Runnable() {

                @Override
                public void run() {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            initComp(getIntent());
                        }
                    });
                }
            });
        }
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        backPageX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        navMap.clear();
        initComp(intent);

    }
    /**
     * 显示进度框.
     * @param message
     * 进度框文字
     */
    public void showProgressDialog(String message) {
        if (!isFinishing())
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setProgressDialog();
                    mProgressDialog.show();
                }
            });
    }

    /**
     * 设置进度框.
     */
    public void setProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.axeac_login_loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
    }

    // describe:show progress dialog
    /**
     * 显示进度框.
     */
    public void showProgressDialog() {
        if (!isFinishing())
            showProgressDialog(null);
    }

    // describe:remove progress dialog
    /**
     * 移除进度框.
     */
    public void removeProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * 获得Property对象
     * @return
     * Property对象
     * */
    public Property getItems() {
        return items;
    }

    /**
     * 初始化
     * @param intent
     * Intent对象
     * */
    private void initComp(Intent intent) {
        if (intent != null) {
            items = new Property();
            items.setSplit("\n");
            items.load(intent.getStringExtra("parms"));
            Log.i("gty",items.toString());
            if (items.getProperty("Form", "Form").equals("Form")) {
                if (null!=items.getProperty("Form.id","")&&!"".equals(items.getProperty("Form.id",""))){
                    setLayout("MEIP_PAGE="+items.getProperty("Form.id",""), true);
                }else{
                    setLayout("MEIP_LOGIN=MEIP_LOGIN", true);
                }
            } else if (items.getProperty("Form", "Form").equals("Native")) {
                String androidurl = items.getProperty("Form.android_url");
                Log.i("gty","url=="+androidurl);
                if (androidurl != null && !"".equals(androidurl)) {
//                    Intent data = new Intent(androidurl);
//                    startActivity(data);

                    PackageManager packageManager = this.getPackageManager();
                    Intent data = packageManager.getLaunchIntentForPackage(androidurl);
                    if(data==null){
                        Toast.makeText(ComponentActivity.this, "应用未安装", Toast.LENGTH_SHORT).show();
                    }else {
                        startActivity(data);
                    }
                    finish();
                }
            }
        }
    }

    /**
     * 设置布局
     * @param meip
     * 请求字段
     * @param isNew
     * 是否为新布局
     * */
    private void setLayout(String meip, final boolean isNew) {
        if (isNew) {
            int index = 0;
            for (NavCompBean ncb : navMap) {
                System.out.println(ncb.getNavItems().getProperty("Form.id"));
                System.out.println(items.getProperty("Form.id"));
                if (ncb.getNavItems().getProperty("Form.id").equals(items.getProperty("Form.id")))
                    break;
                index++;
            }
            if (index < navMap.size()) {
                for (int i = index; i < navMap.size(); i++) {
                    navMap.remove(i);
                }
                navMap.add(new NavCompBean(meip, items));
            } else {
                navMap.add(new NavCompBean(meip, items));
            }
        }
        loadingView.showLoading();

        final ScrollView compLayout = clearLayout();


        compLayout.setFillViewport(false);
        getWindow().getDecorView().post(new Runnable() {

            @Override
            public void run() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        analysis = new ComponentAnalysis(ComponentActivity.this, items);
                        Form form = analysis.analysis(items);
                        if (form != null) {
                            // Set up a reminder message
                            //设置提醒消息
                            String m = form.getMessage();
                            if (m != null && !m.equals("")) {
                                Toast.makeText(ComponentActivity.this, m, Toast.LENGTH_SHORT).show();
                            }
                            String title = form.getTitle();
                            if (title != null && !"".equals(title)) {
                                ((TextView) ComponentActivity.this.findViewById(R.id.layout_title)).setText(title);
                            } else {
                                ((TextView) ComponentActivity.this.findViewById(R.id.layout_title)).setText(R.string.axeac_label);
                            }
                            compLayout.addView(form.getView());
                            form.setButton();
                        } else {
                            ((TextView) ComponentActivity.this.findViewById(R.id.layout_title)).setText(R.string.axeac_label);
                        }
                        loadingView.showContentView();
                        if (isNew) {
                            compLayout.scrollTo(0, 0);
                        }
                    }
                });
            }
        });


    }

    // describe:Clear the layout, that is, restore the layout, in order to reuse
    /**
     * 清空布局，即还原布局，以便复用
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private ScrollView clearLayout() {
        for (Component com:StaticObject.ComponentMap.values()){
            com.end();
        }
        for (Component com:StaticObject.ReturnComponentMap.values()){
            com.end();
        }
        StaticObject.ComponentMap.clear();
        StaticObject.ReturnComponentMap.clear();
        ScrollView compLayout = (ScrollView) ComponentActivity.this.findViewById(R.id.comp_layout);
        compLayout.removeAllViews();
        compLayout.setBackground(WaterMarkImage.getDrawable(StaticObject.read.getString(StaticObject.USERNAME,""),240,180,30));
        RelativeLayout btnsLayout = (RelativeLayout) ComponentActivity.this.findViewById(R.id.layout_bottom);
        btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.INVISIBLE);
        return compLayout;
    }

    // describe:Load data
    /**
     * 加载数据
     * @param meip
     * 请求字段
     */
    private void doLoading(final String meip) {
        UIHelper.send(ComponentActivity.this, meip, new OnRequestCallBack() {
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
                response.setMeip(meip);
                if (response.getReturnType().equals(StaticObject.MEIP_RETURN_FORM)) {
                    items = new Property();
                    items.setSplit("\n");
                    items.load(response.getData());
                    if (items.getProperty("Form", "").equals("Form")) {
                        setLayout(response.getMeip(), true);
                    } else {
                        // Execute the client's jump logic
                        //执行客户端的跳转逻辑
                        Toast.makeText(ComponentActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        String forward = response.getForward();
                        if (!"".equals(forward)) {
                            String[] arg = StringUtil.split(forward, ":");
                            if (arg.length < 1) {
                                return;
                            }
                            String parm = "MEIP_PAGE=" + arg[0] + "\r\n";
                            if (arg.length == 2) {
                                String[] temps = StringUtil.split(arg[1], ",");
                                for (String tmp : temps) {
                                    parm += tmp + "\r\n";
                                }
                            }
                            doLoading(parm);
                        }
                    }
                    return;
                } else {
                    // The reminder returns the result
                    //提醒返回结果
                    if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                        Toast.makeText(ComponentActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onfailed(Throwable e) {
                removeProgressDialog();
            }
        });
    }

    // describe:receive Broadcast and deal it
    /**
     * 接收广播并处理
     * */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("接受到广播...", "componentActivity");
            String action = intent.getAction();
            if (action.equals(StaticObject.BTN_NO_ACTION)) {
                ComponentActivity.this.findViewById(R.id.layout_bottom).setVisibility(View.GONE);
            } else if (action.equals(StaticObject.BTN_OFF_ACTION)) {
                ComponentActivity.this.findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
            } else if (action.equals(StaticObject.INDEX_BUTTON)) {
                loginout();
            } else if (action.equals(StaticObject.NOTIFYCATION_ACTION)){
                notiBack();
            }else {
                doLoading(intent.getStringExtra("meip"));
            }
        }
    };

    // describe:register BroadcastReceiverv
    /**
     * 注册广播
     * */
    public void registerBoradcastReceiver() {
        IntentFilter ifr = new IntentFilter();
        ifr.addAction(StaticObject.BUTTON_ACTION);
        ifr.addAction(StaticObject.CLICK_ACTION);
        ifr.addAction(StaticObject.INDEX_BUTTON);
        ifr.addAction(StaticObject.BTN_NO_ACTION);
        ifr.addAction(StaticObject.BTN_OFF_ACTION);
        ifr.addAction(StaticObject.NOTIFYCATION_ACTION);
        LocalBroadcastManager
                .getInstance(this).registerReceiver(mBroadcastReceiver, ifr);
    }

    // describe:unregister BroadcastReceiver
    /**
     * 注销广播
     * */
    public void unregisterBoradcastReceiver() {
        LocalBroadcastManager
                .getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        TakePhoto takePhoto = PhotoSelector.takePhotoMap.get(PhotoSelector.curPosition);

        if (takePhoto!=null){
            takePhoto.onActivityResult(requestCode, resultCode, data);

        }
        if (requestCode == PhotoSelector.CAMERA_WITH_DATA) {
                        Handler handler = PhotoSelector.handlerMap.get(PhotoSelector.curPosition);
            if (handler!=null) {
                handler.sendEmptyMessage(PhotoSelector.CAMERA_WITH_DATA);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onBackListener != null) {
                onBackListener.onBack();
                return true;
            }
            back();

        }
        return false;
    }

    // describe:When the return, according to the length of the navMap to determine which interface
    // to fall back and whether the back button is displayed
    /**
     * 当按返回时，根据navMap长度来判断回退到哪一界面以及回退按钮是否显示
     * */
    public void back() {
        onBackListener = null;
        if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        }
        if (navMap.size() <= 1) {
            loginout();
        } else {
            int size = navMap.size();
            this.items = navMap.get(size - 2).getNavItems();
            String meip = navMap.get(size - 2).getNavMeip();
            navMap.remove(size - 1);
            String refresh = items.getProperty("Form.refresh");
            if (refresh != null && "true".equals(refresh)) {
                navMap.remove(size - 2);
                doLoading(meip);
            } else {
                this.setLayout(null, false);
            }
        }
    }

    // describe:Exit the current activity, clear all the set, unregister the broadcast
    /**
     * 退出当前activity，清空所有集合，注销广播
     * */
    public void loginout() {
        HtmlListView.pageIndexMap.clear();
        HtmlListView.rowDataListMap.clear();
        navMap.clear();
        unregisterBoradcastReceiver();
        PhotoSelector.takePhotoMap.clear();
        PhotoSelector.handlerMap.clear();
        this.finish();
    }

    public void notiBack(){
        unregisterBoradcastReceiver();
        this.finish();
    }


    public interface OnBackListener {
        void onBack();
    }

    private OnBackListener onBackListener;

    public void setonBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

}