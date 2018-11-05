package com.axeac.android.sdk.ui.container;

import android.view.View;

import com.axeac.android.sdk.ui.base.Component;

public interface KHMAP5Layout {
	
	public void addViewIn(Component view);
	
	public View getLayout();
	
	public void removeAll();
	
	public void removeAll(View view);

}