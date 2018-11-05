package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.chart.PieChart;
import com.axeac.android.sdk.utils.CommonUtil;

/**
 * describe:Pie chart
 * 饼图
 * @author axeac
 * @version 1.0.0
 */
public class Pie extends Component {

	/**
	 * PieChart对象
	 * */
	private PieChart mChart;

	/**
	 * 不同类型饼图标志
	 * <br>可选值：1  2  1代表常规饼图，2代表空心饼图
	 * */
	private int type = 1;
	/**
	 * 标题文本
	 * <br>默认值为空
	 * */
	private String title = "";
	/**
	 * 标题文字尺寸
	 * <br>默认值为font-size:32px;color:051051051;style:bold
	 * */
	private String titleFont = "font-size:32px;color:051051051;style:bold";
	/**
	 * 子标题文本
	 * <br>默认值为空
	 * */
	private String subTitle = "";
	/**
	 * 子标题文本尺寸
	 * <br>默认值为font-size:25px;color:051051051;style:bold
	 * */
	private String subTitleFont = "font-size:25px;color:051051051;style:bold";
	/**
	 * 数据文字尺寸
	 * <br>默认值为font-size:25px;color:051051051;style:bold
	 * */
	private String dataTitleFont = "font-size:25px;color:051051051;style:bold";
	/**
	 * 中间标题文本
	 * <br>默认值为空
	 * */
	private String titleCenter = "";
	/**
	 * 中间标题文本尺寸
	 * <br>默认值为font-size:21px;color:051051051;style:bold
	 * */
	private String titleCenterFont = "font-size:21px;color:051051051;style:bold";
	/**
	 * 百分比文字尺寸
	 * <br>默认值为font-size:21px;color:051051051;style:bold
	 * */
	private String perFont = "font-size:21px;color:051051051;style:bold";
	/**
	 * 顶部标题文本
	 * <br>默认值为空
	 * */
	private String titleTop = "";
	/**
	 * 顶部标题文字尺寸
	 * <br>默认值为font-size:32px;color:051051051;style:bold
	 * */
	private String titleTopFont = "font-size:32px;color:051051051;style:bold";
	/**
	 * 顶部副标题文本
	 * <br>默认值为空
	 * */
	private String titleTopSub = "";
	/**
	 * 顶部副标题文字尺寸
	 * <br>默认值为font-size:25px;color:051051051;style:bold
	 * */
	private String titleTopSubFont = "font-size:25px;color:051051051;style:bold";
	/**
	 * 数据1颜色值
	 * */
	private String color1 = "181061000";
	/**
	 * 数据2颜色值
	 * */
	private String color2 = "001211120";
	/**
	 * 数据3颜色值
	 * */
	private String color3 = "000155237";
	/**
	 * 数据4颜色值
	 * */
	private String color4 = "241164074";
	/**
	 * 数据5颜色值
	 * */
	private String color5 = "070130180";
	/**
	 * 数据6颜色值
	 * */
	private String color6 = "192255062";
	/**
	 * 数据7颜色值
	 * */
	private String color7 = "139101008";
	/**
	 * 数据8颜色值
	 * */
	private String color8 = "144238144";
	/**
	 * 存储图形数据的Map集合
	 * */
	private LinkedHashMap<String, String[]> datas = new LinkedHashMap<String, String[]>();
	/**
	 * 存储颜色值的list集合
	 * */
	private ArrayList<Integer> colors = new ArrayList<Integer>();
	/**
	 * 存储点击显示数据的list集合
	 * */
	private ArrayList<String> itemClicks = new ArrayList<String>();
	/**
	 * 点击时显示的文字
	 * */
	private String click = "";

	/**
	 * 是否为百分比数据标志
	 * <br>默认值为false
	 * */
	private boolean isPerData = false;

	public Pie(Activity ctx) {
		super(ctx);
		this.returnable = false;
	}

	/**
	 * 设置饼图类型
	 * @param type
	 * 可选值：1、2(1代表常规饼图，2代表空心饼图)
	 * */
	public void setType(int type){
		this.type = type;
	}

	/**
	 * 设置标题文本
	 * @param title
	 * 标题文本
	 * */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 设置标题文字尺寸
	 * @param titleFont
	 * 标题文字尺寸
	 * */
	public void setTitleFont(String titleFont) {
		this.titleFont = titleFont;
	}

	/**
	 * 设置子标题文本
	 * @param subTitle
	 * 子标题文本
	 * */
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	/**
	 * 设置子标题文字尺寸
	 * @param subTitleFont
	 * 子标题文字尺寸
	 * */
	public void setSubTitleFont(String subTitleFont) {
		this.subTitleFont = subTitleFont;
	}

	/**
	 * 设置数据文字尺寸
	 * @param dataTitleFont
	 * 数据文字尺寸
	 * */
	public void setDataTitleFont(String dataTitleFont) {
		this.dataTitleFont = dataTitleFont;
	}

	/**
	 * 设置中间标题文本
	 * @param titleCenter
	 * 中间标题文本
	 * */
	public void setTitleCenter(String titleCenter) {
		this.titleCenter = titleCenter;
	}

	/**
	 * 设置中间标题文字尺寸
	 * @param titleCenterFont
	 * 中间标题文字尺寸
	 * */
	public void setTitleCenterFont(String titleCenterFont) {
		this.titleCenterFont = titleCenterFont;
	}

	/**
	 * 设置百分比文字尺寸
	 * @param perFont
	 * 百分比文字尺寸
	 * */
	public void setPerFont(String perFont) {
		this.perFont = perFont;
	}

	/**
	 * 设置顶部标题文本
	 * @param titleTop
	 * 顶部标题文本
	 * */
	public void setTitleTop(String titleTop) {
		this.titleTop = titleTop;
	}

	/**
	 * 设置顶部标题文字尺寸
	 * @param titleTopFont
	 * 顶部标题文字尺寸
	 * */
	public void setTitleTopFont(String titleTopFont) {
		this.titleTopFont = titleTopFont;
	}

	/**
	 * 设置顶部副标题文本
	 * @param titleTopSub
	 * 顶部副标题文本
	 * */
	public void setTitleTopSub(String titleTopSub) {
		this.titleTopSub = titleTopSub;
	}

	/**
	 * 设置顶部副标题文字尺寸
	 * @param titleTopSubFont
	 * 顶部副标题文字尺寸
	 * */
	public void setTitleTopSubFont(String titleTopSubFont) {
		this.titleTopSubFont = titleTopSubFont;
	}

	/**
	 * 设置数据1颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor1(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color1 = color;
		}
	}

	/**
	 * 设置数据2颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor2(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color2 = color;
		}
	}

	/**
	 * 设置数据3颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor3(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color3 = color;
		}
	}

	/**
	 * 设置数据4颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor4(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color4 = color;
		}
	}

	/**
	 * 设置数据5颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor5(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color5 = color;
		}
	}

	/**
	 * 设置数据6颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor6(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color6 = color;
		}
	}

	/**
	 * 设置数据7颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor7(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color7 = color;
		}
	}

	/**
	 * 设置数据8颜色值
	 * @param color
	 * 颜色值
	 * */
	public void setColor8(String color) {
		if (CommonUtil.validRGBColor(color)) {
			this.color8 = color;
		}
	}

	/**
	 * 添加显示数据
	 * @param data
	 * 数据
	 * */
	public void addData(String data) {
		String[] items = StringUtil.split(data, "||");
		if (items.length >= 4) {
			datas.put(items[1], items);
		}
	}

	/**
	 * 向集合中添加点击显示的文本
	 * @param itemClick
	 * 点击显示的文本
	 * */
	public void addItemClick(String itemClick) {
		String[] args = StringUtil.split(itemClick, "||");
		if (args.length >= 4) {
			itemClicks.add(itemClick);
		}
	}

	/**
	 * 设置点击时显示的文本
	 * @param click
	 * 点击时显示的文本
	 * */
	public void setClick(String click) {
		this.click = click;
	}

	/**
	 * 执行方法
	 * */
	@Override
	public void execute() {
		mChart = new PieChart(ctx);
		if (datas.size() > 0) {
			LinkedHashMap<String, String[]> tempDatas1 = new LinkedHashMap<String, String[]>();
			LinkedHashMap<String, String[]> tempDatas2 = new LinkedHashMap<String, String[]>();
			String[] keys = datas.keySet().toArray(new String[0]);
			String val = datas.get(keys[0])[2].trim();
			if ("%".equals(val.substring(val.length() - 1, val.length()))) {
				isPerData = true;
			}
			for (int i = 0; i < datas.size(); i++) {
				String v = datas.get(keys[i])[2].trim();
				if (isPerData && CommonUtil.isFloat(v.substring(0, v.length() - 1)) && "%".equals(v.substring(v.length() - 1, v.length()))) {
					datas.get(keys[i])[2] = v.substring(0, v.length() - 1);
					tempDatas1.put(keys[i], datas.get(keys[i]));
				}
				if (!isPerData && CommonUtil.isFloat(v)) {
					tempDatas2.put(keys[i], datas.get(keys[i]));
				}
			}
			if (isPerData) {
				datas.clear();
				datas = tempDatas1;
			} else {
				datas.clear();
				datas = tempDatas2;
			}
		}
		ArrayList<Integer> color = new ArrayList<Integer>();
		color.add(obtainColor(color1));
		color.add(obtainColor(color2));
		color.add(obtainColor(color3));
		color.add(obtainColor(color4));
		color.add(obtainColor(color5));
		color.add(obtainColor(color6));
		color.add(obtainColor(color7));
		color.add(obtainColor(color8));
		int m = datas.size() / color.size();
		int n = datas.size() % color.size();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < color.size(); j++) {
				colors.add(color.get(j));
			}
		}
		for (int i = 0; i < n; i++) {
			colors.add(color.get(i));
		}
		mChart.setType(type);
		mChart.setTitle(title);
		mChart.setTitleFont(titleFont);
		mChart.setSubTitle(subTitle);
		mChart.setSubTitleFont(subTitleFont);
		mChart.setDataTitleFont(dataTitleFont);
		mChart.setTitleCenter(titleCenter);
		mChart.setTitleCenterFont(titleCenterFont);
		mChart.setPerFont(perFont);
		mChart.setTitleTop(titleTop);
		mChart.setTitleTopFont(titleTopFont);
		mChart.setTitleTopSub(titleTopSub);
		mChart.setTitleTopSubFont(titleTopSubFont);
		mChart.setDatas(datas);
		mChart.setColor(colors);
		mChart.setItemClick(itemClicks);
		mChart.setClick(click);
		mChart.setIsPerData(isPerData);
	}

	/**
	 * 根据颜色值返回颜色
	 * @param color
	 * 颜色值
	 * @return
	 * 根据颜色值生成的颜色
	 * */
	private int obtainColor(String color) {
		int r = Integer.parseInt(color.substring(0, 3));
		int g = Integer.parseInt(color.substring(3, 6));
		int b = Integer.parseInt(color.substring(6, 9));
		return Color.rgb(r, g, b);
	}

	/**
	 * 返回当前视图
	 * */
	@Override
	public View getView() {
		return mChart.getView();
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public void repaint() {

	}

	@Override
	public void starting() {

	}

	@Override
	public void end() {

	}
}