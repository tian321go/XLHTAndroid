package com.axeac.android.sdk.ui.datetime;
/**
 * 日期时间适配器
 * @author axeac
 * @version 1.0.0
 * */
public class DateTimeAdapter {

	/**
	 * 时间日期最小值
	 * */
	private int minValue;
	/**
	 * 时间日期最大值
	 * */
	private int maxValue;
	/**
	 * 时间日期格式
	 * */
	private String format;
	
	public DateTimeAdapter(int minValue, int maxValue) {
		this(minValue, maxValue, null);
	}

	public DateTimeAdapter(int minValue, int maxValue, String format) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
	}

	public String getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value = minValue + index;
			return format != null ? String.format(format, value) : Integer.toString(value);
		}
		return null;
	}

	public int getItemsCount() {
		return maxValue - minValue + 1;
	}
	
	public int getMaximumLength() {
		int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
		int maxLen = Integer.toString(max).length();
		if (minValue < 0) {
			maxLen++;
		}
		return maxLen;
	}
}