package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.ui.datetime.DateTimeUtils;
import com.axeac.android.sdk.utils.CommonUtil;

/**
 * describe:Date
 * 日期
 * @author axeac
 * @version 1.0.0
 */
public class LabelDate extends LabelComponent {
	
	private TextView textField;
	private ImageButton imageButton;
	/**
	 * DateTimeUtils对象
	 * */
	private DateTimeUtils dateTime;
	/**
	 * 设置和获取时分秒，格式为HH:mm:ss
	 * */
	private String text;
	/**
	 * 是否显示小时、分钟、秒
	 * <br>默认值为false
	 * */
	private boolean times = false;

	/**
	 * 是否显示天
	 * */
	private boolean showDay = true;

	/**
	 * 时间格式
	 * <br>默认为yyyy-MM-dd  HH:mm:ss
	 * */
	private String format;

	/**
	 * Calendar对象
	 * */
	private Calendar mCalendar;
	/**
	 * 年
	 * */
	private int year;
	/**
	 * 月
	 * */
	private int month;
	/**
	 * 日
	 * */
	private int day;
	/**
	 * 时
	 * */
	private int HH;
	/**
	 * 分
	 * */
	private int mm;
	
	public LabelDate(Activity ctx) {
		super(ctx);
		this.returnable = true;
		mCalendar = Calendar.getInstance();
		year = mCalendar.get(Calendar.YEAR);
		month = mCalendar.get(Calendar.MONTH);
		day = mCalendar.get(Calendar.DAY_OF_MONTH);
		HH = mCalendar.get(Calendar.HOUR_OF_DAY);
		mm = mCalendar.get(Calendar.MINUTE);
		RelativeLayout valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_click, null);
		textField = (TextView) valLayout.findViewById(R.id.label_click_text);
		imageButton = (ImageButton) valLayout.findViewById(R.id.label_click_btn);
		this.view = valLayout;
	}

	/**
	 * 设置日期文本格式，并根据Format解析和生成。
	 * <br>解析式格式为yyyy-MM-dd HH:mm:ss
	 * @param text
	 * 日期文本格式
	 * */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 设置是否显示小时、分钟、秒
	 * @param times
	 * 可选值true/false
	 * */
	public void setTimes(String times) {
		this.times = Boolean.parseBoolean(times);
	}

	public void setShowDay(String showDay){
		this.showDay = Boolean.parseBoolean(showDay);
	}

	/**
	 * 设置日期格式，默认为yyyy-MM-dd HH:mm:ss
	 * @param format
	 * 日期格式
	 * */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 本类监听事件
	 * */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				dateTime = new DateTimeUtils(ctx);
				if (times) {
					dateTime.initDateTimePicker();
				} else {
					if (showDay) {
						dateTime.initDatePicker(year, month, day);
					}else {
						dateTime.initDatePickerNotDay(year,month);
					}
				}
				CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
				builder.setTitle(R.string.axeac_msg_choice);
				builder.setContentView(dateTime.getView());
				builder.setPositiveButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (times) {
							year = dateTime.getDateTime()[0];
							month = dateTime.getDateTime()[1];
							day = dateTime.getDateTime()[2];
							HH = dateTime.getDateTime()[3];
							mm = dateTime.getDateTime()[4];
						} else {
							if(showDay) {
								year = dateTime.getDate()[0];
								month = dateTime.getDate()[1];
								day = dateTime.getDate()[2];
							}else{
								year = dateTime.getDate()[0];
								month = dateTime.getDate()[1];
							}
						}
						updateDate();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} catch (Throwable e) {
				String clsName = this.getClass().getName();
				clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
				String info = ctx.getString(R.string.axeac_toast_exp_click);
				Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 刷新显示时间
	 * */
	private void updateDate() {
		if (times) {
			textField.setText(format.replaceAll("yyyy", format(year)).replaceAll("MM", format(month + 1)).replaceAll("dd", format(day)).replaceAll("HH", format(HH)).replaceAll("mm", format(mm)));
		} else {
			if (showDay) {
				textField.setText(format.replaceAll("yyyy", format(year)).replaceAll("MM", format(month + 1)).replaceAll("dd", format(day)));
			}else{
				textField.setText(format.replaceAll("yyyy", format(year)).replaceAll("MM", format(month + 1)));
			}
		}
	}

	/**
	 * 格式化时间，位数不够时在前补0
	 * @param x
	 * 日期
	 * */
	private String format(int x) {
		String s = "" + x;
		if(s.length() ==1) s = "0" + s;
		return s;
	}

	/**
	 * 格式化时间
	 * */
	private void parseDate() {
		try {
			if (text == null || text.equals("")) {
				updateDate();
				return;
			}
			if (text.startsWith("date:")) {
				String _date = text.replaceAll("date:date", "");
				if (_date.startsWith("+") && _date.length() >= 3) {
					int num = Integer.valueOf(_date.substring(1, _date.length() - 1));
					if (_date.endsWith("m")) {
						if (num + month > 12) {
							year += (num + month) / 12;
							month = (num + month) % 12;
						} else {
							month += num;
						}
					} else if (_date.endsWith("d")) {
						if (num + day > 30) {
							month += (num + day) / 30;
							day = (num + day) % 30;
						} else {
							day += num;
						}
					}
				} else if (_date.startsWith("-") && _date.length() >= 3) {
					int num = Integer.valueOf(_date.substring(1, _date.length() - 1));
					if (_date.endsWith("m")) {
						if (num >= month) {
							year -= (num - month) / 12 + 1;
							month = 12 - (num - month) % 12;
						} else {
							month -= num;
						}
					} else if (_date.endsWith("d")) {
						if (num >= day) {
							month -= (num - day) / 30 + 1;
							day = 30 - (num - day) % 30;
						} else {
							day -= num;
						}
					}
				}
				updateDate();
			} else {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
				if (times && !text.contains(":")) {
					text += " 00:00";
				}
				try {
					Date date = simpleDateFormat.parse(text);
					mCalendar.setTime(date);
					year = mCalendar.get(Calendar.YEAR);
					month = mCalendar.get(Calendar.MONTH); 
					day = mCalendar.get(Calendar.DAY_OF_MONTH);
					HH = mCalendar.get(Calendar.HOUR_OF_DAY);
					mm = mCalendar.get(Calendar.MINUTE);
					updateDate();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			String clsName = this.getClass().getName();
			clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
			String info = ctx.getString(R.string.axeac_toast_exp_create);
			info = StringUtil.replace(info, "@@TT@@", clsName);
			Toast.makeText(ctx, info, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		if(!this.visiable) return;
		if (!readOnly) {
			textField.setOnClickListener(listener);
			imageButton.setOnClickListener(listener);
		}
		if (format == null || format.equals("")) {
			if (times) {
				format = "yyyy-MM-dd HH:mm";
			} else {
				if (showDay) {
					format = "yyyy-MM-dd";
				}else{
					format = "yyyy-MM";
				}
			}
		} else {
			if (times) {
				if (!format.contains("HH")) {
					format = "yyyy-MM-dd HH:mm";
				}
			} else {
				if (showDay) {
					if (format.contains("HH")) {
						format = "yyyy-MM-dd";
					}
				}else{
					format = "yyyy-MM";
				}
			}
		}
		parseDate();
		String familyName = null;
		int style = Typeface.NORMAL;
		if (this.font != null && !"".equals(this.font)) {
			if (this.font.indexOf(";") != -1) {
				String[] strs = this.font.split(";");
				for (String str : strs) {
					if (str.startsWith("size")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						textField.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
					} else if(str.startsWith("family")) {
						int index = str.indexOf(":");
						if(index == -1)
							continue;
						familyName = str.substring(index + 1).trim();
					} else if(str.startsWith("style")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						if ("bold".equals(s)){
							style = Typeface.BOLD;
						} else if("italic".equals(s)) {
							style = Typeface.ITALIC;
						} else {
							if (s.indexOf(",") != -1) {
								if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
									style = Typeface.BOLD_ITALIC;
								}
								if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
									style = Typeface.BOLD_ITALIC;
								}
							}
						}
					} else if(str.startsWith("color")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						if (CommonUtil.validRGBColor(s)) {
							int r = Integer.parseInt(s.substring(0, 3));
							int g = Integer.parseInt(s.substring(3, 6));
							int b = Integer.parseInt(s.substring(6, 9));
							textField.setTextColor(Color.rgb(r, g, b));
						}
					}
				}
			}
		}
		if (familyName == null || "".equals(familyName)) {
			textField.setTypeface(Typeface.defaultFromStyle(style));
		} else {
			textField.setTypeface(Typeface.create(familyName, style));
		}
	}
	
	@Override
	public View getView() {
		return super.getView();
	}
	
	@Override
	public String getValue() {
		return textField.getText().toString();
	}
	
	@Override
	public void repaint() {

	}

	@Override
	public void starting() {
		this.buildable = false;
	}

	@Override
	public void end() {
		this.buildable = true;
	}
}