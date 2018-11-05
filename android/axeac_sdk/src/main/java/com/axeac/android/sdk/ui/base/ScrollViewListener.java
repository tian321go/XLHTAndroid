package com.axeac.android.sdk.ui.base;

/**
 * 当ScrollView滑动时，回调此接口
 * */
public interface ScrollViewListener {

	/**
	 * 当ScrollView滑动时，调用的方法
	 * @param scrollView
	 * 当前ScrollView对象
	 * @param x
	 * ScrollView滑动后的横坐标
	 * @param y
	 * ScrollView滑动后的纵坐标
	 * @param oldx
	 * ScrollView滑动前的横坐标
	 * @param oldy
	 * ScrollView滑动前的纵坐标
	 * */
	void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
