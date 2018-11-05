package com.axeac.android.sdk.ui.datetime;

/**
 * describe:Wheel scrolled listener interface.
 * 轮子滚动监听接口
 * @author axeac
 * @version 1.0.0
 */
public interface OnDateScrollListener {
	/**
	 * describe:Callback method to be invoked when scrolling started.
	 *
	 * 滚动开始时调用此回调方法
	 *
	 * @param wheel
	 * DateView 时间视图对象
	 */
	void onScrollingStarted(DateView wheel);

	/**
	 * describe:Callback method to be invoked when scrolling ended.
	 *
	 * 滚动结束时调用此回调方法
	 *
	 * @param wheel
	 * DateView 时间视图对象
	 */
	void onScrollingFinished(DateView wheel);
}