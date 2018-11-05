package com.axeac.android.sdk.utils;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.StringUtil;
/**
 * 组件验证工具类
 * @author axeac
 * @version 1.0.0
 * */
public class FilterUtils {

	/**
	 * 验证组件
	 * @param ctx
	 * Context对象
	 * @param filters
	 * 有效性标识
	 * @param value
	 * */
	public static boolean doFilter(Context ctx, String[] filters, String value) {
		try {
			if (filters == null || filters.length == 0)
				return true;
			for (String filter : filters) {
				if (filter == null || "".equals(filter.trim()))
					continue;
				if ("NOTNULL".equals(filter.toUpperCase())) {
					if (value == null || "".equals(value.trim()))
						return false;
				} else if (filter.startsWith("between")) {
					int index = filter.indexOf(":");
					if (index == -1)
						continue;
					filter = filter.substring(index + 1).replace("and", "@");
					index = filter.indexOf("@");
					if (index == -1)
						continue;
					if (filter.split("@").length != 2)
						continue;
					if (value == null || "".equals(value.trim()))
						return false;
					int max = Math.max(Integer.parseInt(filter.split("@")[0].trim()),
							Integer.parseInt(filter.split("@")[1].trim()));
					int min = Math.min(Integer.parseInt(filter.split("@")[0].trim()),
							Integer.parseInt(filter.split("@")[1].trim()));
					int val = Integer.parseInt(value.trim());
					if (val > max || val < min)
						return false;
				} else if (filter.startsWith("include")) {
					int index = filter.indexOf(":");
					if (index == -1)
						continue;
					String[] vals = StringUtil.split(filter.substring(index + 1), "||");
					if (vals.length > 0) {
						for (String val : vals) {
							if (value.indexOf(val) == -1)
								return false;
						}
					}
				} else if (filter.startsWith("uninclude")) {
					int index = filter.indexOf(":");
					if (index == -1)
						continue;
					String[] vals = StringUtil.split(filter.substring(index + 1), "||");
					if (vals.length > 0) {
						for (String val : vals) {
							if (value.indexOf(val) != -1)
								return false;
						}
					}
				} else if (filter.startsWith("regex")) {
					int index = filter.indexOf(":");
					if (index == -1)
						continue;
					String regEx = filter.substring(index + 1);
					if ("".equals(regEx))
						continue;
					Pattern pat = Pattern.compile(regEx);
					if (!pat.matcher(value).matches())
						return false;
				}
			}
		} catch (Throwable e) {
			Toast.makeText(ctx, R.string.axeac_toast_exp_vilcomp, Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	/**
	 * 验证组件
	 * @param ctx
	 * Context对象
	 * @param filters
	 * 有效性标识
	 * @param value
	 * */
	public static boolean doFilter(Context ctx, String filters, String value) {
		if (filters != null && !"".equals(filters))
			return doFilter(ctx, StringUtil.split(filters, "、"), value);
		else
			return true;
	}
}