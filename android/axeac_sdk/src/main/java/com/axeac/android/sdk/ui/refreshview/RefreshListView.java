package com.axeac.android.sdk.ui.refreshview;

import android.content.Context;
import android.widget.ScrollView;
/**
 * ListView刷新
 * @author axeac
 * @version 1.0.0
 * */
public class RefreshListView extends RefreshBase<ScrollView> {

	public RefreshListView(Context ctx) {
		super(ctx, "LIST");
	}

	public RefreshListView(Context ctx, int mode) {
		super(ctx, "LIST", mode);
	}
	
	@Override
	protected ScrollView createRefreshableView(Context ctx) {
		return new ScrollView(ctx);
	}

	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return mRefreshableView.getScrollY() >= (mRefreshableView.getHeight() - 2);
	}
}