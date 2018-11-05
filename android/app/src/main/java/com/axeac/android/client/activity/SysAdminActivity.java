package com.axeac.android.client.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.utils.StaticObject;

public class SysAdminActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(SysAdminActivity.this);
		this.setContentView(R.layout.axeac_common_layout_normal);
		this.findViewById(R.id.settings_layout_bottom).setVisibility(View.GONE);
		setTitle(R.string.settings_sysadmin);
		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		WebView webView = new WebView(this);
		webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					view.postInvalidate();
				}
			}
		});
		webView.setMinimumHeight(StaticObject.deviceHeight);
		layout.addView(webView);
		String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
		String nu = url.substring(0,url.lastIndexOf("/"))+"/sysadmin.html";
		webView.loadUrl(nu);
		backPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backFuc();
			}
		});
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