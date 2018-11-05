package com.axeac.android.sdk.ui.datetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * 时间控件工具类
 * @author axeac
 * @version 1.0.0
 * */
public class DateTimeUtils {
	/**开始年份与最后年份*/
	private static final int START_YEAR = 1990, END_YEAR = 2100;

	/**
	 * 放置时间选择器的view
	 * */
	private View view;
	/**
	 * 日期选择器年视图
	 * */
	private DateView date_year;
	/**
	 * 日期选择器月视图
	 * */
	private DateView date_month;
	/**
	 * 日期选择器日视图
	 * */
	private DateView date_day;

	/**
	 * 时间选择器时视图
	 * */
	private DateView date_hour;
	/**
	 * 时间选择器分视图
	 * */
	private DateView date_minute;
	/**
	 * 时间选择器秒视图
	 * */
	private DateView date_second;

	/**
	 * 日期时间选择器年视图
	 * */
	private DateView date_years;
	/**
	 * 日期时间选择器月视图
	 * */
	private DateView date_months;
	/**
	 * 日期时间选择器日视图
	 * */
	private DateView date_days;
	/**
	 * 日期时间选择器时视图
	 * */
	private DateView date_hours;
	/**
	 * 日期时间选择器分视图
	 * */
	private DateView date_mins;

	public DateTimeUtils(Context ctx) {
		super();
		view = LayoutInflater.from(ctx).inflate(R.layout.axeac_datetime, null);
	}

	public void initDatePickerNotDay(int y,int m) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if(y>0){
			year = y;
		}
		if(m>0){
			month = m;
		}
		// Add the month and convert it to list, to facilitate the subsequent judgments
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// year
		// 年
		date_year = (DateView) view.findViewById(R.id.labeldatetime_1);
		// Set the display data for "year"
		// 设置"年"的显示数据
		date_year.setAdapter(new DateTimeAdapter(START_YEAR, END_YEAR));
		// Can be Scroll
		// 可循环滚动
		date_year.setCyclic(true);
		// Add text
		// 添加文字
		date_year.setLabel("");
		// The data displayed when initializing
		// 初始化时显示的数据
		date_year.setCurrentItem(year - START_YEAR);

		// month
		// 月
		date_month = (DateView) view.findViewById(R.id.labeldatetime_2);
		date_month.setAdapter(new DateTimeAdapter(1, 12));
		date_month.setCyclic(true);
		date_month.setLabel("");
		date_month.setCurrentItem(month);

		//day
		// 日
		date_day = (DateView) view.findViewById(R.id.labeldatetime_3);
		date_day.setVisibility(View.GONE);
		int textSize = (StaticObject.deviceHeight / 100) * 3;
		date_month.TEXT_SIZE = textSize;
		date_year.TEXT_SIZE = textSize;
	}

	/**
	 * describe:Pop-up date selector
	 * 弹出日期选择器
	 * @param y
	 * 年份
	 *
	 * @param m
	 * 月份
	 * @param d
	 * 日
	 */
	public void initDatePicker(int y,int m,int d) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		if(y>0){
			year = y;
		}
		if(m>0){
			month = m;
		}
		if(d>0){
			day = d;
		}
		// Add the month and convert it to list, to facilitate the subsequent judgments
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// year
		// 年
		date_year = (DateView) view.findViewById(R.id.labeldatetime_1);
		// Set the display data for "year"
		// 设置"年"的显示数据
		date_year.setAdapter(new DateTimeAdapter(START_YEAR, END_YEAR));
		// Can be Scroll
		// 可循环滚动
		date_year.setCyclic(true);
		// Add text
		// 添加文字
		date_year.setLabel("");
		// The data displayed when initializing
		// 初始化时显示的数据
		date_year.setCurrentItem(year - START_YEAR);

		// month
		// 月
		date_month = (DateView) view.findViewById(R.id.labeldatetime_2);
		date_month.setAdapter(new DateTimeAdapter(1, 12));
		date_month.setCyclic(true);
		date_month.setLabel("");
		date_month.setCurrentItem(month);

		//day
		// 日
		date_day = (DateView) view.findViewById(R.id.labeldatetime_3);
		date_day.setCyclic(true);
		// Determine the size of the month and whether the leap year, used to determine the data of the "day"
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			date_day.setAdapter(new DateTimeAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			date_day.setAdapter(new DateTimeAdapter(1, 30));
		} else {
			// leap year
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				date_day.setAdapter(new DateTimeAdapter(1, 29));
			else
				date_day.setAdapter(new DateTimeAdapter(1, 28));
		}
		date_day.setLabel("");
		date_day.setCurrentItem(day - 1);

		// Add listen to "year"
		// 添加"年"监听
		OnDateChangedListener listener_year = new OnDateChangedListener() {
			public void onChanged(DateView view, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// Determine the size of the month and whether the leap year, used to determine the data of the "day"
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(date_month.getCurrentItem() + 1))) {
					date_day.setAdapter(new DateTimeAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(date_month.getCurrentItem() + 1))) {
					date_day.setAdapter(new DateTimeAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						date_day.setAdapter(new DateTimeAdapter(1, 29));
					else
						date_day.setAdapter(new DateTimeAdapter(1, 28));
				}
			}
		};

		// Add listen to "month"
		// 添加"月"监听
		OnDateChangedListener listener_month = new OnDateChangedListener() {
			public void onChanged(DateView view, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// Determine the size of the month and whether the leap year, used to determine the data of the "day"
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					date_day.setAdapter(new DateTimeAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					date_day.setAdapter(new DateTimeAdapter(1, 30));
				} else {
					if (((date_year.getCurrentItem() + START_YEAR) % 4 == 0 && (date_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (date_year.getCurrentItem() + START_YEAR) % 400 == 0)
						date_day.setAdapter(new DateTimeAdapter(1, 29));
					else
						date_day.setAdapter(new DateTimeAdapter(1, 28));
				}
			}
		};
		date_year.addChangingListener(listener_year);
		date_month.addChangingListener(listener_month);

		int textSize = (StaticObject.deviceHeight / 100) * 3;
		date_day.TEXT_SIZE = textSize;
		date_month.TEXT_SIZE = textSize;
		date_year.TEXT_SIZE = textSize;
	}

	/**
	 * describe:Pop-up date selector
	 * 弹出时间选择器
	 * @param minuteStep
	 * 分
	 * @param secondStep
	 * 秒
	 */
	public void initTimePicker(int minuteStep, int secondStep) {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		// hour
		// 时
		date_hour = (DateView) view.findViewById(R.id.labeldatetime_1);
		date_hour.setAdapter(new DateTimeAdapter(0, 23));
		date_hour.setCyclic(true);
		date_hour.setLabel("");
		date_hour.setCurrentItem(hour);

		//minute
		// 分
		date_minute = (DateView) view.findViewById(R.id.labeldatetime_2);
		date_minute.setAdapter(new DateTimeAdapter(0, 59));
		date_minute.setCyclic(true);
		date_minute.setLabel("");
		date_minute.setCurrentItem(minute);

		// second
		// 秒
		date_second = (DateView) view.findViewById(R.id.labeldatetime_3);
		date_second.setAdapter(new DateTimeAdapter(0, 59));
		date_second.setCyclic(true);
		date_second.setLabel("");
		date_second.setCurrentItem(second);

		int textSize = (StaticObject.deviceHeight / 100) * 3;
		date_hour.TEXT_SIZE = textSize;
		date_minute.TEXT_SIZE = textSize;
		date_second.TEXT_SIZE = textSize;
	}

	/**
	 * describe:Pop-up date and time selector
	 * 弹出日期时间选择器
	 */
	public void initDateTimePicker() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// Add the month and convert it to list, to facilitate the subsequent judgments
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// year
		// 年
		date_years = (DateView) view.findViewById(R.id.labeldatetime_1);
		// Set the display data for "year"
		// 设置"年"的显示数据
		date_years.setAdapter(new DateTimeAdapter(START_YEAR, END_YEAR));
		// Can be scrolled
		// 可循环滚动
		date_years.setCyclic(true);
		// Add text
		// 添加文字
		date_years.setLabel("");
		// The data displayed when initializing
		// 初始化时显示的数据
		date_years.setCurrentItem(year - START_YEAR);

		// month
		// 月
		date_months = (DateView) view.findViewById(R.id.labeldatetime_2);
		date_months.setAdapter(new DateTimeAdapter(1, 12));
		date_months.setCyclic(true);
		date_months.setLabel("");
		date_months.setCurrentItem(month);

		// day
		// 日
		date_days = (DateView) view.findViewById(R.id.labeldatetime_3);
		date_days.setCyclic(true);
		// Determine the size of the month and whether the leap year, used to determine the data of the "day"
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			date_days.setAdapter(new DateTimeAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			date_days.setAdapter(new DateTimeAdapter(1, 30));
		} else {
			// leap year
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				date_days.setAdapter(new DateTimeAdapter(1, 29));
			else
				date_days.setAdapter(new DateTimeAdapter(1, 28));
		}
		date_days.setLabel("");
		date_days.setCurrentItem(day - 1);

		// Add listen to "year"
		// 添加"年"监听
		OnDateChangedListener listener_year = new OnDateChangedListener() {
			public void onChanged(DateView view, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// Determine the size of the month and whether the leap year, used to determine the data of the "day"
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(date_months.getCurrentItem() + 1))) {
					date_days.setAdapter(new DateTimeAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(date_months.getCurrentItem() + 1))) {
					date_days.setAdapter(new DateTimeAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						date_days.setAdapter(new DateTimeAdapter(1, 29));
					else
						date_days.setAdapter(new DateTimeAdapter(1, 28));
				}
			}
		};

		// Add listen to "month"
		// 添加"月"监听
		OnDateChangedListener listener_month = new OnDateChangedListener() {
			public void onChanged(DateView view, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// Determine the size of the month and whether the leap year, used to determine the data of the "day"
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					date_days.setAdapter(new DateTimeAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					date_days.setAdapter(new DateTimeAdapter(1, 30));
				} else {
					if (((date_years.getCurrentItem() + START_YEAR) % 4 == 0 && (date_years
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (date_years.getCurrentItem() + START_YEAR) % 400 == 0)
						date_days.setAdapter(new DateTimeAdapter(1, 29));
					else
						date_days.setAdapter(new DateTimeAdapter(1, 28));
				}
			}
		};
		date_years.addChangingListener(listener_year);
		date_months.addChangingListener(listener_month);

		// hour
		// 时
		date_hours = (DateView) view.findViewById(R.id.labeldatetime_4);
		date_hours.setVisibility(View.VISIBLE);
		date_hours.setAdapter(new DateTimeAdapter(0, 23));
		date_hours.setCyclic(true);
		date_hours.setLabel("");
		date_hours.setCurrentItem(hour);

		// minute
		// 分
		date_mins = (DateView) view.findViewById(R.id.labeldatetime_5);
		date_mins.setVisibility(View.VISIBLE);
		date_mins.setAdapter(new DateTimeAdapter(0, 59));
		date_mins.setCyclic(true);
		date_mins.setLabel("");
		date_mins.setCurrentItem(minute);

		int textSize = (StaticObject.deviceHeight / 100) * 3;
		date_years.TEXT_SIZE = textSize;
		date_months.TEXT_SIZE = textSize;
		date_days.TEXT_SIZE = textSize;
		date_hours.TEXT_SIZE = textSize;
		date_mins.TEXT_SIZE = textSize;
	}

	/**
	 * 返回int类型包含年月日的数组
	 * @return
	 * int类型包含年月日的数组
	 * */
	public int[] getDate() {
		int[] date = new int[3];
		date[0] = date_year.getCurrentItem() + START_YEAR;
		date[1] = date_month.getCurrentItem();
		date[2] = date_day.getCurrentItem() + 1;
		return date;
	}

	/**
	 * 返回int类型包含时分秒的数组
	 * @return
	 * int类型包含时分秒的数组
	 * */
	public int[] getTime() {
		int[] time = new int[3];
		time[0] = date_hour.getCurrentItem();
		time[1] = date_minute.getCurrentItem();
		time[2] = date_second.getCurrentItem();
		return time;
	}

	/**
	 * 返回int类型包含年月日时分的数组
	 * @return
	 * int类型包含年月日时分的数组
	 * */
	public int[] getDateTime() {
		int[] datetime = new int[5];
		datetime[0] = date_years.getCurrentItem() + START_YEAR;
		datetime[1] = date_months.getCurrentItem();
		datetime[2] = date_days.getCurrentItem() + 1;
		datetime[3] = date_hours.getCurrentItem();
		datetime[4] = date_mins.getCurrentItem();
		return datetime;
	}

	public View getView() {
		return view;
	}
}