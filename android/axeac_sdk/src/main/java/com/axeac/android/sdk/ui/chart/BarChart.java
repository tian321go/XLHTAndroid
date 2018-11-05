package com.axeac.android.sdk.ui.chart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.OptionAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 柱形图视图
 * @author axeac
 * @version 1.0.0
 * */
public class BarChart extends View {

    /**
     * 边距长度
     * */
    private static final int DEFAULT_PADDING_LENGTH = 25;
    /**
     *
     * */
    private static final int DEFAULT_DATAAXIS_NGRID = 4;
    /**
     * 柱图间间距
     * */
    private static final float DEFAULT_EMPTYPX = 30;

    private Activity ctx;
    /**
     * RectF对象
     * */
    private RectF rect;
    /**
     * 标题文本
     * */
    private String title;
    /**
     * 标题文字尺寸
     * */
    private String titleFont;
    /**
     * 子标题文本
     * */
    private String subTitle;
    /**
     * 子标题文字尺寸
     * */
    private String subTitleFont;
    /**
     * 数据文字尺寸
     * */
    private String dataTitleFont;
    /**
     * 坐标轴横轴文字
     * */
    private String titleX;
    /**
     * 坐标轴纵轴文字
     * */
    private String titleY;
    /**
     * 坐标轴横轴文字尺寸
     * */
    private String titleXFont;
    /**
     * 坐标轴纵轴文字尺寸
     * */
    private String titleYFont;
    /**
     * 存储横轴标签文字的list集合
     * */
    private ArrayList<String> dataXs;
    /**
     * 存储图形数据的Map集合
     * */
    private LinkedHashMap<String, ArrayList<String[]>> datas;

    /**
     * 存储颜色值的list集合
     * */
    private ArrayList<Integer> colors;
    /**
     * 存储点击显示数据的list集合
     * */
    private ArrayList<String> itemClicks;
    /**
     * 点击时显示的文字
     * */
    private String click;
    /**
     * 网格横线间隙
     * */
    private float xAxisGridGap;
    /**
     * 网格竖线间隙
     * */
    private float yAxisGridGap;
    /**
     * 坐标系内网格横线数量
     * */
    private int xAxisNGrid;
    /**
     * 坐标系内网格竖线数量
     * */
    private int yAxisNGrid;

    private int dataMaxValue = 0;
    private int dataPMaxValue = 0;
    private int titleXPx = 0;
    private int titleYPx = 0;

    /**
     * 存储坐标系内文字区域的Map集合
     * */
    private Map<String, RectF> rectMap = new HashMap<String, RectF>();

    private Map<String, RectF> rectPMap = new HashMap<String, RectF>();
    /**
     * 存储坐标系内文字数据的Map集合
     * */
    private Map<String, String[]> dataMap = new HashMap<String, String[]>();

    private Map<String, String[]> dataPMap = new HashMap<String, String[]>();

    private List<float[]> pointList = new ArrayList<float[]>();

    private List<Integer> pointLineColor = new ArrayList<>();

    /**
     * 是否含有百分比数据
     * */
    private boolean havePercent;

    /**
    * 条形图类型
    * */
    private String type;

    public BarChart(Activity ctx, String size) {
        super(ctx);
        this.ctx = ctx;
        int height = 0;
        Rect frame = new Rect();
        ctx.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        height = frame.top;
        height += ctx.findViewById(R.id.toolbar).getHeight();
        height += ctx.findViewById(R.id.layout_bottom).getHeight();
        float sizef;
        if(size.endsWith("%")) {
            int w = Integer.parseInt(size.substring(0,size.length()-1));
            if(w<=0){
                w = 60;
                sizef = Float.parseFloat("0." + w);
            }else if(w>=100) {
                sizef = Float.parseFloat(size.substring(0,size.length()-3) + "."
                        + size.substring(size.length()-3,size.length()-1));
            }else{
                sizef = Float.parseFloat("0." + w);
            }
        }else{
            sizef = Float.parseFloat("0." + 60);
        }
        this.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) (StaticObject.deviceHeight * sizef)));
        this.setBackgroundColor(getResources().getColor(R.color.background));
        this.getBackground().setAlpha(180);
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
     * 判断是否含有百分比数据
     * */
    public void setHavePercent(boolean havePercent){
        this.havePercent = havePercent;
    }

    public void setType(String type){
        this.type = type;
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
     * 设置横轴标签文字（即X轴坐标点文字）
     * @param dataXs
     * 横轴标签文字（即X轴坐标点文字）
     * */
    public void setDataX(ArrayList<String> dataXs) {
        this.dataXs = dataXs;
    }

    /**
     * 对datas进行赋值（图形数据集合）
     * @param datas
     * 图形数据集合
     * */
    public void setDatas(LinkedHashMap<String, ArrayList<String[]>> datas) {
        this.datas = datas;
    }

    /**
     * 对colors进行赋值（颜色值集合）
     * @param colors
     * 颜色值集合
     * */
    public void setColor(ArrayList<Integer> colors) {
        this.colors = colors;
    }

    /**
     * 对itemClicks进行赋值（点击显示数据集合）
     * @param itemClicks
     * 点击时显示的数据的集合
     * */
    public void setItemClick(ArrayList<String> itemClicks) {
        this.itemClicks = itemClicks;
    }

    /**
     * 设置点击时显示的文字
     * @param click
     * 点击时显示的文字
     * */
    public void setClick(String click) {
        this.click = click;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }

    /**
     * 绘制柱图
     * @param canvas
     * Canvas对象
     * */
    private void drawChart(Canvas canvas) {
        Rect leftRect = drawTitle(canvas);
        Rect rightRect = drawDataTitle(canvas, leftRect);
        int titleHeight = leftRect.bottom > rightRect.bottom ? leftRect.bottom : rightRect.bottom;
        rect = new RectF(0, titleHeight, this.getWidth(), this.getHeight());
        initChartDatas();
        if (!titleX.equals("")) {
            drawTitleX(canvas);
        }
        if (!titleY.equals("")) {
            drawTitleY(canvas);
        }
        drawXAxisLabel(canvas);
        drawYAxisLabel(canvas);
        drawDiagram(canvas);
        drawPointLine(canvas);
        drawXAxisLine(canvas);
        drawYAxisLine(canvas);
        if(havePercent){
            drawYAxisPLine(canvas);
            drawYAxisPLabel(canvas);
        }
    }

    /**
     * 绘制主副标题
     * @param canvas
     * Canvas对象
     * */
    private Rect drawTitle(Canvas canvas) {
        // 主标题
        Paint paint = new Paint();
        float titleTextSize = 30;
        if (titleFont != null && !"".equals(titleFont)) {
            if (titleFont.indexOf(";") != -1) {
                String[] strs = titleFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        titleTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if(str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)){
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if("italic".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
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
                            paint.setColor(Color.rgb(r, g, b));
                        } else {
                            paint.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawText(title, DEFAULT_PADDING_LENGTH, paint.getFontMetrics().bottom - paint.getFontMetrics().top, paint);
        int titleWidth = (int) paint.measureText(title) + DEFAULT_PADDING_LENGTH * 2;
        int titleHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top + titleTextSize * 0.75);
        // subtitle
        // 副标题
        paint = new Paint();
        float subTitleTextSize = 23;
        if (subTitleFont != null && !"".equals(subTitleFont)) {
            if (subTitleFont.indexOf(";") != -1) {
                String[] strs = subTitleFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1) continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        subTitleTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if(str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)){
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if("italic".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
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
                            paint.setColor(Color.rgb(r, g, b));
                        } else {
                            paint.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawText(subTitle, DEFAULT_PADDING_LENGTH, paint.getFontMetrics().bottom - paint.getFontMetrics().top + titleHeight, paint);
        int subTitleWidth = (int) paint.measureText(subTitle) + DEFAULT_PADDING_LENGTH * 2;
        int subTitleHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top + subTitleTextSize * 0.75);

        int width = titleWidth > subTitleWidth ? titleWidth : subTitleWidth;
        int height = titleHeight + subTitleHeight;
        return new Rect(0, 0, width, height);
    }

    /**
     * 绘制数据标题
     *
     * @param canvas
     * Canvas对象
     * @param rectF
     * Rect对象
     * @return
     * Rect对象
     */
    private Rect drawDataTitle(Canvas canvas, Rect rectF) {
        Paint paint = new Paint();
        float dataTitleTextSize = 23;
        if (dataTitleFont != null && !"".equals(dataTitleFont)) {
            if (dataTitleFont.indexOf(";") != -1) {
                String[] strs = dataTitleFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1) continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        dataTitleTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if(str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)){
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if("italic".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
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
                            paint.setColor(Color.rgb(r, g, b));
                        } else {
                            paint.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        Rect rect = new Rect(rectF);
        rect.set(rect.right + DEFAULT_PADDING_LENGTH, rect.top + 15, this.getWidth() - DEFAULT_PADDING_LENGTH, rect.bottom);
        if (datas.size() > 0) {
            String[] list = datas.keySet().toArray(new String[0]);
            Integer[] lengths = new Integer[list.length];
            for (int i = 0; i < list.length; i++) {
                lengths[i] = (int) paint.measureText(list[i]);
            }
            lengths = CommonUtil.sortDesc(lengths);
            int itemWidth = lengths[0];
            int itemHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top);
            int lineHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top + dataTitleTextSize * 0.75);
            int colsCount = rect.width() / (itemWidth + DEFAULT_PADDING_LENGTH * 2);
            int rowsCount = list.length / colsCount + (list.length % colsCount > 0 ? 1 : 0);
            rect.set(rect.right - (itemWidth + DEFAULT_PADDING_LENGTH * 2) * colsCount, rect.top, rect.right, rect.top + lineHeight * rowsCount);
            for (int i = 0; i < rowsCount; i++) {
                if (i < rowsCount - 1) {
                    for (int j = 0; j < colsCount; j++) {
                        canvas.drawText(list[i * colsCount + j], rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) * j + DEFAULT_PADDING_LENGTH * 2, rect.top + lineHeight * i + itemHeight, paint);
                        Paint p = new Paint();
                        p.setColor(colors.get(i * colsCount + j));
                        p.setStyle(Style.FILL_AND_STROKE);
                        p.setAntiAlias(true);
                        canvas.drawCircle(rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) * j + DEFAULT_PADDING_LENGTH, rect.top + lineHeight * i + lineHeight / 2, 13, p);
                    }
                } else {
                    int index = list.length - (rowsCount - 1) * colsCount;
                    for (int j = 0; j < index; j++) {
                        canvas.drawText(list[i * colsCount + j], rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) * (j + colsCount - index) + DEFAULT_PADDING_LENGTH * 2, rect.top + lineHeight * i + itemHeight, paint);
                        Paint p = new Paint();
                        p.setColor(colors.get(i * colsCount + j));
                        p.setStyle(Style.FILL_AND_STROKE);
                        p.setAntiAlias(true);
                        canvas.drawCircle(rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) * (j + colsCount - index) + DEFAULT_PADDING_LENGTH, rect.top + lineHeight * i + lineHeight / 2, 13, p);
                    }
                }
            }
        }
        return rect;
    }

    /**
     * 初始化操作
     * */
    private void initChartDatas() {
        obtainDataMaxValue();
        if(havePercent){
            obtainPDataMaxValue();
        }

        titleXPx = obtainPaintXYHeight(titleXFont);
        titleYPx = obtainPaintXYHeight(titleYFont);
        float originX = 0;
        float originY = 0;
        int xwidth = getMaxWidth(datas,obtainPaintXYPaint(titleYFont));
        if (titleY.equals("")) {
            originX = rect.left + titleYPx + DEFAULT_EMPTYPX * 2 + 5 + xwidth;
        } else {
            originX = rect.left + (titleYPx + DEFAULT_EMPTYPX) * 2 + 5 + xwidth;
        }
        if (titleX.equals("")) {
            originY = rect.bottom - titleXPx - DEFAULT_EMPTYPX * 2;
        } else {
            originY =  rect.bottom - (titleXPx + DEFAULT_EMPTYPX) * 2;
        }
        rect = new RectF(originX, rect.top + DEFAULT_EMPTYPX * 2, rect.right - DEFAULT_EMPTYPX * 4, originY);

        String[] ids = datas.keySet().toArray(new String[0]);
        for (String id : ids) {
            ArrayList<String[]> list = datas.get(id);
            for (int i = 0; i < list.size(); i++) {
                if (!dataXs.contains(list.get(i)[2])) {
                    dataXs.add(list.get(i)[2]);
                }
            }
        }

        xAxisNGrid = dataXs.size();
        yAxisNGrid = DEFAULT_DATAAXIS_NGRID;
        xAxisGridGap = rect.height() / xAxisNGrid;
        yAxisGridGap = rect.width() / yAxisNGrid;
    }

    public int getMaxWidth(LinkedHashMap<String, ArrayList<String[]>> datas,Paint paint){
        int max = 0;
        for(String key:datas.keySet()){
            for(int i=0;i<datas.get(key).size();i++){
                max = max>paint.measureText(datas.get(key).get(i)[2])?max: (int) paint.measureText(datas.get(key).get(i)[2]);
            }
        }
        return max;
    }

    /**
     * 设置Y轴坐标点最大数值
     * */
    private void obtainDataMaxValue() {
        float max = 0;
        String[] ids = datas.keySet().toArray(new String[0]);
        if(type.toLowerCase().equals("stacked")){
            LinkedHashMap<Integer, ArrayList<String[]>> datamap = new LinkedHashMap<>();
            for(String id : ids){
                ArrayList<String[]> list = datas.get(id);
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i)[3].endsWith("%")) {
                        if (datamap.containsKey(i)) {
                            datamap.get(i).add(list.get(i));
                        } else {
                            ArrayList<String[]> list_ = new ArrayList<>();
                            list_.add(list.get(i));
                            datamap.put(i, list_);
                        }
                    }
                }
            }
            for ( Integer integer : datamap.keySet()) {
                float count = 0;
                ArrayList<String[]> list = datamap.get(integer);
                for (int i = 0; i < list.size(); i++) {
                    count += Float.parseFloat(list.get(i)[3]);
                }
                max = max > count ? max : count;
            }
            dataMaxValue = CommonUtil.obtainMaxData(Math.round(max), DEFAULT_DATAAXIS_NGRID);
        }else {
            for (String id : ids) {
                ArrayList<String[]> list = datas.get(id);
                for (int i = 0; i < list.size(); i++) {
                    if (!list.get(i)[3].endsWith("%")) {
                        float count = Float.parseFloat(list.get(i)[3]);
                        max = max > count ? max : count;
                    }
                }
            }
            dataMaxValue = CommonUtil.obtainMaxData(Math.round(max), DEFAULT_DATAAXIS_NGRID);
        }
    }

    /**
     * 设置Y百分比副轴坐标点最大数值
     * */
    private void obtainPDataMaxValue() {
        float max = 0;
        String[] ids = datas.keySet().toArray(new String[0]);
        if(type.toLowerCase().equals("stacked")){
            LinkedHashMap<Integer, ArrayList<String[]>> datamap = new LinkedHashMap<>();
            for(String id : ids){
                ArrayList<String[]> list = datas.get(id);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i)[3].endsWith("%")) {
                        if (datamap.containsKey(i)) {
                            datamap.get(i).add(list.get(i));
                        } else {
                            ArrayList<String[]> list_ = new ArrayList<>();
                            list_.add(list.get(i));
                            datamap.put(i, list_);
                        }
                    }
                }
            }
            for ( Integer integer : datamap.keySet()) {
                float count = 0;
                ArrayList<String[]> list = datamap.get(integer);
                for (int i = 0; i < list.size(); i++) {
                    count += Float.parseFloat(list.get(i)[3].substring(0, list.get(i)[3].length() - 1));
                }
                max = max > count ? max : count;
            }
            dataPMaxValue = CommonUtil.obtainMaxData(Math.round(max), DEFAULT_DATAAXIS_NGRID);
        }else {
            for (String id : ids) {
                ArrayList<String[]> list = datas.get(id);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i)[3].endsWith("%")) {
                        float count = Float.parseFloat(list.get(i)[3].substring(0, list.get(i)[3].length() - 1));
                        max = max > count ? max : count;
                    }
                }
            }
            dataPMaxValue = CommonUtil.obtainMaxData(Math.round(max), DEFAULT_DATAAXIS_NGRID);
        }
    }

    /**
     * 返回XY轴坐标点文字的高度
     * @param font
     * 文字尺寸
     * @return
     * 文字高度
     * */
    private int obtainPaintXYHeight(String font) {
        Paint paint = obtainPaintXYPaint(font);
        return (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top);
    }

    /**
     * 返回绘制XY坐标点文字的Paint对象
     * @param font
     * 文字尺寸
     * @return
     * Paint对象
     * */
    private Paint obtainPaintXYPaint(String font) {
        Paint paint = new Paint();
        if (font != null && !"".equals(font)) {
            if (font.indexOf(";") != -1) {
                String[] strs = font.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                    } else if(str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)){
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if("italic".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
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
                            paint.setColor(Color.rgb(r, g, b));
                        } else {
                            paint.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        return paint;
    }

    /**
     * 绘制坐标轴横轴文字标题
     * @param canvas
     * Canvas对象
     * */
    private void drawTitleX(Canvas canvas) {
        Paint paint = obtainPaintXYPaint(titleYFont);
        canvas.drawText(titleY, rect.left + (rect.width() - paint.measureText(titleY)) / 2, rect.bottom + titleYPx * 2, paint);
    }

    /**
     * 绘制坐标轴竖轴文字标题
     * @param canvas
     * Canvas对象
     * */
    private void drawTitleY(Canvas canvas) {
        Paint paint = obtainPaintXYPaint(titleXFont);
        Path path = new Path();
        path.moveTo(rect.left - titleXPx * 2, rect.top + (rect.height() - paint.measureText(titleX)) / 2);
        path.lineTo(rect.left - titleXPx * 2, rect.bottom - (rect.height() - paint.measureText(titleX)) / 2);
        canvas.drawTextOnPath(titleX, path, 0, 0, paint);
    }

    /**
     * 绘制横轴标签文字
     * @param canvas
     * Canvas对象
     * */
    private void drawXAxisLabel(Canvas canvas) {
        Paint paint = obtainPaintXYPaint(titleYFont);

        String text;
        for (int i = 0; i <= yAxisNGrid; i++) {
            int singleVal = dataMaxValue / DEFAULT_DATAAXIS_NGRID;
            text = String.valueOf(singleVal * i);

            canvas.drawText(text, rect.left +  + i * yAxisGridGap, rect.bottom + titleYPx, paint);
        }
    }

    /**
     * 绘制纵轴标签文字
     * @param canvas
     * Canvas对象
     * */
    private void drawYAxisLabel(Canvas canvas) {
        Paint paint = obtainPaintXYPaint(titleXFont);
        float grid = (xAxisGridGap - DEFAULT_EMPTYPX) / datas.size();
        for (int i = 0; i < xAxisNGrid; i++) {
            canvas.drawText(dataXs.get(i),rect.left - paint.measureText(dataXs.get(i))-10,
                         rect.bottom - DEFAULT_EMPTYPX  - grid/2 +
                            paint.measureText(dataXs.get(i).substring(0,1))/2 - i*xAxisGridGap,paint);
        }
    }

    /**
     * 绘制纵副轴标签文字
     * @param canvas
     * Canvas对象
     * */
    private void drawYAxisPLabel(Canvas canvas) {
        Paint paint = obtainPaintXYPaint(titleYFont);
        String text;
        for (int i = 0; i <= yAxisNGrid; i++) {
            int singleVal = dataPMaxValue / DEFAULT_DATAAXIS_NGRID;
            text = String.valueOf(singleVal * i);
            canvas.drawText(text, rect.left +  + i * yAxisGridGap, rect.top - titleYPx, paint);
        }
    }

    /**
     * 绘制矩形柱、文字
     * @param canvas
     * Canvas对象
     * */
    private void drawDiagram(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.FILL_AND_STROKE);
        float grid = (xAxisGridGap - DEFAULT_EMPTYPX) / datas.size();
        String[] ids = datas.keySet().toArray(new String[0]);
        pointList.clear();
        if(type.toLowerCase().equals("stacked")) {
            for (int i = 0; i < dataXs.size(); i++) {
                String label = dataXs.get(i);
                float columnHeight = 0;
                float lastColumnHeight = 0;
                float columnPHeight = 0;
                for (int j = 0; j < ids.length; j++) {
                    List<String[]> list = datas.get(ids[j]);
                    for (int k = 0; k < list.size(); k++) {
                        if (label.equals(list.get(k)[2])) {
                            if (!list.get(k)[3].endsWith("%")) {
                                columnHeight += Float.parseFloat(list.get(k)[3]) / dataMaxValue * rect.width();
                                RectF areaRectF = new RectF();
                                areaRectF.set(rect.left + lastColumnHeight,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - grid,
                                        rect.left + columnHeight,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap);
                                lastColumnHeight = columnHeight;
                                paint.setColor(colors.get(j));
                                canvas.drawRect(areaRectF, paint);
                                String uuid = UUID.randomUUID().toString();
                                rectMap.put(uuid, areaRectF);
                                dataMap.put(uuid, list.get(k));
                            } else {
                                String strp = list.get(k)[3];
                                String str = strp.substring(0, strp.length() - 1);
                                paint.setColor(colors.get(j));
                                columnPHeight += Float.parseFloat(str) / dataPMaxValue * rect.width();
                                RectF areaRectF = new RectF();
                                areaRectF.set(rect.left + columnPHeight - (grid * ids.length) / 2,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - grid - (grid * ids.length) / 2,
                                        rect.left + columnPHeight + (grid * ids.length) / 2,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap + (grid * ids.length) / 2);
                                canvas.drawCircle(rect.left + columnPHeight, rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2, 8f, paint);
                                float[] point = {rect.left + columnPHeight, rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2};
                                pointList.add(point);
                                pointLineColor.add(colors.get(j));
                                String uuid = UUID.randomUUID().toString();
                                rectPMap.put(uuid, areaRectF);
                                dataPMap.put(uuid, list.get(k));
                            }
                        }
                    }

                }
            }

            paint.setTextSize(18);
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            for (int i = 0; i < dataXs.size(); i++) {
                String label = dataXs.get(i);
                float columnHeight = 0;
                for (int j = 0; j < ids.length; j++) {
                    List<String[]> list = datas.get(ids[j]);
                    for (int k = 0; k < list.size(); k++) {
                        if (label.equals(list.get(k)[2])) {
                            if (!list.get(k)[3].endsWith("%")) {
                                String str = list.get(k)[3];
                                paint.setColor(colors.get(j));
                                columnHeight += Float.parseFloat(list.get(k)[3]) / dataMaxValue * rect.width();
                                canvas.drawText(str, rect.left + columnHeight + 5,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - grid / 2 + paint.measureText(str.substring(0, 1)), paint);
                            } else {
                                String strp = list.get(k)[3];
                                String str = strp.substring(0, strp.length() - 1);
                                paint.setColor(colors.get(j));
                                canvas.drawText(strp, rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width() + 8,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2, paint);
                            }
                        }
                    }
                }
            }
        }else{
            for (int i = 0; i < dataXs.size(); i++) {
                String label = dataXs.get(i);
                for (int j = 0; j < ids.length; j++) {
                    List<String[]> list = datas.get(ids[j]);
                    for (int k = 0; k < list.size(); k++) {
                        if (label.equals(list.get(k)[2])) {
                            if (!list.get(k)[3].endsWith("%")) {
                                RectF areaRectF = new RectF();
                                areaRectF.set(rect.left,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - grid - j * grid,
                                        rect.left + Float.parseFloat(list.get(k)[3]) / dataMaxValue * rect.width(),
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - j * grid);
                                paint.setColor(colors.get(j));
                                canvas.drawRect(areaRectF, paint);
                                String uuid = UUID.randomUUID().toString();
                                rectMap.put(uuid, areaRectF);
                                dataMap.put(uuid, list.get(k));
                            } else {
                                String strp = list.get(k)[3];
                                String str = strp.substring(0, strp.length() - 1);
                                paint.setColor(colors.get(j));
                                RectF areaRectF = new RectF();
                                areaRectF.set(rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width() - (grid * ids.length) / 2,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - grid - j * grid - (grid * ids.length) / 2,
                                        rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width() + (grid * ids.length) / 2,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - j * grid + (grid * ids.length) / 2);
                                canvas.drawCircle(rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width(), rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2, 8f, paint);
                                float[] point = {rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width(), rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2};
                                pointList.add(point);
                                pointLineColor.add(colors.get(j));
                                String uuid = UUID.randomUUID().toString();
                                rectPMap.put(uuid, areaRectF);
                                dataPMap.put(uuid, list.get(k));
                            }
                        }
                    }
                }
            }
            paint.setTextSize(18);
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            for (int i = 0; i < dataXs.size(); i++) {
                String label = dataXs.get(i);
                for (int j = 0; j < ids.length; j++) {
                    List<String[]> list = datas.get(ids[j]);
                    for (int k = 0; k < list.size(); k++) {
                        if (label.equals(list.get(k)[2])) {
                            if (!list.get(k)[3].endsWith("%")) {
                                String str = list.get(k)[3];
                                paint.setColor(colors.get(j));
                                canvas.drawText(str, rect.left + Float.parseFloat(list.get(k)[3]) / dataMaxValue * rect.width() + 5,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - j * grid - grid / 2 + paint.measureText(str.substring(0, 1)), paint);
                            } else {
                                String strp = list.get(k)[3];
                                String str = strp.substring(0, strp.length() - 1);
                                paint.setColor(colors.get(j));
                                canvas.drawText(strp, rect.left + Float.parseFloat(str) / dataPMaxValue * rect.width() + 8,
                                        rect.bottom - DEFAULT_EMPTYPX - i * xAxisGridGap - (grid * ids.length) / 2, paint);
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawPointLine(Canvas canvas){
        for (int i=0;i<pointList.size()-1;i++){
            Paint paint = new Paint();
            paint.setStrokeWidth(5f);
            paint.setAntiAlias(true);
            paint.setColor(pointLineColor.get(i));
            canvas.drawLine(pointList.get(i)[0],pointList.get(i)[1],pointList.get(i+1)[0],pointList.get(i+1)[1],paint);
        }
    }

    /**
     * 绘制X轴
     * @param canvas
     * Canvas对象
     * */
    private void drawXAxisLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, paint);
    }

    /**
     * 绘制Y轴
     * @param canvas
     * Canvas对象
     * */
    private void drawYAxisLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(rect.left, rect.bottom, rect.left, rect.top, paint);
    }

    /**
     * 绘制Y百分比副轴
     * @param canvas
     * Canvas对象
     * */
    private void drawYAxisPLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(rect.left, rect.top, rect.right, rect.top, paint);
    }

    /**
     * 测量所需宽度和高度
     * @param widthMeasureSpec
     * 宽度
     * @param heightMeasureSpec
     * 高度
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 返回测量宽度
     * @param measureSpec
     * 传入的宽度值
     * @return
     * 测量宽度
     * */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    /**
     * 返回测量高度
     * @param measureSpec
     * 传入的高度值
     * @return
     * 测量高度
     * */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        String[] data = obtainOnClickSelectedArea(event);
        if (data == null) {
            try {
                if(click != null && !"".equals(click)) {
                    String str = "";
                    String vs[] = StringUtil.split(click, "||");
                    if(vs.length >= 1){
                        String[] op = vs[0].split(":");
                        if (op.length >= 2) {
                            if(click.startsWith("PAGE")) {
                                str = "MEIP_PAGE=" + op[1] + "\r\n";
                            } else if(click.startsWith("OP")) {
                                str = "MEIP_ACTION=" + op[1] + "\r\n";
                            }
                            if (!str.equals("")) {
                                if (vs.length >= 2) {
                                    String[] args = StringUtil.split(vs[1], ",");
                                    for (String arg : args) {
                                        str += arg + "\r\n";
                                    }
                                }
                                Intent intent = new Intent();
                                intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                intent.putExtra("meip", str);
                                LocalBroadcastManager
                                        .getInstance(ctx).sendBroadcast(intent);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                String clsName = this.getClass().getName();
                clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                String info = ctx.getString(R.string.axeac_toast_exp_click);
                Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (itemClicks.size() > 0) {
                try {
                    CustomDialog.Builder btnsDialog = new CustomDialog.Builder(ctx);
                    btnsDialog.setTitle(R.string.axeac_msg_choice);
                    btnsDialog.setCancelable(false);
                    btnsDialog.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    ListView lv = new ListView(ctx);
                    lv.setBackgroundColor(Color.WHITE);
                    lv.setCacheColorHint(Color.TRANSPARENT);
                    lv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    btnsDialog.setContentView(lv);
                    btnsDialog.setNeutralButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    CustomDialog dialog = btnsDialog.create();
                    List<String> vals = new ArrayList<String>();
                    for (String d : data) {
                        vals.add(d);
                    }
                    lv.setAdapter(new OptionAdapter(ctx, itemClicks, dialog, vals));
                    dialog.show();
                } catch (Throwable e) {
                    String clsName = this.getClass().getName();
                    clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                    String info = ctx.getString(R.string.axeac_toast_exp_click);
                    Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ctx, data[4], Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    /**
     * 判断点击手势是否在柱图柱上，并返回点击处设置的数据
     * @param event
     * MotionEvent对象
     * @return
     * 包含数据的String数组
     * */
    private String[] obtainOnClickSelectedArea(MotionEvent event) {
        String[] data = null;
        String u_uid = "";
        String[] uuids = rectMap.keySet().toArray(new String[0]);
        for (String uuid : uuids) {
            RectF rectF = rectMap.get(uuid);
            if (event.getX() > rectF.left && event.getX() < rectF.right
                    && event.getY() > rectF.top && event.getY() < rectF.bottom) {
                u_uid = uuid;
                break;
            }
        }
        if (!u_uid.equals("")) {
            data = dataMap.get(u_uid);
        }
        String[] uuidsp = rectPMap.keySet().toArray(new String[0]);
        for (String uuid : uuidsp) {
            RectF rectF = rectPMap.get(uuid);
            if (event.getX() > rectF.left && event.getX() < rectF.right
                    && event.getY() > rectF.top && event.getY() < rectF.bottom) {
                u_uid = uuid;
                break;
            }
        }
        if (!u_uid.equals("")) {
            if(null!=dataPMap.get(u_uid)&&!"".equals(dataPMap.get(u_uid))){
                data = dataPMap.get(u_uid);
            }
        }
        return data;
    }

    public View getView() {
        return this;
    }
}