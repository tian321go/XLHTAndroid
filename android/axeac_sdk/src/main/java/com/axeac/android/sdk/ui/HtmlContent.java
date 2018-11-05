package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ComponentActivity;
import com.axeac.android.sdk.adapters.HtmlListOptionAdapter;
import com.axeac.android.sdk.customview.MyWebView;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.container.TabContainer;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Html element component
 * html元素组件
 * @author axeac
 * @version 1.0.0
 */
public class HtmlContent extends Component {

	/**
	 * 数据的类型
	 * */
	private static final String MIMETYPE = "text/html";
	/**
	 * 编码字符集
	 * */
	private static final String ENCODING = "UTF-8";

	private MyWebView webView;

	/**
	 * 存储字符串数据的list集合
	 * */
	private Map<String,Map<String,String>> propertyList = new HashMap<String,Map<String,String>>();
	/**
	 * 存储长按某行时出现的按钮的list集合
	 * */
	private List<String> clicks = new ArrayList<String>();
	/**
	 * html代码
	 * <br>默认值为空
	 * */
	private String html = "";
	/**
	 * 执行execute方法，可以引用@@data.Id@@ ，@@Form变量@@
	 * <br>默认值为空
	  * */
	private String js = "";

//	private boolean autoHeight;

	/**
	 * 存储js中key数据的Map集合
	 * */
	private Map<String,String> datas = new HashMap<String,String>();
	/**
	 * 组件id
	 * */
	private String compId = "";

	public HtmlContent(Activity ctx, String compId) {
		super(ctx);
		this.compId = compId;
		this.returnable = false;
		webView = new MyWebView(ctx);
		webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		WebSettings settings = webView.getSettings();

		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		settings.setJavaScriptEnabled(true);
		settings.setPluginState(WebSettings.PluginState.ON);
		settings.setAppCacheEnabled(true);
//		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
//			settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
	}

    /**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if(!this.visiable) return;
		if (!js.equals("")) {
			for(String key : datas.keySet()){
				js = js.replaceAll("@@data." + key + "@@", datas.get(key));
			}
			System.out.println("htmljs="+js);
			html += js;
		}
		if (!html.equals("")) {
			for(String key : datas.keySet()){
				html = html.replaceAll("@@data." + key + "@@", datas.get(key));
			}
		}
		if (propertyList.size() > 0) {
			String script = "<script language='javascript' type='text/javascript'>";
			for(String key : propertyList.keySet()){
				for(String key1 : propertyList.get(key).keySet()){
					script += "document.getElementById('" + key + "').setAttribute('" + key1 + "','" + propertyList.get(key).get(key1) + "');";
				}
			}
			script += "</script>";
			html += script;
		}
		if(html.endsWith("https://www.axeac.com:8443/upload/'/>")){
			html = html.substring(0,html.indexOf("<img"));
		}
		html = html.replace("@@%@@","\\n");
//		((ScrollView) ctx.findViewById(R.id.comp_layout)).setFillViewport(true);
		Log.i("html",html);
		webView.loadDataWithBaseURL(null, html, MIMETYPE, ENCODING, null);
	}

	/**
	 * 设置组件存储的变量名，替换js及HTML代码里的@@data.Id@@内容
	 * */
	public void setData(String key,String data){
		datas.put(key.substring(key.indexOf("_") + 1, key.length()), data);
	}

	/**
	 * 设置html代码
	 * @param html
	 * html代码
	 * */
	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * 设置js
	 * @param js
	 * */
	public void setJs(String js) {
		this.js = js;
	}

	/**
	 * 向propertyList中添加数据
	 * @param property
	 * 添加的数据
	 * @param value
	 * 添加的数据值
	 * */
	public void setProperty(String property, String value) {
		Log.e("Property", property + ";" + value);
		String[] s = StringUtil.split(property, "_");
		if (s.length == 3) {
			if(propertyList.containsKey(s[1])){
				Map<String,String> map = propertyList.get(s[1]);
				map.put(s[2], value);
			}else{
				Map<String,String> map = new HashMap<String,String>();
				propertyList.put(s[1], map);
				map.put(s[2], value);
			}
		}
	}

	/**
	 * 设置长按某行时出现的按钮
	 * @param click
	 * */
	public void setClick(String click) {
		if(click == null || "".equals(click.trim()))
			return;
		String[] clickItems = StringUtil.split(click,"||");
		if(clickItems == null || clickItems.length != 4)
			return;
		this.clicks.add(click);
	}


//	public void setAutoHeight(String autoHeight) {
//		this.autoHeight = Boolean.parseBoolean(autoHeight);
//	}

	/**
	 * 下载附件
	 * */
	private void downloadAdjunct(String url) {
		Intent intent = new Intent();  
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		ctx.startActivity(intent);
	}

	/**
	 * 记录触摸点横坐标
	 * */
	private float x;

	/**
	 * WebView触摸事件
	 * */
	private View.OnTouchListener webViewOnTouchListener () {
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x = event.getX();
					break;
				case MotionEvent.ACTION_UP:
					if (Math.abs(event.getX() - x) < 10) {
						doClick();
					}
				case MotionEvent.ACTION_MOVE:
					if (Math.abs(event.getX() - x) > 10) {
						dispatchTouchToTabContainer(compId, event);
					}
				default:
					break;
				}
				return false;
			}
		};
	}

	/**
	 * 事件分发
	 * @param cId
	 * 组件id
	 * @param event
	 * 触摸事件
	 * */
	private void dispatchTouchToTabContainer(String cId, MotionEvent event) {
		if (ctx.getClass().getName().equals(ComponentActivity.class.getName())) {
			for (Object keyStr : ((ComponentActivity) ctx).getItems().keys().toArray()) {
				String temp = (String) keyStr;
				if (temp.endsWith(".add." + cId)) {
					String containerId = temp.substring(0, temp.indexOf("."));
					Component container = StaticObject.ComponentMap.get(containerId);
					if (container == null) continue;
					if (container.getClass().getName().equals(TabContainer.class.getName())) {
						if (event.getX() - x > 30) {
							((TabContainer) container).showLastTab();
						} else if (event.getX() - x < -30) {
							((TabContainer) container).showNextTab();
						}
					} else {
						dispatchTouchToTabContainer(containerId, event);
					}
				}
			}
		}
	}

	/**
	 * 点击事件
	 * */
	public void doClick(){
		try {
			if (clicks.size() == 0) {
				return;
			}
			if (clicks.size() == 1) {
				String click = StringUtil.split(clicks.get(0), "||")[3];
				if (click.equals("")) {
					Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
					return;
				}
				String str = "";
				String vs[] = StringUtil.split(click, ":");
				if(vs.length >= 2){
					String id = vs[1];
					String params[];
					if(vs.length >= 3){
						params = StringUtil.split(vs[2], ",");
						for(String param : params){
							String kv[] = StringUtil.split(param, "=");
							if(kv.length >= 2){
								if(datas.containsKey(kv[1])){
									if(datas.get(kv[1]) != null){
										str += kv[0] + "=" + datas.get(kv[1]) + "\r\n";
									}
								}else{
									if(propertyList.get(StringUtil.split(kv[1], ".")[0]) != null){
										str += kv[0] + "=" + propertyList.get(StringUtil.split(kv[1], ".")[0]).get(StringUtil.split(kv[1], ".")[1]) + "\r\n";
									}
								}

							}
						}
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
				final List<String> idList = new ArrayList<String>();
				final List<String> nameList = new ArrayList<String>();
				final List<String> iconList = new ArrayList<String>();
				final List<String> typeList = new ArrayList<String>();
				final List<String> meipList = new ArrayList<String>();
				for (int i = 0; i < clicks.size(); i++) {
					String[] ar = StringUtil.split(clicks.get(i), "||");
					String click = ar[3];
					if (click.equals("")) {
						Toast.makeText(ctx, R.string.axeac_toast_exp_arg, Toast.LENGTH_SHORT).show();
						return;
					}
					String str = "";
					String vs[] = StringUtil.split(click, ":");
					if(vs.length >= 2){
						String id = vs[1];
						String params[];
						if(vs.length >= 3){
							params = StringUtil.split(vs[2], ",");
							for(String param : params){
								String kv[] = StringUtil.split(param, "=");
								if(kv.length >= 2){
									if(datas.containsKey(kv[1])){
										if(datas.get(kv[1]) != null){
											str += kv[0] + "=" + datas.get(kv[1]) + "\r\n";
										}
									}else{
										if(propertyList.get(StringUtil.split(kv[1], ".")[0]) != null){
											str += kv[0] + "=" + propertyList.get(StringUtil.split(kv[1], ".")[0]).get(StringUtil.split(kv[1], ".")[1]) + "\r\n";
										}
									}
								}
							}
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
			t.printStackTrace();
			String clsName = this.getClass().getName();
			clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
			String info = ctx.getString(R.string.axeac_toast_exp_click);
			Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public String getValue() {
		return null;
	}

	/**
	 * 返回WebView对象
	 * @return
	 * WebView对象
	 * */
	@Override
	public View getView() {
//		if (!autoHeight) {
//			webView.setMinimumHeight(StaticObject.deviceHeight);
//		}
//		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();  // 接受所有网站的证书
				super.onReceivedSslError(view, handler, error);
			}
		});
//		webView.setWebChromeClient(new WebChromeClient() {
//			@Override
//			public void onProgressChanged(WebView view, int newProgress) {
//				if (newProgress == 100) {
//					view.postInvalidate();
//				}
//			}
//		});
		if(clicks.size() != 0){
			webView.setOnTouchListener(webViewOnTouchListener());
		}else{
			webView.setDownloadListener(new DownloadListener(){
				@Override  
				public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,  
						long contentLength) {
					Uri uri = Uri.parse(url);  
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
					ctx.startActivity(intent);  
				}  
			});
		}
		return webView;
	}

	@Override
	public void repaint() {

	}

	@Override
	public void starting() {

	}

	@Override
	public void end() {
		if (webView != null) {
			webView.clearHistory();
			webView.clearCache(true);
			webView.loadUrl("about:blank");
			webView.destroy();
		}
	}
}