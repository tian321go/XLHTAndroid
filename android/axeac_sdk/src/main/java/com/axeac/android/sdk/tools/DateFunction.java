package com.axeac.android.sdk.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/**
 * describe:Processing class of time
 * <br>处理时间的类
 * @author axeac
 * @version 1.0.0
 * */
public class DateFunction {

	static Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	public static Date getDateCN(Date d) {
		return addHour(d, 8 - c.getTimeZone().getRawOffset());
	}

	public static String format(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static String format(Date date, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		format = StringUtil.replace(format, "yyyy", String.valueOf(cal.get(Calendar.YEAR)));
		format = StringUtil.replace(format, "yy", String.valueOf(cal.get(Calendar.YEAR )% 100));
		format = StringUtil.replace(format, "MM", prosInt(cal.get(Calendar.MONTH) + 1));
		format = StringUtil.replace(format, "dd", prosInt(cal.get(Calendar.DAY_OF_MONTH)));
		format = StringUtil.replace(format, "HH", prosInt(cal.get(Calendar.HOUR_OF_DAY)));
		format = StringUtil.replace(format, "hh", prosInt(cal.get(Calendar.HOUR)));
		format = StringUtil.replace(format, "mm", prosInt(cal.get(Calendar.MINUTE)));
		format = StringUtil.replace(format, "ss", prosInt(cal.get(Calendar.SECOND)));
		format = StringUtil.replace(format, "SSS", String.valueOf(cal.get(Calendar.MILLISECOND)));
		format = StringUtil.replace(format, "wkCN", getWeekCN(cal.get(Calendar.DAY_OF_WEEK)));
		format = StringUtil.replace(format, "wk", String.valueOf(cal.get(Calendar.DAY_OF_WEEK)));
		return format;
	}

	public static String getWeekCN(int wk) {
		String re = "\u661f\u671f";
		if (wk == 1)
			re += "\u65e5";
		else if (wk == 2)
			re += "\u4e00";
		else if (wk == 3)
			re += "\u4e8c";
		else if (wk == 4)
			re += "\u4e09";
		else if (wk == 5)
			re += "\u56db";
		else if (wk == 6)
			re += "\u4e94";
		else if (wk == 7)
			re += "\u516d";
		return re;
	}

	private static String prosInt(int cal) {
		return (cal < 10 ? "0" + String.valueOf(cal) : String.valueOf(cal));
	}

	public static Date parse(String src, String format) {
		Calendar cal = Calendar.getInstance();
		try {
			pros(cal, src, format, "yyyy", Calendar.YEAR);
			pros(cal, src, format, "MM", Calendar.MONTH);
			pros(cal, src, format, "dd", Calendar.DAY_OF_MONTH);
			pros(cal, src, format, "HH", Calendar.HOUR_OF_DAY);
			pros(cal, src, format, "hh", Calendar.HOUR);
			pros(cal, src, format, "mm", Calendar.MINUTE);
			pros(cal, src, format, "ss", Calendar.SECOND);
			pros(cal, src, format, "SSS", Calendar.MILLISECOND);
			pros(cal, src, format, "wk", Calendar.DAY_OF_WEEK);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cal.getTime();
	}

	private static void pros(Calendar cal, String src, String format, String des, int field) {
		int pos = format.indexOf(des);
		if (pos != -1)
			cal.set(field, Integer.parseInt(src.substring(pos, pos + des.length())));
	}

	public static Date getDateCN() {
		return getDateCN(new Date());
	}

	public static Date calDate(String val) {
		Calendar c = Calendar.getInstance();
		int cy = c.get(Calendar.YEAR);
		int cm = c.get(Calendar.MONTH);
		int cd = c.get(Calendar.DAY_OF_MONTH);
		int ch = c.get(Calendar.HOUR_OF_DAY);
		int cmi = c.get(Calendar.MINUTE);
		int cs = c.get(Calendar.SECOND);
		c.setTime(new Date());
		String[] ar = StringUtil.split((String) val, "+");
		for (int i = 0; i < ar.length; i++) {
			if (ar[i].toLowerCase().equals("date"))
				continue;
			String[] t = StringUtil.split(ar[i], "-");
			for (int j = 0; j < t.length; j++) {
				String p = t[j];
				int type = 0, count = 0;
				if (p.toLowerCase().equals("cy")) {
					type = Calendar.YEAR;
					count = cy;
				} else if (p.toLowerCase().equals("cm")) {
					type = Calendar.MONTH;
					count = cm;
				} else if (p.toLowerCase().equals("cd")) {
					type = Calendar.DAY_OF_MONTH;
					count = cd;
				} else if (p.toLowerCase().equals("ch")) {
					type = Calendar.HOUR_OF_DAY;
					count = ch;
				} else if (p.toLowerCase().equals("cmi")) {
					type = Calendar.MINUTE;
					count = cmi;
				} else if (p.toLowerCase().equals("cs")) {
					type = Calendar.SECOND;
					count = cs;
				} else {
					if (p.endsWith("y")) {
						type = Calendar.YEAR;
						count = Integer.parseInt(p.substring(0, p.length() - 1));
					} else if (p.endsWith("m")) {
						type = Calendar.MONTH;
						count = Integer.parseInt(p.substring(0, p.length() - 1));
					} else if (p.endsWith("d")) {
						type = Calendar.DAY_OF_MONTH;
						count = Integer.parseInt(p.substring(0, p.length() - 1));
					} else if (p.endsWith("h")) {
						type = Calendar.HOUR_OF_DAY;
						count = Integer.parseInt(p.substring(0, p.length() - 1));
					} else if (p.endsWith("mi")) {
						type = Calendar.MINUTE;
						count = Integer.parseInt(p.substring(0, p.length() - 2));
					} else if (p.endsWith("s")) {
						type = Calendar.SECOND;
						count = Integer.parseInt(p.substring(0, p.length() - 1));
					}else{
						type = Calendar.DATE;
					}
				}
				if (j > 0)
					count = -1 * count;
				c.set(type, count + c.get(type));
			}
		}
		return c.getTime();
	}

	public static Date addDay(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.DATE, count + c.get(Calendar.DATE));
		return c.getTime();
	}

	public static Date addHour(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, count + c.get(Calendar.HOUR_OF_DAY));
		return c.getTime();
	}

	public static Date addMilliSecond(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.MILLISECOND, count + c.get(Calendar.MILLISECOND));
		return c.getTime();
	}

	public static Date addMinute(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.MINUTE, count + c.get(Calendar.MINUTE));
		return c.getTime();
	}

	public static Date addMonth(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.MONTH, count + c.get(Calendar.MONTH));
		return c.getTime();
	}

	public static Date addSecond(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.SECOND, count + c.get(Calendar.SECOND));
		return c.getTime();
	}

	public static Date addYear(Date d, int count) {
		c.setTime(d);
		c.set(Calendar.YEAR, count + c.get(Calendar.YEAR));
		return c.getTime();
	}

	// describe:Returns the number of seconds less than the specified date Positive integer
	/**
	 * 返回小于指定日期的秒数正整数
	 * @param date
	 * Dat对象
	 * @param times
	 * int类型时间
	 * */
	public static Date floorSecond(Date date, int times) {
		c.setTime(date);
		c.set(Calendar.SECOND, c.get(Calendar.SECOND) - getTimes(date) % times);
		return c.getTime();
	}

	public static Date getDate() {
		return getDate(new Date());
	}

	public static Date getDate(Date d) {
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static int getMonthDays(Date d) {
		c.setTime(d);
		int m = c.get(Calendar.MONTH) + 1;
		if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
			return 31;
		else if (m == 2) {
			c.set(Calendar.DATE, 29);
			if (c.get(Calendar.DATE)-1 == 28)
				return 29;
			else
				return 28;
		} else
			return 30;
	}

	public static Date getDateMax(Date d) {
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	public static int getHour(Date d) {
		c.setTime(d);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public static int getTimes(Date d) {
		c.setTime(d);
		return c.get(Calendar.HOUR_OF_DAY) * 3600 + c.get(Calendar.MINUTE) * 60 + c.get(Calendar.SECOND);
	}
}