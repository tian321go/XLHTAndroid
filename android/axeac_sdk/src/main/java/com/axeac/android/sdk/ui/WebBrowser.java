package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ComponentActivity;
import com.axeac.android.sdk.customview.ScrollWebView;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Web component
 * 网页组件
 * @author axeac
 * @version 1.0.0
 */
public class WebBrowser extends Component {

    /**
     * 文件类型
     * */
    private static final String MIMETYPE = "text/html";
    /**
     * 字符编码格式
     * */
    private static final String ENCODING = "UTF-8";
    private ScrollView scrollView;
    private ScrollWebView mWebView;
    /**
     * 载入的URL地址
     * */
    private String url;
    /**
     * 载入的html，优先级低于url
     * */
    private String html;
    /**
     * 标题文本
     * */
    private String title;
    /**
     * 加载文本
     * */
    private String loadingText = "Loading";
    /**
     * ProgressDialog对象
     * */
    private ProgressDialog dialog;
    /**
     * 是否旋转屏幕标志
     * */
    private boolean rotation;

    public WebBrowser(Activity ctx) {
        super(ctx);
        this.returnable = false;
        scrollView = ((ScrollView) ctx.findViewById(R.id.comp_layout));
        scrollView.setFillViewport(true);
        scrollView.setVisibility(View.GONE);
        if (height == -1){
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mWebView = new ScrollWebView(ctx.getApplicationContext());
            mWebView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }else{
            mWebView = new ScrollWebView(ctx.getApplicationContext());
            mWebView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mWebView.setITouch(new ScrollWebView.ITouch() {
            @Override
            public void onTouchPointerMult() {
                mWebView.requestDisallowInterceptTouchEvent(true);
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        settings.setDisplayZoomControls(false);//隐藏缩放控件

        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        dialog = new ProgressDialog(ctx);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(R.string.axeac_msg_prompt);
        dialog.setMessage(loadingText);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        goBack();
    }

    /**
     * 返回时，webview执行的方法
     * */
    public void goBack(){
        ((ComponentActivity) ctx).setonBackListener(new ComponentActivity.OnBackListener() {
            @Override
            public void onBack() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    return;
                }
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    if (mWebView != null) {
                        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                        mWebView.clearHistory();
                        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
                        mWebView.destroy();
                        mWebView = null;
                    }
                    ((ComponentActivity) WebBrowser.this.ctx).back();
                }
            }
        });
    }

    /**
     * 设置加载地址
     * @param url
     * url地址
     * */
    public void setUrl(String url) {
        if (url.startsWith("http:") || url.startsWith("https:")) {
            this.url = url;
        } else {
            boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
            this.url = (isHttps ? "https://" : "http://") + url + "/";
        }

    }

    /**
     * 设置载入的html
     * @param html
     * 载入的html
     * */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * 设置标题文本
     * @param title
     * 标题文本
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置载入文本
     * @param loadingText
     * 载入文本
     * */
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
        dialog.setMessage(loadingText);
    }

    public void setHeight(){
        int webviewHeight = DensityUtil.dip2px(ctx, height);
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,webviewHeight));
    }

    /**
     * 返回是否加载了url地址
     * @return
     * true表示已加载，false表示未加载
     * */
    public boolean loadURL(String url) {
        try {
            if (!"".equals(url) && "" != url) {
                mWebView.loadUrl(url);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 返回是否加载了html
     * @return
     * true表示已加载，false表示未加载
     * */
    public boolean loadHTML(String html) {
        try {
            if (!"".equals(html) && "" != html) {
                mWebView.loadDataWithBaseURL(null, html, MIMETYPE, ENCODING, null);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 设置屏幕显示方向
     * @param rotation
     * true表示横屏，false表示竖屏
     * */
    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (!this.visiable) return;

        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (height!=-1){
            setHeight();
        }

        String version = android.os.Build.VERSION.RELEASE.replace(".", "");
        System.out.println(version.replace(".", ""));
        if (title != null && !title.equals("")) {
            ((TextView) ctx.findViewById(R.id.layout_title)).setText(title);
        }
        if (url == null || "".equals(url.trim()) || "" == url.trim()) {
            if (html != null && !"".equals(html.trim()) && "" != html.trim()) {
                loadHTML(html);
            }
        } else {
            loadURL(url);
        }
    }

    @Override
    public String getValue() {
        return null;
    }

    /**
     * 返回当前视图
     * */
    @Override
    public View getView() {
        if (!this.visiable) return mWebView;
        if (rotation) {
            ctx.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ctx.findViewById(R.id.toolbar).setVisibility(View.GONE);
            ctx.findViewById(R.id.linearlayout_button).setVisibility(View.VISIBLE);
        }
        return mWebView;

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            return true;
        }
        if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void starting() {

    }

    @Override
    public void end() {

    }
}