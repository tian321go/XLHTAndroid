package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.chart.GaugeChart;

import java.util.ArrayList;

/**
 * Created by hp on 2018/9/13.
 */

public class Gauge extends Component{
    /**
     * ColumnChart对象
     * */
    private GaugeChart gChart;

    private String color_outCicle = "222222222";

    /**
     * 标题文本
     * <br>默认值为空
     * */
    private String title = "";
    /**
     * 标题字体
     * <br>默认值为font-size:32px;color:051051051;style:bold
     * */
    private String titleFont = "font-size:32px;color:051051051;style:bold";
    /**
     * 副标题字体
     * <br>默认值为空
     * */
    private String subTitle = "";
    /**
     * 副标题字体
     * <br>默认值为font-size:25px;color:051051051;style:bold
     * */
    private String subTitleFont = "font-size:25px;color:051051051;style:bold";
    /**
     * 数据标题字体
     * <br>默认值为font-size:25px;color:051051051;style:bold
     * */
    private String dataTitleFont = "font-size:25px;color:135206235;style:bold";

    //数据字体
    private String dataFont = "font-size:55px;color:135206235;style:bold";

    /**
     * 仪表盘样式  基础仪表盘Base、横向试管形HorizontalTube、竖向试管形 VerticalTube、百分比环形Donut
     * */
    private String type = "Base";

    //刻度颜色
    private String scaleColor = "088173228";

    //进度条颜色
    private String progressColor = "135206235";

    //中心圆颜色
    private String centerCircleColor = "088173228";

    //当前进度
    private int progress = 0;

    //当前进度标题
    private String progressTitle = "";

    public Gauge(Activity ctx) {
        super(ctx);
        this.returnable = false;
    }

    /**
     * 设置标题
     * @param title
     * 标题文本
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置标题字体
     * @param titleFont
     * 标题字体
     * */
    public void setTitleFont(String titleFont) {
        this.titleFont = titleFont;
    }

    /**
     * 设置副标题
     * @param subTitle
     * 副标题文本
     * */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * 设置副标题字体
     * @param subTitleFont
     * 副标题字体
     * */
    public void setSubTitleFont(String subTitleFont) {
        this.subTitleFont = subTitleFont;
    }

    /**
     * 设置数据标题字体
     * @param dataTitleFont
     * 数据字体
     * */
    public void setDataTitleFont(String dataTitleFont) {
        this.dataTitleFont = dataTitleFont;
    }

    /**
     * 设置数据字体
     * */
    public void setDataFont(String dataFont) {
        this.dataFont = dataFont;
    }

    public void setType(String type){
        this.type = type;
    }

    public  void setScaleColor(String scaleColor){
        this.scaleColor = scaleColor;
    }
    public void setProgressColor(String progressColor){
        this.progressColor = progressColor;
    }

    public void setCenterCircleColor(String centerCircleColor){
        this.centerCircleColor = centerCircleColor;
    }

    public void setData(String data){
        String[] datas = StringUtil.split(data, "||");
        if(datas.length>=2){
            this.progressTitle = datas[0];
            if (datas[1].endsWith("%")){
                if (Integer.parseInt(datas[1].substring(0,datas[1].indexOf("%")))>100){
                    this.progress = 100;
                }else if(Integer.parseInt(datas[1].substring(0,datas[1].indexOf("%")))<0){
                    this.progress = 0;
                }else {
                    this.progress = Integer.parseInt(datas[1].substring(0,datas[1].indexOf("%")));
                }
            }else{
                if (Integer.parseInt(datas[1])>100){
                    this.progress = 100;
                }else if(Integer.parseInt(datas[1])<0){
                    this.progress = 0;
                }else {
                    this.progress = Integer.parseInt(datas[1]);
                }
            }
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        gChart = new GaugeChart(ctx);
        ArrayList<Integer> color = new ArrayList<Integer>();

        gChart.setTitle(title);
        gChart.setTitleFont(titleFont);
        gChart.setSubTitle(subTitle);
        gChart.setSubTitleFont(subTitleFont);
        gChart.setDataTitleFont(dataTitleFont);
        gChart.setDataFont(dataFont);
        gChart.setScaleColor(obtainColor(scaleColor));
        gChart.setProgressColor(obtainColor(progressColor));
        gChart.setCenterCircleColor(obtainColor(centerCircleColor));
        gChart.setProgress(progress);
        gChart.setProgressTitle(progressTitle);
        gChart.setColor_outcircle(obtainColor(color_outCicle));
        gChart.setType(type);
    }

    /**
     * 转换颜色（颜色值转换为颜色）
     * @param color
     * 转换的颜色值
     * */
    private int obtainColor(String color) {
        int r = Integer.parseInt(color.substring(0, 3));
        int g = Integer.parseInt(color.substring(3, 6));
        int b = Integer.parseInt(color.substring(6, 9));
        return Color.rgb(r, g, b);
    }

    @Override
    public View getView() {
        return gChart.getView();
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
