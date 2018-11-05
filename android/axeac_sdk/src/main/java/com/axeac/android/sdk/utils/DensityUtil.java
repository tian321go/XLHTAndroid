package com.axeac.android.sdk.utils;

import android.content.Context;

/**
 * 根据手机分辨率调整尺寸的工具类
 * @author axeac
 * @version 1.0.0
 * */
public class DensityUtil {

	/**
	 * describe:According to the resolution of the phone from dp units into px(pixels)
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * @param context
	 * Context对象
	 * @param dpValue
	 * 单位dp的值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * describe:According to the resolution of the phone from px(pixels) units into dp
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * @param context
	 * Context对象
	 * @param pxValue
	 * 像素值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}