package com.axeac.android.sdk.ui.base;

import android.view.View;

public interface KHMAP5View {

	/**
	 * 组件执行方法
	 */
	public void execute();

	/**
	 * 返回组件视图
	 *
	 */
	public View getView();

	/**
	 * 返回字符串
	 */
	public String getValue();
	
}