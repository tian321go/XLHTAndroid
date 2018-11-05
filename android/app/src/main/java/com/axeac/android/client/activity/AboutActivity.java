package com.axeac.android.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.axeac.android.client.R;
import com.axeac.android.sdk.activity.BaseActivity;
import com.axeac.android.sdk.utils.ClearCacheUtils;

/**
 *
 * 关于界面
 * describe:About Interface
 * @author axeac
 * @version 2.3.0.0001
 *
 * */
public class AboutActivity extends BaseActivity {

	private Context mContext;
	private LinearLayout convertView;
	private RelativeLayout about_clearCache;
	private RelativeLayout about_copyRight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTransStatus(AboutActivity.this);
		this.setContentView(R.layout.axeac_common_layout_normal);
		this.findViewById(R.id.settings_layout_bottom).setVisibility(View.GONE);
		setTitle(R.string.settings_about);
		mContext = this;
		FrameLayout layout = (FrameLayout) this.findViewById(R.id.settings_layout_center);
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		convertView = (LinearLayout) mInflater.inflate(R.layout.setting_aboutus,null);
		layout.addView(convertView);
		about_clearCache = this.findViewById(R.id.about_clearcache);
		about_copyRight = this.findViewById(R.id.about_copyright);
		about_clearCache.setOnClickListener(mBtnClickListener());
		about_copyRight.setOnClickListener(mBtnClickListener());

//		WebView webView = new WebView(this);
//		webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//		webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//		webView.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				view.loadUrl(url);
//				return true;
//			}
//		});
//		webView.setWebChromeClient(new WebChromeClient() {
//			@Override
//			public void onProgressChanged(WebView view, int newProgress) {
//				if (newProgress == 100) {
//					view.postInvalidate();
//				}
//			}
//		});
//		webView.setMinimumHeight(StaticObject.deviceHeight);
//		layout.addView(webView);
//		String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
//		String nu = url.substring(0,url.lastIndexOf("/"))+"/about.html";
//		webView.loadUrl(nu);
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
				if (v.equals(about_clearCache)) {
					ClearCacheUtils.clearAllCache(AboutActivity.this);
					Toast.makeText(mContext, R.string.clearcache, Toast.LENGTH_SHORT).show();
				}
				if (v.equals(about_copyRight)){
					mContext.startActivity(new Intent(mContext,CopyrightAcvitity.class));
				}
			}
		};
	}

	/**
	 * 结束Activity
	 * @see android.view.KeyEvent.Callback
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backFuc();
		}
		return false;
	}

	/**
	 * 结束Activity
	 * */
	private void backFuc() {
		this.finish();
	}
}