package com.axeac.android.sdk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.HtmlListOptionAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Base64Coding;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.refreshview.RefreshBase;
import com.axeac.android.sdk.ui.refreshview.RefreshWebView;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:HTML list
 * html列表
 * @author axeac
 * @version 1.0.0
 */
public class HtmlListView extends Component {

    /**
     * 数据类型
     * */
    private static final String MIMETYPE = "text/html";

    /**
     * 字符编码
     * */
    private static final String ENCODING = "UTF-8";
    private static final String ONCLICK = "onclick=\"window.runJS.runOnAndroidJavaScript(\'$^_^$');\"";

    private RefreshWebView mRefreshWebView;
    private WebView webView;

    /**
     * 存储行级点击按钮的List集合
     * */
    private List<String> rowClicks = new ArrayList<String>();

    /**
     * 存储行信息的Map集合
     * */
    public static Map<String, List<String[]>> rowDataListMap = new HashMap<String, List<String[]>>();
    /**
     * 存储页数数据的Map集合
     * */
    public static Map<String, Integer> pageIndexMap = new HashMap<String, Integer>();

    private int totPage = 1;

    private int pageCount = 20;

    /**
     * 唯一识别码
     * */
    private String uuId = "";
    /**
     * 列表前的HTML
     * <br>默认值为空
     * */
    private String headHtml = "";
    /**
     * 底部的HTML，中间是列表的HTML
     * <br>默认值为空
     * */
    private String bottomHtml = "";
    /**
     * 模板HTML@@数字@@，数字是引用addRowData中的个数
     * */
    private String template = "";

    private String html = "";
    /**
     * 下次请求的MEIP
     * */
    private String next = "";

    /**
     * 是否自动加载标志
     * <br>默认为true
     * */
    private boolean autoLoad = true;

    public HtmlListView(Activity ctx, String uuId) {
        super(ctx);
        this.uuId = uuId;
        this.returnable = false;
//        if (!rowDataListMap.containsKey(uuId)) {
            rowDataListMap.put(uuId, new ArrayList<String[]>());
//        }
//        if (!pageIndexMap.containsKey(uuId)) {
            pageIndexMap.put(uuId, 0);
//        }
        mRefreshWebView = new RefreshWebView(ctx, RefreshBase.MODE_BOTH);
        mRefreshWebView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mRefreshWebView.setOnRefreshListener(defaultOnRefreshListener);
        webView = mRefreshWebView.getRefreshableView();

    }

    /**
     * 刷新加载数据
     * */
    private final RefreshBase.OnRefreshListener defaultOnRefreshListener = new RefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh() {
            System.out.println("=================refresh=================");
            int index = pageIndexMap.get(uuId) - 1;
            if (index > 0) {
                pageIndexMap.put(uuId, index);
                execute(index);
                getView();
            } else {
                Toast.makeText(ctx, R.string.axeac_toast_exp_reqdata, Toast.LENGTH_SHORT).show();
                onNextOpt(1);
            }
        }

        @Override
        public void onLoad() {
            System.out.println("==================reload==================");
            int index = pageIndexMap.get(uuId) + 1;
            if (index <= totPage) {
                pageIndexMap.put(uuId, index);
                execute(index);
                getView();
            } else {
                onNextOpt(index);
            }
        }
    };

    /**
     * 加载下一个数据
     * @param index
     * 指定加载的位置
     * */
    private void onNextOpt(final int index) {
        String[] cs = StringUtil.split(next, "||");
        if (cs.length != 4) {
            Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
            getView();
            return;
        }
        String click = cs[3];
        if (click.equals("")) {
            Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
            getView();
            return;
        }
        String str = "";
        String vs[] = StringUtil.split(click, ":");
        if (vs.length >= 2) {
            String id = vs[1];
            String params[];
            if (vs.length >= 3) {
                params = StringUtil.split(vs[2], ",");
                for (int i = 0; i < params.length; i++) {
                    int pos = params[i].indexOf("=");
                    if (pos >= 0) {
                        String s = params[i].substring(0, pos).trim();
                        if (s.equals("pageIndex")) {
                            str += params[i].substring(0, pos) + "=" + index + "\r\n";
                        } else if (s.equals("pageSize")) {
                            str += params[i].substring(0, pos) + "=" + pageCount + "\r\n";
                        }
                    }
                }
            }
        }
        final String meip = str;
//        final Handler m_Handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                Bundle bund = msg.getData();
//                if (msg.what == 0) {
//                    String data = bund.getString("data");
//                    if (data.trim().equals("null")) {
//                        Toast.makeText(ctx, R.string.toast_exp_loadnodata, Toast.LENGTH_SHORT).show();
//                        getView();
//                        return;
//                    }
//                    int indexxx = data.indexOf(".");
//                    String key = "";
//                    if (indexxx != -1) {
//                        key = data.substring(0, indexxx);
//                        Property items = new Property(bund.getString("data"));
//                        LinkedHashtable props = items.searchSubKey(key + ".");
//                        if (props != null && props.size() > 0) {
//                            Vector<?> subkeys = props.linkedKeys();
//                            if (subkeys != null && subkeys.size() > 0) {
//                                if (index == 1) {
//                                    rowDataListMap.get(key).clear();
//                                }
//                                for (int j = 0; j < subkeys.size(); j++) {
//                                    String prop = (String) subkeys.elementAt(j);
//                                    String val = (String) props.get(prop);
//                                    if (prop.trim().toLowerCase().startsWith("addrowdata")) {
//                                        addRowData(val);
//                                    } else if (prop.trim().toLowerCase().equals("next")) {
//                                        setNext(val);
//                                    }
//                                }
//                                pageIndexMap.put(key, index);
//                                execute(index);
//                            }
//                        }
//                    } else {
//                        Toast.makeText(ctx, R.string.toast_exp_loadexp, Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ctx, R.string.toast_exp_loadfail, Toast.LENGTH_SHORT).show();
//                }
//                getView();
//                super.handleMessage(msg);
//            }
//        };
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                JHSPResponse response = Send.send(ctx, meip);
//                Message message = new Message();
//                Bundle bund = new Bundle();
//                if (response.getCode() == 0) {
//                    bund.putString("data", response.getData());
//                } else {
//                    bund.putString("data", response.getMessage() == null ? response.getData() : response.getMessage());
//                }
//                message.what = response.getCode();
//                message.setData(bund);
//                m_Handler.sendMessageDelayed(message, 0);
//            }
//        }).start();

        UIHelper.send(ctx, meip, new OnRequestCallBack() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    String data = response.getData();
                    if (data.trim().equals("null")) {
                        Toast.makeText(ctx, R.string.axeac_toast_exp_loadnodata, Toast.LENGTH_SHORT).show();
                        getView();
                        return;
                    }
                    int indexxx = data.indexOf(".");
                    String key = "";
                    if (indexxx != -1) {
                        key = data.substring(0, indexxx);
                        Property items = new Property(data);
                        LinkedHashtable props = items.searchSubKey(key + ".");
                        if (props != null && props.size() > 0) {
                            Vector<?> subkeys = props.linkedKeys();
                            if (subkeys != null && subkeys.size() > 0) {
                                if (index == 1) {
                                    rowDataListMap.get(key).clear();
                                }
                                for (int j = 0; j < subkeys.size(); j++) {
                                    String prop = (String) subkeys.elementAt(j);
                                    String val = (String) props.get(prop);
                                    if (prop.trim().toLowerCase().startsWith("addrowdata")) {
                                        addRowData(val);
                                    } else if (prop.trim().toLowerCase().equals("next")) {
                                        setNext(val);
                                    }
                                }
                                pageIndexMap.put(key, index);
                                execute(index);
                            }
                        }
                    } else {
                        Toast.makeText(ctx, R.string.axeac_toast_exp_loadexp, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ctx, R.string.axeac_toast_exp_loadfail, Toast.LENGTH_SHORT).show();
                }
                getView();
            }

            @Override
            public void onfailed(Throwable e) {

            }
        });

    }

    /**
     * 执行方法
     * @param index
     * 位置数据
     * */
    public void execute(int index) {
        if (pageIndexMap.get(uuId) == 0) {
            index = 1;
            pageIndexMap.put(uuId, index);
        }
        pageCount = rowDataListMap.get(uuId).size();
        if (pageCount != 0) {
            if (rowDataListMap.get(uuId).size() % pageCount > 0) {
                totPage = rowDataListMap.get(uuId).size() / pageCount + 1;
            } else {
                totPage = rowDataListMap.get(uuId).size() / pageCount;
            }
        }
        html = headHtml;
        for (int i = (index - 1) * pageCount; i < index * pageCount; i++) {
            if (i == rowDataListMap.get(uuId).size()) {
                break;
            }
            String temp = template;
            String[] rowData = rowDataListMap.get(uuId).get(i);
            String tempArgs = "";
            for (int j = 0; j < rowData.length; j++) {
                String[] tempElement = StringUtil.split(rowData[j], ",");
                if (temp.contains(tempElement[0])) {
                    String value = ctx.getString(R.string.axeac_toast_exp_nodata);
                    if (tempElement.length > 1) {
                        value = tempElement[1];
                    }
                    temp = temp.replace("@@" + tempElement[0] + "@@", value);
                }
                if (tempElement.length > 1) {
                    tempArgs += tempElement[0].trim() + "," + tempElement[1].trim() + "||";
                }
            }
            if (!tempArgs.equals("")) {
                tempArgs = Base64Coding.encode(tempArgs.getBytes());
            }
            if (rowClicks.size() > 0) {
                if (temp.substring(0, 6).equals("<table")) {
                    temp = "<table " + ONCLICK.replace("$^_^$", tempArgs) + temp.substring(temp.indexOf("<table") + 6);
                }
                if (temp.substring(0, 3).equals("<tr")) {
                    temp = "<tr " + ONCLICK.replace("$^_^$", tempArgs) + temp.substring(temp.indexOf("<tr") + 3);
                }
                if (temp.substring(0, 4).equals("<div")) {
                    temp = "<div " + ONCLICK.replace("$^_^$", tempArgs) + temp.substring(temp.indexOf("<div") + 3);
                }
            }
            html += temp;
        }
        html += bottomHtml;
    }

    private Handler mHandler = new Handler();


    /**
     * 运行js
     * */
    final class runJavaScript {

        @JavascriptInterface
        public void runOnAndroidJavaScript(final String arg) {
            mHandler.post(new Runnable() {
                public void run() {
                    try {
                        if (rowClicks.size() == 0) {
                            return;
                        }
                        if (rowClicks.size() == 1) {
                            String[] cs = StringUtil.split(rowClicks.get(0), "||");
                            if (cs.length != 4) {
                                Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String click = cs[3];
                            if (click.equals("")) {
                                Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String str = "";
                            String vs[] = StringUtil.split(click, ":");
                            if (vs.length >= 2) {
                                String id = vs[1];
                                String params[];
                                if (vs.length >= 3) {
                                    params = StringUtil.split(vs[2], ",");
                                    Map<String, String> argsMap = new HashMap<String, String>();
                                    String[] args = StringUtil.split(new String(Base64Coding.decode(arg)), "||");
                                    for (int j = 0; j < args.length; j++) {
                                        if (args[j].indexOf(",") > 0) {
                                            String[] item = StringUtil.split(args[j], ",");
                                            argsMap.put(item[0], item[1]);
                                        }
                                    }
                                    for (String param : params) {
                                        String kv[] = StringUtil.split(param, "=");
                                        if (kv.length >= 2) {
                                            if (argsMap.containsKey(kv[1])) {
                                                str += kv[0] + "=" + argsMap.get(kv[1]) + "\r\n";
                                            } else {
                                                str += kv[0] + "=" + kv[1] + "\r\n";
                                            }
                                        }
                                    }
                                    if (id.startsWith("@"))
                                        id = argsMap.get(id.substring(1));
                                }
                                if (click.startsWith("PAGE")) {
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                    intent.putExtra("meip", "MEIP_PAGE=" + id + "\r\n" + str);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                } else if (click.startsWith("OP")) {
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                    intent.putExtra("meip", "MEIP_ACTION=" + id + "\r\n" + str);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                }
                            }
                        } else {
                            // ID||Name||Icon||PAGE:PAGEID:id=0,name=1
                            final List<String> idList = new ArrayList<String>();
                            final List<String> nameList = new ArrayList<String>();
                            final List<String> iconList = new ArrayList<String>();
                            final List<String> typeList = new ArrayList<String>();
                            final List<String> meipList = new ArrayList<String>();
                            for (int i = 0; i < rowClicks.size(); i++) {
                                String[] ar = StringUtil.split(rowClicks.get(i), "||");
                                if (ar.length != 4 || arg == null || "".equals(arg)) {
                                    Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String click = ar[3];
                                if (click.equals("")) {
                                    Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String str = "";
                                String vs[] = StringUtil.split(click, ":");
                                if (vs.length >= 2) {
                                    String id = vs[1];
                                    String params[];
                                    if (vs.length >= 3) {
                                        params = StringUtil.split(vs[2], ",");
                                        Map<String, String> argsMap = new HashMap<String, String>();
                                        String[] args = StringUtil.split(new String(Base64Coding.decode(arg)), "||");
                                        for (int j = 0; j < args.length; j++) {
                                            if (args[j].indexOf(",") > 0) {
                                                String[] item = StringUtil.split(args[j], ",");
                                                argsMap.put(item[0], item[1]);
                                            }
                                        }
                                        for (String param : params) {
                                            String kv[] = StringUtil.split(param, "=");
                                            if (kv.length >= 2) {
                                                if (argsMap.containsKey(kv[1])) {
                                                    str += kv[0] + "=" + argsMap.get(kv[1]) + "\r\n";
                                                } else {
                                                    str += kv[0] + "=" + kv[1] + "\r\n";
                                                }
                                            }
                                        }
                                        if (id.startsWith("@"))
                                            id = argsMap.get(id.substring(1));
                                    }
                                    idList.add(id);
                                    nameList.add(ar[1]);
                                    iconList.add(ar[2]);
                                    typeList.add(click);
                                    meipList.add(str);
                                }
                            }
                            CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                            builder.setTitle(R.string.axeac_msg_choice);
                            ListView lv = new ListView(ctx);
                            lv.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.FILL_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            lv.setAdapter(new HtmlListOptionAdapter(ctx, nameList, iconList));
                            builder.setContentView(lv);
                            builder.setNeutralButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            final CustomDialog dialog = builder.create();
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    dialog.dismiss();
                                    if (typeList.get(position).startsWith("PAGE")) {
                                        Intent intent = new Intent();
                                        intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                        intent.putExtra("meip", "MEIP_PAGE=" + idList.get(position) + "\r\n" + meipList.get(position));
                                        LocalBroadcastManager
                                                .getInstance(ctx).sendBroadcast(intent);
                                    } else if (typeList.get(position).startsWith("OP")) {
                                        Intent intent = new Intent();
                                        intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                        intent.putExtra("meip", "MEIP_ACTION=" + idList.get(position) + "\r\n" + meipList.get(position));
                                        LocalBroadcastManager
                                                .getInstance(ctx).sendBroadcast(intent);
                                    }
                                }
                            });
                            dialog.show();
                        }
                    } catch (Throwable t) {
                        String clsName = this.getClass().getName();
                        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                        String info = ctx.getString(R.string.axeac_toast_exp_click);
                        Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 返回唯一识别码
     * @return
     * uuId 唯一识别码
     * */
    public String getUuId() {
        return uuId;
    }

    /**
     * 设置列表前的HTML
     * @param headHtml
     * 列表前的HTML
     * */
    public void setHeadHtml(String headHtml) {
        this.headHtml = headHtml;
    }

    /**
     * 设置底部的HTML
     * @param bottomHtml
     * 底部的HTML
     * */
    public void setBottomHtml(String bottomHtml) {
        this.bottomHtml = bottomHtml;
    }

    /**
     * 设置模板HTML@@数字@@，数字是引用addRowData中的个数
     * @param template
     * 模板HTML@@数字@@
     * */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * 信息1||信息2||信息3，行信息添加到，方法执行后，将当前Data结合temlpate的HTML代码
     * @param rowData
     * 添加行信息
     * */
    public void addRowData(String rowData) {
        rowDataListMap.get(uuId).add(StringUtil.split(rowData, "||"));
    }

    /**
     * 添加行级点击按钮，当存在多个事件时，弹出事件供用户选择
     * @param click
     * 行级点击按钮
     * */
    public void addRowClick(String click) {
        if (click == null || "".equals(click.trim()))
            return;
        String[] clickItems = StringUtil.split(click, "||");
        if (clickItems == null || clickItems.length == 0)
            return;
        this.rowClicks.add(click);
    }

    /**
     * 设置下次请求的MEIP
     * @param next
     * 下次请求的MEIP
     * */
    public void setNext(String next) {
        this.next = next;
        String[] cs = StringUtil.split(next, "||");
        if (cs.length == 4) {
            return;
        }
        String click = cs[3];
        if (click.equals("")) {
            return;
        }
        String vs[] = StringUtil.split(click, ":");
        if (vs.length >= 3) {
            String[] params = StringUtil.split(vs[2], ",");
            for (int i = 0; i < params.length; i++) {
                int pos = params[i].indexOf("=");
                if (pos >= 0) {
                    String s = params[i].substring(0, pos).trim();
                    if (s.equals("pageIndex")) {
                        pageCount = Integer.parseInt(params[i].substring(pos + 1).trim());
                    }
                }
            }
        }
    }

    /**
     * 设置是否自动加载标志
     * @param autoLoad
     * 可选值 true|false
     * */
    public void setAutoLoad(String autoLoad) {
        this.autoLoad = Boolean.parseBoolean(autoLoad.trim());
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        ((ScrollView) ctx.findViewById(R.id.comp_layout)).setFillViewport(true);
        int r = Integer.parseInt(bgColor.substring(0, 3));
        int g = Integer.parseInt(bgColor.substring(3, 6));
        int b = Integer.parseInt(bgColor.substring(6, 9));
        mRefreshWebView.setBackgroundColor(Color.rgb(r, g, b));
        if (this.bgImage != null && !"".equals(bgImage)) {
            BitmapDrawable draw;
            try {
                draw = new BitmapDrawable(this.ctx.getResources(), BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
                mRefreshWebView.setBackgroundDrawable(draw);
            } catch (IOException e) {
            }
        }
        mRefreshWebView.getBackground().setAlpha((int) (255 * ((float) this.alpha / 100)));
    }

    /**
     * 返回加载js后的webview
     * @return
     * 加载js后的webview
     * */
    @SuppressLint("JavascriptInterface")
    @Override
    public View getView() {
        if (!this.visiable) return mRefreshWebView;
        autoLoad = false;
        mRefreshWebView.setAutoLoad(autoLoad);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new runJavaScript(), "runJS");
        html = StringUtil.replaceAll(html, "<spa>", " ");
        html = StringUtil.replaceAll(html, "<equ>", "=");
        webView.loadDataWithBaseURL(null, html, MIMETYPE, ENCODING, null);
        return mRefreshWebView;
    }

    @Override
    public String getValue() {
        return null;
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