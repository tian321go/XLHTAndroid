package com.axeac.android.sdk.ui.datetime;

/**
 * describe：Wheel changed listener interface.
 * 			<p>The currentItemChanged() method is called whenever current wheel positions is changed:
 * 			<li> New Wheel position is set
 * 			<li> Wheel view is scrolled
 *
 * 日期轮子监听接口
 * 		<br>当当前轮子位更改时，调用currentItemChanged（）方法：
 * 		<br>设置新轮子位置
 * 		<br>滚动轮子视图
 * 	@author axeac
 * 	@version 1.0.0
 */
public interface OnDateChangedListener {
	/**
	 * describe:Callback method to be invoked when current item changed
	 *
	 * 当前item改变时调用的回调方法
	 *
	 * @param wheel
	 * DateView 时间视图对象
	 *
	 * @param oldValue
	 * 当前item的旧值
	 *
	 * @param newValue
	 * 当前item的新值
	 */
	void onChanged(DateView wheel, int oldValue, int newValue);
}