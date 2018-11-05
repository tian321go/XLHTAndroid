package com.axeac.android.sdk.ui.refreshview;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.axeac.android.sdk.customview.MyWebView;
/**
 * WebView刷新
 * @author axeac
 * @version 1.0.0
 * */
public class RefreshWebView extends RefreshBase<WebView> {

	private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				onRefreshComplete();
			}
		}
	};

	public RefreshWebView(Context ctx) {
		super(ctx, "WEB");
		// Added so that by default, Pull-to-Refresh refreshes the page
		//默认添加，Pull-to-Refresh刷新页面
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	public RefreshWebView(Context ctx, int mode) {
		super(ctx, "WEB", mode);
		// Added so that by default, Pull-to-Refresh refreshes the page
		//默认添加，Pull-to-Refresh刷新页面
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	@Override
	protected WebView createRefreshableView(Context ctx) {
		return new MyWebView(ctx);
	}

	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return mRefreshableView.getScrollY() >= (mRefreshableView.getContentHeight() * mRefreshableView.getScale() - mRefreshableView.getHeight() - 2);
	}
}