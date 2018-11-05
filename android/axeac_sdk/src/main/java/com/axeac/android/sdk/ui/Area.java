package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.chart.AreaChart;
import com.axeac.android.sdk.ui.chart.LineChart;
import com.axeac.android.sdk.utils.CommonUtil;

/**
 * describe:Line chart
 * 面积图
 * @author axeac
 * @version 1.0.0
 */
public class Area extends Component {

    /**
     * LineChart对象
     * */
    private AreaChart aChart;

    /**
     * 标题文本
     * <br>默认值为空
     * */
    private String title = "";
    /**
     * 标题字体
     * <br>默认值为font-size:30px;color:255255255;style:bold
     * */
    private String titleFont = "font-size:30px;color:051051051;style:bold";
    /**
     * 副标题文本
     * <br>默认值为空
     * */
    private String subTitle = "";
    /**
     * 副标题字体
     * <br>默认值为font-size:22px;color:255255255;style:bold
     * */
    private String subTitleFont = "font-size:22px;color:051051051;style:bold";
    /**
     * 数据字体
     * <br>默认值为font-size:22px;color:255255255;style:bold
     * */
    private String dataTitleFont = "font-size:22px;color:051051051;style:bold";
    /**
     * X轴标题
     * <br>默认值为空
     * */
    private String titleX = "";
    /**
     * X轴标题字体
     * <br>默认值为font-size:18px;color:255255255;style:bold
     * */
    private String titleXFont = "font-size:18px;color:051051051;style:bold";
    /**
     * Y轴标题
     * <br>默认值为空
     * */
    private String titleY = "";
    /**
     * Y轴标题字体
     * <br>默认值为font-size:18px;color:255255255;style:bold
     * */
    private String titleYFont = "font-size:18px;color:051051051;style:bold";
    /**
     * 数据1颜色值
     * */
    private String color1 = "238140043";
    /**
     * 数据2颜色值
     * */
    private String color2 = "001140241";
    /**
     * 数据3颜色值
     * */
    private String color3 = "000239238";
    /**
     * 数据4颜色值
     * */
    private String color4 = "065105225";
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
    private LinkedHashMap<String, ArrayList<String[]>> datas = new LinkedHashMap<String, ArrayList<String[]>>();
    /**
     * 存储颜色值的list集合
     * */
    private ArrayList<Integer> colors = new ArrayList<Integer>();
    /**
     * 存储横轴标签文字的list集合
     * */
    private ArrayList<String> dataXs = new ArrayList<String>();
    /**
     * 存储点击显示数据的list集合
     * */
    private ArrayList<String> itemClicks = new ArrayList<String>();
    /**
     * 点击时显示的文字
     * */
    private String click = "";

    private String type = "Base";

    private boolean smoothed = false;

    private float areaAlpha = 1f;

    public Area(Activity ctx) {
        super(ctx);
        this.returnable = false;
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
     * 设置X轴标题文本
     * @param titleX
     * X轴标题文本
     * */
    public void setTitleX(String titleX) {
        this.titleX = titleX;
    }

    /**
     * 设置X轴标题文字尺寸
     * @param titleXFont
     * X轴标题文字尺寸
     * */
    public void setTitleXFont(String titleXFont) {
        this.titleXFont = titleXFont;
    }

    /**
     * 设置Y轴标题文本
     * @param titleY
     * Y轴标题文本
     * */
    public void setTitleY(String titleY) {
        this.titleY = titleY;
    }

    /**
     * 设置Y轴标题文字尺寸
     * @param titleYFont
     * Y轴标题文字尺寸
     * */
    public void setTitleYFont(String titleYFont) {
        this.titleYFont = titleYFont;
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
        if (items.length >= 5 && CommonUtil.isFloat(items[3])) {
            String val = items[3].trim();
            if (CommonUtil.isFloat(val)) {
                if (datas.containsKey(items[1])) {
                    datas.get(items[1]).add(items);
                } else {
                    ArrayList<String[]> list = new ArrayList<String[]>();
                    list.add(items);
                    datas.put(items[1], list);
                }
            }
        }
    }

    /**
     * 添加横轴数据
     * @param dataX
     * 横轴数据
     * */
    public void addDataX(String dataX) {
        dataXs.add(dataX);
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

    public void setType(String type){
        this.type = type;
    }

    public void setSmoothed(String smoothed){
        this.smoothed = Boolean.parseBoolean(smoothed);
    }

    public void setAreaAlpha(String areaAlpha){
        if (areaAlpha.endsWith("%")){
            this.areaAlpha = Float.parseFloat(areaAlpha.substring(0,areaAlpha.length()-1))/100;
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        aChart = new AreaChart(ctx);
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
        aChart.setTitle(title);
        aChart.setTitleFont(titleFont);
        aChart.setSubTitle(subTitle);
        aChart.setSubTitleFont(subTitleFont);
        aChart.setDataTitleFont(dataTitleFont);
        aChart.setTitleX(titleX);
        aChart.setTitleXFont(titleXFont);
        aChart.setTitleY(titleY);
        aChart.setTitleYFont(titleYFont);
        aChart.setDatas(datas);
        aChart.setColor(colors);
        aChart.setDataX(dataXs);
        aChart.setItemClick(itemClicks);
        aChart.setClick(click);
        aChart.setType(type);
        aChart.setSmoothed(smoothed);
        aChart.setAreaAlpha(areaAlpha);
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
        return aChart.getView();
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