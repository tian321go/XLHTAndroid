package com.axeac.android.sdk.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.CommonUtil;

/**
 * 该控件为基础类，不再DFS脚本中使用，为Label组件通用属性，该组件由两个到多个组件组成，包含一个Label组件，
 * <br>默认显示在左侧，默认长度25%。当设置label时，组件Label才起作用。不设置label不起作用
 *
 * @author axeac
 * @version 1.0.0
 * */
public abstract class LabelComponent extends Component {

	protected LinearLayout layoutView;
	protected TextView labelView;
	protected View view;
	protected LinearLayout linearLayout;
	protected View lineView;
	private boolean wrap = false;

	public LabelComponent(Activity ctx) {
		super(ctx);
		labelView = new TextView(ctx);
		layoutView = new LinearLayout(ctx);
		linearLayout = new LinearLayout(ctx);
		lineView = new View(ctx);
	}

	/**  当设置Label属性时，才创建Label并限制 */
	protected String label;
	/** Lable组件宽度，默认值为25% */
	protected String labelWidth = "25%";
	/** 组件是否只读，默认值为false*/
	protected boolean readOnly = false;
	/** Label左侧图标*/
	protected String icon;
	/** Label字体*/
	protected String labelFont = "size:15px;family:宋体;style=bold,italic;color:000000000;";;
	/** Label文本对齐格式，默认为left*/
	protected String labelTextAlign = "center";
	/** Label背景颜色，默认为255255255*/
	protected String labelBgColor = "255255255";

	public void setWrap(String wrap){
		this.wrap = Boolean.parseBoolean(wrap);
	}
	/**
	 * 设置Label
	 * @param label
	 * */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * 获取Label
	 * @return
	 * 返回Label
	 * */
	public String getLabel(){
		return this.label;
	}

	/**
	 * 设置Label宽度
	 * @param labelWidth
	 * Label宽度
	 * <br>默认值：25%
	 * */
	public void setLabelWidth(String labelWidth) {
		this.labelWidth = labelWidth;
	}

	/**
	 * 设置Label是否只读
	 * @param readOnly
	 * 默认值：true
	 * */
	public void setReadOnly(String readOnly) {
		this.readOnly = Boolean.parseBoolean(readOnly);
	}

	/**
	 * 设置Label左侧图标
	 * @param icon
	 * */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 设置Label字体
	 * @param labelFont
	 * */
	public void setLabelFont(String labelFont) {
		this.labelFont = labelFont;
	}

	/**
	 * 设置Label文本对齐格式
	 * @param labelTextAlign
	 * Label文本对齐格式，可选值：left\right\center
	 * */
	public void setLabelTextAlign(String labelTextAlign) {
		this.labelTextAlign = labelTextAlign;
	}

	/**
	 * 设置Label背景色
	 * @param labelBgColor
	 * 背景色，默认值为255255255
	 * */
	public void setLabelBgColor(String labelBgColor) {
		this.labelBgColor = labelBgColor;
	}

	/**
	 *  创建LinearLayout布局
	 * */
	protected void buildLayoutView() {
		if (wrap){
			layoutView.setOrientation(LinearLayout.VERTICAL);
		}else {
			layoutView.setOrientation(LinearLayout.HORIZONTAL);
		}
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		layoutView.setGravity(Gravity.CENTER_VERTICAL);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		if (this.width == "-1" && this.height == -1) {
			layoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		} else {
			if (this.width == "-1") {
				layoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, this.height, 1));
				linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, this.height, 1));
			} else if (this.height == -1) {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					layoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					layoutView.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), LinearLayout.LayoutParams.WRAP_CONTENT));
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			} else {
				if (this.width.endsWith("%")) {
					int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
					layoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
				} else {
					layoutView.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), this.height));
					linearLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), this.height));
				}
			}
		}
		int r = Integer.parseInt(bgColor.substring(0, 3));
		int g = Integer.parseInt(bgColor.substring(3, 6));
		int b = Integer.parseInt(bgColor.substring(6, 9));
		layoutView.setBackgroundColor(Color.rgb(r, g, b));
		layoutView.setAlpha(0.8f);
		linearLayout.setBackgroundColor(Color.rgb(r, g, b));
		linearLayout.setAlpha(0.8f);
		linearLayout.setPadding(0,10,0,0);
		if (this.bgImage != null && !"".equals(bgImage)) {
			BitmapDrawable draw;
			try {
				draw = new BitmapDrawable(this.ctx.getResources(),BitmapFactory.decodeStream(this.ctx.getResources().getAssets().open(bgImage + ".png")));
				layoutView.setBackgroundDrawable(draw);
			} catch (IOException e) {
			}
		}
		layoutView.getBackground().setAlpha((int)(255 * ((float)this.alpha/100)));
		linearLayout.getBackground().setAlpha((int)(255 * ((float)this.alpha/100)));
//		layoutView.setPadding(10, 3, 10, 3);
	}

	/**
	 * 创建Label视图
	 * */
	protected void buildLabelView() {
		if("-1".equals(labelWidth)){
			labelView.setVisibility(View.GONE);
		}
//		linearLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp;
		if (labelWidth.endsWith("%")) {
			int labelWeight = 80;
			int viewWeight = 20;
			viewWeight = (int) Float.parseFloat(labelWidth.substring(0, labelWidth.indexOf("%")));
			labelWeight = 100 - viewWeight;
			lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, labelWeight);
			labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, labelWeight));
//			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, labelWeight));
//			lineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1,labelWeight));
			if (view != null)
				view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
		} else {
			lp = new LinearLayout.LayoutParams(Integer.parseInt(labelWidth), LinearLayout.LayoutParams.WRAP_CONTENT);
//			lineView.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(labelWidth),1));
//			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(labelWidth), LinearLayout.LayoutParams.WRAP_CONTENT));
			labelView.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(labelWidth), LinearLayout.LayoutParams.WRAP_CONTENT));
			if (view != null)
				view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		labelView.setText(this.label);
		int gravity = Gravity.LEFT;
		if ("left".equals(this.labelTextAlign.trim().toLowerCase())) {
			gravity = Gravity.LEFT;
		} else if ("center".equals(this.labelTextAlign.trim().toLowerCase())) {
			gravity = Gravity.CENTER;
		} else if ("right".equals(this.labelTextAlign.trim().toLowerCase())) {
			gravity = Gravity.RIGHT;
		}
		if (wrap){
			lp .setMargins((int) dipToPx(16), (int) dipToPx(8), (int) dipToPx(16), (int) dipToPx(8));
			labelView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			labelView.setLayoutParams(lp);
		}else {
			labelView.setGravity(gravity | Gravity.CENTER_VERTICAL);
		}
		String familyName = null;
		int style = Typeface.NORMAL;
		if (this.labelFont != null && !"".equals(this.labelFont)) {
			if (this.labelFont.indexOf(";") != -1) {
				String[] strs = this.labelFont.split(";");
				for (String str : strs) {
					if (str.startsWith("size")) {
						int index = str.indexOf(":");
						if (index == -1)
							continue;
						String s = str.substring(index + 1).trim();
						labelView.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
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
							labelView.setTextColor(Color.rgb(r, g, b));
						}
					}
				}
			}
		} else {
			if (this.font != null && !"".equals(this.font)) {
				if (this.font.indexOf(";") != -1) {
					String[] strs = this.font.split(";");
					for (String str : strs) {
						if (str.startsWith("size")) {
							int index = str.indexOf(":");
							if (index == -1)
								continue;
							String s = str.substring(index + 1).trim();
							labelView.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
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
								labelView.setTextColor(Color.rgb(r, g, b));
							}
						}
					}
				}
			}
		} 
		if (familyName == null || "".equals(familyName)) {
			labelView.setTypeface(Typeface.defaultFromStyle(style));
		} else {
			labelView.setTypeface(Typeface.create(familyName, style));
		}
		if (icon != null && !"".equals(icon)) {
			Drawable draw;
			try {
				draw = new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/" + icon + ".png")));
				draw.setBounds(0, 0, 32, 32);
				labelView.setCompoundDrawables(draw, null, null, null);
			} catch (IOException e) {
				try {
					draw = new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeStream(ctx.getResources().getAssets().open("opicon/qita.png")));
					draw.setBounds(0, 0, 32, 32);
					labelView.setCompoundDrawables(draw, null, null, null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (this.alpha != 0) {
			int r = Integer.parseInt(labelBgColor.substring(0, 3));
			int g = Integer.parseInt(labelBgColor.substring(3, 6));
			int b = Integer.parseInt(labelBgColor.substring(6, 9));
			labelView.setBackgroundColor(Color.rgb(r, g, b));
		}

//		linearLayout.removeAllViews();
//		linearLayout.addView(labelView);
//		linearLayout.addView(lineView);
	}

	protected void buildLineView(){
		lineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
		lineView.setBackgroundColor(Color.rgb(204,204,204));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
		lp.setMargins(0,10,0,0);
		lineView.setLayoutParams(lp);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public View getView() {
		if (!this.buildable) {
			return null;
		}
		this.buildLayoutView();
		this.buildLabelView();
		this.buildLineView();
		layoutView.removeAllViews();
		layoutView.addView(labelView);
		if (view != null) {
			layoutView.addView(view);
		}
		linearLayout.removeAllViews();
		linearLayout.addView(layoutView);
		linearLayout.addView(lineView);
		return linearLayout;
	}
	private float dipToPx(int dipValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue,
				ctx.getResources().getDisplayMetrics());
	}
}