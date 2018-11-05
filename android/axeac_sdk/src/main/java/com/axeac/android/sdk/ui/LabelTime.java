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
 * describe：Time selection
 * 时间选择
 * @author axeac
 * @version 1.0.0
 */
public class LabelTime extends LabelComponent {

	private TextView textField;
	private ImageButton imageButton;
	private DateTimeUtils dateTime;

    /**
	 * 设置和获取时分秒，格式为HH:mm:ss
	 * */
	private String text;
	/**
	 * 设置时间显示格式，true为24小时，false为12小时
	 * */
	private boolean hour24 = true;
	/**
	 * 按多少倍率显示，例如为5，则显示0/5/10/15...
	 * <br>默认值为5
	 * */
	private int minuteStep = 5;
	/**
	 * 按多少倍率显示，例如为5，则显示0/5/10/15...
	 * <br>默认值为5
	 * */
	private int secondStep = 5;

	/**
	 * 设置时间格式
	 * <br>默认格式 HH:mm:ss
	 * */
	private String format = "HH:mm:ss";
	/**
	 * Calendar对象
	 * */
	private Calendar mCalendar;
	/**
	 * 时
	 * */
	private int hour;
	/**
	 * 分
	 * */
	private int minute;
	/**
	 * 秒
	 * */
	private int second;
	
	public LabelTime(Activity ctx) {
		super(ctx);
		this.returnable = true;
		mCalendar = Calendar.getInstance();
		hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		minute = mCalendar.get(Calendar.MINUTE);
		second = mCalendar.get(Calendar.SECOND);
		RelativeLayout valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_click, null);
		textField = (TextView) valLayout.findViewById(R.id.label_click_text);
		imageButton = (ImageButton) valLayout.findViewById(R.id.label_click_btn);
		this.view = valLayout;
	}

	/**
	 * 设置显示文本时分秒
	 * @param text
	 * 显示文本时分秒
	 * */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 设置时间显示格式
	 * @param hour24
	 * true为24小时格式，false为12小时格式
	 * */
	public void setHour24(String hour24) {
		this.hour24 = Boolean.parseBoolean(hour24);
	}

	/**
	 * 设置显示倍率
	 * @param minuteStep
	 * 显示倍率
	 * */
	public void setMinuteStep(String minuteStep) {
		this.minuteStep = Integer.parseInt(minuteStep);
	}

	/**
	 * 设置显示倍率
	 * @param secondStep
	 * 显示倍率
	 * */
	public void setSecondStep(String secondStep) {
		this.secondStep = Integer.parseInt(secondStep);
	}

	/**
	 * 本类监听事件
	 * */
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				dateTime = new DateTimeUtils(ctx);
				dateTime.initTimePicker(minuteStep, secondStep);
				CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
				builder.setTitle(R.string.axeac_msg_choice);
				builder.setContentView(dateTime.getView());
				builder.setPositiveButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hour = dateTime.getTime()[0];
						minute = dateTime.getTime()[1];
						second = dateTime.getTime()[2];
						updateTime();
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
	private void updateTime() {
		if (hour24) {
			textField.setText(format.replaceAll("HH", format(hour)).replaceAll("mm", format(minute)).replaceAll("ss", format(second)));
		} else {
			if (hour >= 12) {
				textField.setText(format.replaceAll("HH", "PM " + format(hour - 12)).replaceAll("mm", format(minute)).replaceAll("ss", format(second)));
			} else {
				textField.setText(format.replaceAll("HH", "AM " + format(hour)).replaceAll("mm", format(minute)).replaceAll("ss", format(second)));
			}
		}
	}

	/**
	 * 格式化时间，位数不够时在前补0
	 * @param x
	 * 时间
	 * */
	private String format(int x) {
		String s = "" + x;
		if(s.length() ==1) s = "0" + s;
		return s;
	}

	/**
	 * 格式化时间
	 * */
	private void parseTime() {
		try {
			if (text == null || text.equals("")) {
				updateTime();
			} else {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
				try {
					Date date = simpleDateFormat.parse(text);
					mCalendar.setTime(date);
					hour = mCalendar.get(Calendar.HOUR_OF_DAY);
					minute = mCalendar.get(Calendar.MINUTE);
					second = mCalendar.get(Calendar.SECOND);
					updateTime();
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
		parseTime();
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