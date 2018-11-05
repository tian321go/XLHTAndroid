package com.axeac.android.sdk.ui.chart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.OptionAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.StaticObject;
/**
 * 饼图视图
 * @author axeac
 * @version 1.0.0
 * */
public class PieChart extends View {
    /**
     * 边距长度
     * */
    private static final int DEFAULT_PADDING_LENGTH = 25;
    /**
     * 默认半径
     * */
    private static final int DEFAULT_RADIUS_LENGTH = 200;

    private Activity ctx;
    /**
     * Point对象
     * */
    private Point centre = new Point(0, 0);
    /**
     * RectF对象
     * */
    private RectF rect;

    /**
     *圆半径
     */
    private int radius = DEFAULT_RADIUS_LENGTH;

    /**
     * 不同类型饼图标志
     * <br>可选值：1  2  1代表常规饼图，2代表空心饼图
     * */
    private int type;

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
     * 中间标题文本
     * */
    private String titleCenter;
    /**
     * 中间标题文字尺寸
     * */
    private String titleCenterFont;

    /**
     * 百分比文字尺寸
     * */
    private String perFont;

    /**
     * 顶部标题文本
     * */
    private String titleTop;

    /**
     * 顶部标题文字尺寸
     * */
    private String titleTopFont;

    /**
     * 顶部副标题文本
     * */
    private String titleTopSub;
    /**
     * 顶部副标题文字尺寸
     * */
    private String titleTopSubFont;

    /**
     * 存储图形数据的Map集合
     * */
    private LinkedHashMap<String, String[]> datas;
    /**
     * 存放颜色值的list集合
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


    private boolean isPerData = false;

    /**
     * 存储扇形角度的list集合
     * */
    private ArrayList<Integer[]> angles = new ArrayList<Integer[]>();

    public PieChart(Activity ctx) {
        super(ctx);
        this.ctx = ctx;
        this.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 100));
        this.setBackgroundColor(getResources().getColor(R.color.background));
        this.getBackground().setAlpha(180);
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
     * 对datas进行赋值（图形数据集合）
     * @param datas
     * 图形数据集合
     * */
    public void setDatas(LinkedHashMap<String, String[]> datas) {
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

    /**
     * 对isPerData赋值
     * @param isPerData
     *
     * */
    public void setIsPerData(boolean isPerData) {
        this.isPerData = isPerData;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas);
    }

    /**
     * 绘制饼图以及标题等
     * @param canvas
     * Canvas对象
     * */
    private void drawChart(Canvas canvas) {
        radius = (int) (this.getWidth() * 0.618 / 2f) - DEFAULT_PADDING_LENGTH * 2;
        Rect topLeftRect = drawTitleTop(canvas);
        Rect topRightRect = drawDataTitle(canvas, topLeftRect);
        int titleTopHeight = topLeftRect.bottom;//> topRightRect.bottom ? topLeftRect.bottom : topRightRect.bottom;
        Rect bottomRect = drawTitleBottom(canvas, titleTopHeight);
        if (title.equals("") && subTitle.equals("")) {
            this.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, bottomRect.top + DEFAULT_PADDING_LENGTH));
        } else {
            this.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, bottomRect.bottom));
        }
        centre = new Point(DensityUtil.dip2px(ctx, 100) + (bottomRect.width() / 2), titleTopHeight + radius + DEFAULT_PADDING_LENGTH);

        if (type==1){
            rect = new RectF(centre.x - radius, centre.y - radius, centre.x + radius, centre.y + radius);
            drawBorderCircle(canvas);
            drawInsideBorderCircle(canvas);
            drawDiagram(canvas);
            drawSumCircle(canvas);
        }
        if (type==2) {
            rect = new RectF(centre.x - radius-10, centre.y - radius-10, centre.x + radius+10, centre.y + radius+10);
            drawDiagram(canvas);
            drawSumCircle(canvas);
            drawCenterCircle(canvas);
            drawInCircle(canvas);
        }

    }

    /**
     * describe:Top main subtitle
     * 绘制顶部主副标题
     *
     * @param canvas
     * Canvas对象
     */
    private Rect drawTitleTop(Canvas canvas) {
        // main title
        // 主标题
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float titleTopTextSize = 30;
        if (titleTopFont != null && !"".equals(titleTopFont)) {
            if (titleTopFont.indexOf(";") != -1) {
                String[] strs = titleTopFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        titleTopTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
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
                    } else if (str.startsWith("color")) {
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
                            paint.setColor(Color.BLACK);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        canvas.drawText(titleTop, DEFAULT_PADDING_LENGTH, paint.getFontMetrics().bottom - paint.getFontMetrics().top, paint);
        int titleWidth = (int) paint.measureText(titleTop) + DEFAULT_PADDING_LENGTH * 2;
        int titleHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top + titleTopTextSize * 0.75);
        // subtitle
        // 副标题
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float titleTopSubTextSize = 23;
        if (titleTopSubFont != null && !"".equals(titleTopSubFont)) {
            if (titleTopSubFont.indexOf(";") != -1) {
                String[] strs = titleTopSubFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1) continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        titleTopSubTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
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
                    } else if (str.startsWith("color")) {
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
                            paint.setColor(Color.BLACK);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        canvas.drawText(titleTopSub, DEFAULT_PADDING_LENGTH, paint.getFontMetrics().bottom - paint.getFontMetrics().top + titleHeight, paint);
        int subTitleWidth = (int) paint.measureText(titleTopSub) + DEFAULT_PADDING_LENGTH * 2;
        int subTitleHeight = (int) (paint.getFontMetrics().bottom - paint.getFontMetrics().top + titleTopSubTextSize * 0.75);

        int width = titleWidth > subTitleWidth ? titleWidth : subTitleWidth;
        int height = titleHeight + subTitleHeight;
        return new Rect(0, 0, width, height);
    }

    /**
     * 绘制数据文字
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
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
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
                    } else if (str.startsWith("color")) {
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
                            paint.setColor(Color.BLACK);
                        }
                    }
                }
            }
        }
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        Rect rect = new Rect(rectF);
        rect.set(rect.right + DEFAULT_PADDING_LENGTH, rect.top + radius + DEFAULT_PADDING_LENGTH, this.getWidth() - 25, rect.bottom + radius + DEFAULT_PADDING_LENGTH);
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
            int colsCount = rect.width() / (itemWidth + 25 * 2); //??
            int rowsCount = list.length / colsCount + ((list.length % colsCount) > 0 ? 1 : 0); //??
            rect.set(rect.right - (rect.right - DEFAULT_PADDING_LENGTH - radius * 2) / 2 - (itemWidth + DEFAULT_PADDING_LENGTH * 2), rect.top, rect.right, rect.top + lineHeight * list.length);
            for (int i = 0; i < list.length; i++) {
                canvas.drawText(list[i], rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) + DEFAULT_PADDING_LENGTH * 2, rect.top + lineHeight * i + itemHeight, paint);
                Paint p = new Paint();
                p.setColor(colors.get(i));
                p.setStyle(Style.FILL_AND_STROKE);
                p.setAntiAlias(true);
                canvas.drawCircle(rect.left + (itemWidth + DEFAULT_PADDING_LENGTH * 2) + DEFAULT_PADDING_LENGTH, rect.top + lineHeight * i + lineHeight / 2, 13, p);
            }
        }
        return rect;
    }

    /**
     * 绘制底部标题
     * @param canvas
     * Canvas对象
     * @param topHeight
     * */
    private Rect drawTitleBottom(Canvas canvas, int topHeight) {
        // main title
        // 主标题
        Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
                        titlePaint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        titleTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            titlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
                            titlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    titlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    titlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                            }
                        }
                    } else if (str.startsWith("color")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if (CommonUtil.validRGBColor(s)) {
                            int r = Integer.parseInt(s.substring(0, 3));
                            int g = Integer.parseInt(s.substring(3, 6));
                            int b = Integer.parseInt(s.substring(6, 9));
                            titlePaint.setColor(Color.rgb(r, g, b));
                        } else {
                            titlePaint.setColor(Color.BLACK);
                        }
                    }
                }
            }
        }
        titlePaint.setStyle(Style.STROKE);

        // subtitle
        // 副标题
        Paint subTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float subTitleTextSize = 23;
        if (subTitleFont != null && !"".equals(subTitleFont)) {
            if (subTitleFont.indexOf(";") != -1) {
                String[] strs = subTitleFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1) continue;
                        String s = str.substring(index + 1).trim();
                        subTitlePaint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        subTitleTextSize = Float.parseFloat(s.replace("px", "").trim());
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            subTitlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
                            subTitlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    subTitlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    subTitlePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                            }
                        }
                    } else if (str.startsWith("color")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if (CommonUtil.validRGBColor(s)) {
                            int r = Integer.parseInt(s.substring(0, 3));
                            int g = Integer.parseInt(s.substring(3, 6));
                            int b = Integer.parseInt(s.substring(6, 9));
                            subTitlePaint.setColor(Color.rgb(r, g, b));
                        } else {
                            subTitlePaint.setColor(Color.BLACK);
                        }
                    }
                }
            }
        }
        subTitlePaint.setStyle(Style.STROKE);

        int titleWidth = (int) titlePaint.measureText(title);
        int titleHeight = (int) (titlePaint.getFontMetrics().bottom - titlePaint.getFontMetrics().top + titleTextSize * 0.75);
        int subTitleWidth = (int) subTitlePaint.measureText(subTitle);
        int subTitleHeight = (int) (subTitlePaint.getFontMetrics().bottom - subTitlePaint.getFontMetrics().top + subTitleTextSize * 0.75);

        int top = topHeight + radius * 2 + DEFAULT_PADDING_LENGTH * 2;
        Rect rect = new Rect(DensityUtil.dip2px(ctx, 100), top, radius * 2 + DEFAULT_PADDING_LENGTH * 4, top + titleHeight + subTitleHeight);

        if (!title.equals("") || !subTitle.equals("")) {
            RectF bgRect = new RectF(rect.left + 5, rect.top + 5, rect.right - 5, rect.bottom - 5);
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(Color.WHITE);
            bgPaint.setStyle(Style.STROKE);
            canvas.drawText(title, DensityUtil.dip2px(ctx, 100) + (rect.width() - titleWidth) / 2, (int) (top + titleHeight - titleTextSize * 0.75), titlePaint);
            canvas.drawText(subTitle, DensityUtil.dip2px(ctx, 100) + (rect.width() - subTitleWidth) / 2, (int) (top + titleHeight + subTitleHeight - subTitleTextSize * 0.75), subTitlePaint);
        }
        return rect;
    }

    /**
     * 绘制外部黑圈
     *
     * @param canvas
     * Canvas对象
     */
    private void drawBorderCircle(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(40, 40, 40));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawCircle(centre.x, centre.y, radius + 20, paint);
    }

    /**
     * 绘制内部透明小圈
     *
     * @param canvas
     * Canvas对象
     */
    private void drawInsideBorderCircle(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(36, 36, 36, 36));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.drawCircle(centre.x, centre.y, radius, paint);
    }

    /**
     * 绘制饼图圆
     * @param canvas
     * Canvas对象
     * */
    private void drawSumCircle(Canvas canvas) {
        if ("".equals(titleCenter)) {
            return;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.argb(36, 36, 36, 36));
        paint.setStyle(Style.FILL);
        canvas.drawCircle(centre.x, centre.y, (float) (radius * 0.3), paint);
        if (titleCenterFont != null && !"".equals(titleCenterFont)) {
            if (titleCenterFont.indexOf(";") != -1) {
                String[] strs = titleCenterFont.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1) continue;
                        String s = str.substring(index + 1).trim();
                        paint.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                    } else if (str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)) {
                            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if ("italic".equals(s)) {
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
                    } else if (str.startsWith("color")) {
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
        canvas.drawText(titleCenter, centre.x - paint.measureText(titleCenter) / 2, centre.y + 8, paint);
    }

    /**
     * 绘制空心饼图空心圆
     * @param canvas
     * Canvas对象
     * */
    private void drawCenterCircle(Canvas canvas){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth((radius+20)/2);
        canvas.drawCircle(centre.x, centre.y, (radius+20)/2, paint);
    }

    /**
     * 绘制空心饼图内部阴影圆环
     * @param canvas
     * Canvas对象
     * */
    private void drawInCircle(Canvas canvas){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setAlpha(30);
        paint.setStrokeWidth(radius/2);
        canvas.drawCircle(centre.x, centre.y, (radius+20)/2+20, paint);
    }
    /**
     * 绘制饼图圆内扇形
     *
     * @param canvas
     * Canvas对象
     */
    private void drawDiagram(Canvas canvas) {
        if (datas.size() > 0) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            String[] keys = datas.keySet().toArray(new String[0]);
            for (int i = 0; i < keys.length; i++) {
                list.add(datas.get(keys[i]));
            }
            boolean is100 = true;
            float sum = 0;
            if (isPerData) {
                for (int i = 0; i < list.size(); i++) {
                    if (sum >= 100) {
                        list.get(i)[2] = 0 + "";
                    }
                    if (sum + Float.parseFloat(list.get(i)[2]) >= 100) {
                        list.get(i)[2] = 100 - sum + "";
                        sum = 100;
                    }
                    if (sum < 100) {
                        sum = sum + Float.parseFloat(list.get(i)[2]);
                    }
                }
                if (sum < 100) {
                    is100 = false;
                    list.add(new String[]{"idnull", "", (100 - sum) + "", ""});
                }
                sum = 100;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    sum = sum + Float.parseFloat(list.get(i)[2]);
                }
            }
            Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mArcPaint.setStyle(Style.FILL);
            int offset = -90;
            float sumval = 0f;
            for (int i = 0; i < list.size(); i++) {
                if (!is100 && i == list.size() - 1) {
                    mArcPaint.setColor(Color.DKGRAY);
                } else {
                    mArcPaint.setColor(colors.get(i));
                }
                int sweep = Math.round(Float.parseFloat(list.get(i)[2]) / sum * 360f);
                canvas.drawArc(rect, offset, sweep-1, true, mArcPaint);
                angles.add(new Integer[]{offset, sweep});
                offset = offset + sweep;

                double value = Double.parseDouble(list.get(i)[2]);
                sumval = sumval + (float) value;
                float rate = (sumval - (float) value / 2) / sum;
                float offsetX;
                float offsetY;
                if (type==1){
                    offsetX = (float) (centre.x - radius * 0.5 * Math.sin(rate * -2 * Math.PI));
                    offsetY = (float) (centre.y - radius * 0.5 * Math.cos(rate * -2 * Math.PI));
                }
                else{
                    offsetX = (float) (centre.x - radius * 0.8 * Math.sin(rate * -2 * Math.PI));
                    offsetY = (float) (centre.y - radius * 0.8 * Math.cos(rate * -2 * Math.PI));

                }
                Paint mPaintFont = new Paint(Paint.ANTI_ALIAS_FLAG);
                if (perFont != null && !"".equals(perFont)) {
                    if (perFont.indexOf(";") != -1) {
                        String[] strs = perFont.split(";");
                        for (String str : strs) {
                            if (str.startsWith("font-size")) {
                                int index = str.indexOf(":");
                                if (index == -1) continue;
                                String s = str.substring(index + 1).trim();
                                mPaintFont.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                            } else if (str.startsWith("style")) {
                                int index = str.indexOf(":");
                                if (index == -1)
                                    continue;
                                String s = str.substring(index + 1).trim();
                                if ("bold".equals(s)) {
                                    mPaintFont.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                } else if ("italic".equals(s)) {
                                    mPaintFont.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                } else {
                                    if (s.indexOf(",") != -1) {
                                        if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                            mPaintFont.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                        }
                                        if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                            mPaintFont.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                        }
                                    }
                                }
                            } else if (str.startsWith("color")) {
                                int index = str.indexOf(":");
                                if (index == -1)
                                    continue;
                                String s = str.substring(index + 1).trim();
                                if (CommonUtil.validRGBColor(s)) {
                                    int r = Integer.parseInt(s.substring(0, 3));
                                    int g = Integer.parseInt(s.substring(3, 6));
                                    int b = Integer.parseInt(s.substring(6, 9));
                                    mPaintFont.setColor(Color.rgb(r, g, b));
                                } else {
                                    mPaintFont.setColor(Color.WHITE);
                                }
                            }
                        }
                    }
                }
                String desc = "";
                if (isPerData) {
                    desc = value + "%";
                } else {
                    desc = value + "";
                }
                float realx = 0;
                float realy = 0;
                if (offsetX < centre.x) {
                    realx = offsetX - mPaintFont.measureText(desc) + 15;
                } else if (offsetX > centre.x) {
                    realx = offsetX - 30;
                } else {
                    realx = offsetX - mPaintFont.measureText(desc) / 2;
                }
                if (offsetY > centre.y) {
                    if (value / sum < 0.2f) {
                        realy = offsetY + 10;
                    } else {
                        realy = offsetY + 5;
                    }
                } else if (offsetY < centre.y) {
                    if (value / sum < 0.2f) {
                        realy = offsetY - 10;
                    } else {
                        realy = offsetY + 5;
                    }
                } else {
                    realy = offsetY + 3;
                }

                canvas.drawText(desc, realx, realy, mPaintFont);
            }
        }
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
                if (click != null && !"".equals(click)) {
                    String str = "";
                    String vs[] = StringUtil.split(click, "||");
                    if (vs.length >= 1) {
                        String[] op = vs[0].split(":");
                        if (op.length >= 2) {
                            if (click.startsWith("PAGE")) {
                                str = "MEIP_PAGE=" + op[1] + "\r\n";
                            } else if (click.startsWith("OP")) {
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
                Toast.makeText(ctx, data[3], Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    /**
     * 判断点击手势是否在饼图内，并返回点击处设置的数据
     * @param event
     * MotionEvent对象
     * @return
     * 包含数据的String数组
     * */
    private String[] obtainOnClickSelectedArea(MotionEvent event) {
        String[] data = null;
        int index = -1;
        boolean isInsideCircle = false;
        ArrayList<String[]> list = new ArrayList<String[]>();
        String[] keys = datas.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            list.add(datas.get(keys[i]));
        }
        for (int i = 0; i < list.size(); i++) {

            if (type==1){
                // To determine whether it is in the circle
                // 判断是否在圆内
                isInsideCircle = (event.getX() - centre.x) * (event.getX() - centre.x)
                        + (event.getY() - centre.y) * (event.getY() - centre.y)
                        <= radius * radius ? true : false;
            }else{
                // To determine whether it is in the circle
                //判断是否在圆内
                isInsideCircle = (event.getX() - centre.x) * (event.getX() - centre.x)
                        + (event.getY() - centre.y) * (event.getY() - centre.y)
                        <= (radius+10)* (radius+10)&&(event.getX() - centre.x) * (event.getX() - centre.x)
                        + (event.getY() - centre.y) * (event.getY() - centre.y)
                        >= ((radius+20)/2+20)* ((radius+20)/2+20) ? true : false;
            }

            if (isInsideCircle) {
                float tanX = (event.getY() - centre.y) / (event.getX() - centre.x);
                double angle = Math.atan(tanX) * 180 / Math.PI;
                /**
                 * describe:Second quadrant | first quadrant
                 *         -----------------------------------
                 *          Third quadrant | fourth quadrant
                 *
                 * 描述： 第二象限|第一象限
                 *       ------------------
                 *        第三象限|第四象限
                 */
                if (event.getY() <= centre.y && event.getX() > centre.x) {
                    // First quadrant area
                    // 第一象限区域内
                } else if (event.getY() <= centre.y && event.getX() < centre.x) {
                    // Second quadrant area
                    // 第二象限区域内
                    angle += 180;
                } else if (event.getY() > centre.y && event.getX() < centre.x) {
                    // Third quadrant area
                    // 第三象限区域内
                    angle += 180;
                } else if (event.getY() > centre.y && event.getX() > centre.x) {
                    // Fourth quadrant area
                    // 第四象限区域内
                }
                if (angle >= angles.get(i)[0] && angle <= angles.get(i)[0] + angles.get(i)[1]) {
                    index = i;
                    break;
                }
            }
        }
        if (index != -1) {
            data = list.get(index);
        }
        return data;
    }

    public View getView() {
        return this;
    }
}