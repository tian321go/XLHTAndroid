package com.axeac.android.sdk.ui.chart;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.StaticObject;

import java.util.ArrayList;

/**
 * Created by hp on 2018/9/13.
 */

public class GaugeChart extends View{

    private int color_outcircle;//灰色部分颜色
    private String color_smart_circle = "#C2B9B0";//指针中心颜色
    private String color_indicator_left = "#E1DCD6";//指针左侧颜色
    private String color_indicator_right = "#F4EFE9";//指针右侧颜色

    /**
     * 要画的内容的实际宽度
     */
    private int contentWidth;
    /**
     * view的实际宽度
     */
    private int viewWidth;
    /**
     * view的实际高度
     */
    private int viewHeight;
    /**
     * 外环线的宽度
     */
    private int outCircleWidth = 1;
    /**
     * 外环的半径
     */
    private int outCircleRadius = 0;
    /**
     * 内环的半径
     */
    private int inCircleRedius = 0;
    /**
     * 内环与外环的距离
     */
    private int outAndInDistance = 0;
    /**
     * 内环的宽度
     */
    private int inCircleWidth = 0;
    /**
     * 刻度盘距离它外面的圆的距离
     */
    private int dialOutCircleDistance = 0;
    /**
     * 内容中心的坐标
     */
    private int[] centerPoint = new int[2];


    /**
     * 刻度线的数量
     */
    private int dialCount = 0;
    /**
     * 每隔几次出现一个长线
     */
    private int dialPer = 0;
    /**
     * 长线的长度
     */
    private int dialLongLength = 0;
    /**
     * 短线的长度
     */
    private int dialShortLength = 0;
    /**
     * 刻度线距离圆心最远的距离
     */
    private int dialRadius = 0;


    /**
     * 圆弧开始的角度
     */
    private int startAngle = 0;
    /**
     * 圆弧划过的角度
     */
    private int allAngle = 0;

    private Paint mPaint;

    private Paint dataPaint = new Paint();
    /**
     * 刻度盘上数字的数量
     */
    private int figureCount = 6;



    /**
     * 边距长度
     * */
    private static final int DEFAULT_PADDING_LENGTH = 25;
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
     * 数据标题尺寸
     * */
    private String dataTitleFont;
    //数据字体
    private String dataFont;

    /**
     * 柱图类型
     * */
    private String type;

    //刻度颜色
    private int scaleColor;

    //进度条颜色
    private int progressColor;

    //中心圆颜色
    private int centerCircleColor;

    //当前进度
    private int progress;

    //当前进度标题
    private String progressTitle;

    public GaugeChart(Activity ctx) {
        super(ctx);
        this.ctx = ctx;
        int height = 0;
        Rect frame = new Rect();
        ctx.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        height = frame.top;
        height += ctx.findViewById(R.id.toolbar).getHeight();
        height += ctx.findViewById(R.id.layout_bottom).getHeight();
        this.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) (StaticObject.deviceWidth*0.618)));
        this.setBackgroundColor(getResources().getColor(R.color.background));
        this.getBackground().setAlpha(180);
        init();
    }

    public void setColor_outcircle(int color_outcircle){
        this.color_outcircle = color_outcircle;
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

    public  void setScaleColor(int scaleColor){
        this.scaleColor = scaleColor;
    }
    public void setProgressColor(int progressColor){
        this.progressColor = progressColor;
    }

    public void setCenterCircleColor(int centerCircleColor){
        this.centerCircleColor = centerCircleColor;
    }

    public void setProgressTitle(String progressTitle){
        this.progressTitle = progressTitle;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 初始化尺寸
     */
    private void initBaseValues(Canvas canvas) {
        Rect leftRect = drawTitle(canvas);
        rect = new RectF(0, leftRect.bottom, this.getWidth(), this.getHeight());
        viewWidth = this.getWidth() - DEFAULT_PADDING_LENGTH;
        viewHeight = (int) rect.height() - DEFAULT_PADDING_LENGTH;
        contentWidth = viewWidth > viewHeight ? viewHeight : viewWidth;
        outCircleRadius = contentWidth / 2 - outCircleWidth;
        outAndInDistance = (int) (contentWidth / 26.5);
        inCircleWidth = (int) (contentWidth / 10);
        centerPoint[0] = viewWidth / 2;
        centerPoint[1] = viewHeight / 2 + leftRect.bottom;
        inCircleRedius = outCircleRadius - outAndInDistance;
        if(type.toLowerCase().equals("base")) {
            startAngle = 150;
            allAngle = 240;
        }else{
            startAngle = 130;
            allAngle = 280;
        }
        dialOutCircleDistance = inCircleWidth;

        dialCount = 50;
        dialPer = 5;
        dialLongLength = (int) (dialOutCircleDistance / 1.2);
        dialShortLength = (int) (dialLongLength / 1.8);
        dialRadius = inCircleRedius;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (type.toLowerCase().equals("base")) {
            initBaseValues(canvas);
            drawBaseStatic(canvas);
            drawBaseDynamic(canvas);
        }else if(type.toLowerCase().equals("horizontaltube")){
            initBaseValues(canvas);
            drawHorizontalTubeStatic(canvas);
            drawHorizontalTubeDynamic(canvas);
        }else if(type.toLowerCase().equals("verticaltube")){
            initBaseValues(canvas);
            drawVerticalTubeStatic(canvas);
            drawVerticalTubeDynamic(canvas);
        }else if(type.toLowerCase().equals("donut")){
            initBaseValues(canvas);
            drawDountStatic(canvas);
            drawDountDynamic(canvas);
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
     * 绘制静态的部分
     *
     * @param canvas
     */
    private void drawBaseStatic(Canvas canvas) {
        drawOutCircle(canvas);
        drawDial(startAngle, allAngle, dialCount, dialPer, dialLongLength, dialShortLength, dialRadius, canvas);
        drawBackGround(canvas);
        drawFigure(canvas, figureCount);
    }

    /**
     * 绘制静态的部分
     *
     * @param canvas
     */
    private void drawDountStatic(Canvas canvas) {
        drawCircleWithRound(startAngle, allAngle, inCircleWidth, inCircleRedius, color_outcircle, canvas);
    }

    /**
     * 绘制静态的部分
     *
     * @param canvas
     */
    private void drawHorizontalTubeStatic(Canvas canvas){
        drawRectWithRound(inCircleWidth,color_outcircle,inCircleRedius,canvas);
        drawHorizontalTubeDial(inCircleWidth,dialLongLength, dialShortLength, scaleColor,canvas);
    }

    /**
     * 绘制静态的部分
     *
     * @param canvas
     */
    private void drawVerticalTubeStatic(Canvas canvas){
        drawVerticalRectWithRound(inCircleWidth,color_outcircle,inCircleRedius,canvas);
        drawVerticalTubeDial(inCircleWidth,dialLongLength, dialShortLength, scaleColor,canvas);
    }

    private void drawFigure(Canvas canvas, int count) {
        int figure = 0;
        int angle;
        for (int i = 0; i < count; i++) {
            figure = (int) (100 / (1f * count-1) * i);
            angle = (int) ((allAngle) / ((count-1) * 1f) * i) + startAngle;
            int[] pointFromAngleAndRadius = getPointFromAngleAndRadius(angle, dialRadius - dialLongLength * 2 );
            mPaint.setTextSize(21);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.save();
            canvas.rotate(angle+90,pointFromAngleAndRadius[0],pointFromAngleAndRadius[1]);
            canvas.drawText(figure+"%",pointFromAngleAndRadius[0],pointFromAngleAndRadius[1],mPaint);
            canvas.restore();
        }
    }

    /**
     * 画内层背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        mPaint.setColor(centerCircleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerPoint[0], centerPoint[1], (outCircleRadius / 3f / 2), mPaint);
    }

    /**
     * 画刻度盘
     *
     * @param startAngle  开始画的角度
     * @param allAngle    总共划过的角度
     * @param dialCount   总共的线的数量
     * @param per         每隔几个出现一次长线
     * @param longLength  长线的长度
     * @param shortLength 短线的长度
     * @param radius      距离圆心最远的地方的半径
     */
    private void drawDial(int startAngle, int allAngle, int dialCount, int per, int longLength, int shortLength, int radius, Canvas canvas) {
        int length;
        int angle;
        for (int i = 0; i <= dialCount; i++) {
            angle = (int) ((allAngle) / (dialCount * 1f) * i) + startAngle;

            if (i % 5 == 0) {
                length = longLength;
            } else {
                length = shortLength;
            }
            drawSingleDial(angle, length, radius, canvas);
        }
    }

    /**
     * 画刻度线
     * @param width 宽度
     * @param longLength  刻度线长度
     * @param shortLength 刻度线长度
     * @param color    刻度颜色
     */
    private void drawHorizontalTubeDial(int width,int longLength, int shortLength, int color, Canvas canvas) {
        int length;
        int x;
        boolean islongLength;
        for (int i = 0; i <= dialCount; i++) {
            x = width * 2 + i*(centerPoint[0] * 2 - 4 * width - width/2)/dialCount + width/4;

            if (i % 5 == 0) {
                length = longLength;
                islongLength = true;
            } else {
                length = shortLength;
                islongLength = false;
            }
            drawHorizontalTubeSingleDial(i * 2,x, centerPoint[1] + width/2, length, islongLength, canvas);
        }
    }


    /**
     * 画刻度线
     * @param width 宽度
     * @param longLength  刻度线长度
     * @param shortLength 刻度线长度
     * @param color    刻度颜色
     */
    private void drawVerticalTubeDial(int width,int longLength, int shortLength, int color, Canvas canvas) {
        int length;
        int y;
        boolean islongLength;
        for (int i = 0; i <= dialCount; i++) {
            y = (int) (rect.bottom - i*(rect.bottom - rect.top - width/2 - 20)/dialCount - width/4 - 20);
            if (i % 5 == 0) {
                length = longLength;
                islongLength = true;
            } else {
                length = shortLength;
                islongLength = false;
            }
            drawVerticalTubeSingleDial(i * 2,centerPoint[0] + width/2, y, length, islongLength,color,canvas);
        }
    }

    /**
     * 画刻度中的一条线
     *
     * @param angle  所处的角度
     * @param length 线的长度
     * @param radius 距离圆心最远的地方的半径
     */
    private void drawSingleDial(int angle, int length, int radius, Canvas canvas) {
        mPaint.setColor(scaleColor);
        int[] startP = getPointFromAngleAndRadius(angle, radius);
        int[] endP = getPointFromAngleAndRadius(angle, radius - length);
        canvas.drawLine(startP[0], startP[1], endP[0], endP[1], mPaint);
    }

    /**
     * 画刻度中的一条线
     */
    private void drawHorizontalTubeSingleDial(int text, float x,float y, int length,boolean isLongLength, Canvas canvas) {
        mPaint.setColor(scaleColor);
        canvas.drawLine(x, y, x, y + length, mPaint);
        if (isLongLength&&text%20==0){
            mPaint.setTextSize(18);
            float textWidth = mPaint.measureText(String.valueOf(text));
            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            canvas.drawText(String.valueOf(text),x-textWidth/2,y + length + metrics.bottom - metrics.top,mPaint);
        }
    }

    /**
     * 画刻度中的一条线
     */
    private void drawVerticalTubeSingleDial(int text, float x,float y, int length,boolean isLongLength, int color, Canvas canvas) {
        mPaint.setColor(color);
        canvas.drawLine(x, y, x + length, y, mPaint);
        if (isLongLength&&text%20==0){
            mPaint.setTextSize(18);
            float textWidth = mPaint.measureText(String.valueOf(text));
            Paint.FontMetrics metrics = mPaint.getFontMetrics();
            canvas.drawText(String.valueOf(text),x + length + textWidth/2,y,mPaint);
        }
    }

    /**
     * 画最外层的圆
     *
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        mPaint.setStrokeWidth(outCircleWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(color_outcircle);
        canvas.drawCircle(centerPoint[0], centerPoint[1], outCircleRadius, mPaint);
    }

    /**
     * 绘制动态的部分
     *
     * @param canvas
     */
    private void drawBaseDynamic(Canvas canvas) {
        drawIndicator(canvas);
        drawBaseCurrentProgressTv(progress, canvas);
    }

    /**
     * 绘制动态的部分
     *
     * @param canvas
     */
    private void drawDountDynamic(Canvas canvas) {
        drawDonutProgress(progress, canvas);
        drawDonutCurrentProgressTv(progress, canvas);
    }

    /**
     * 绘制动态的部分
     *
     * @param canvas
     */
    private void drawHorizontalTubeDynamic(Canvas canvas) {
        drawHorizontalTubeProgress(progress, inCircleWidth,progressColor,inCircleRedius,canvas);
        drawHorizontalTubeCurrentProgressTv(progress, inCircleWidth, canvas);
    }

    /**
     * 绘制动态的部分
     *
     * @param canvas
     */
    private void drawVerticalTubeDynamic(Canvas canvas) {
        drawVerticalTubeProgress(progress, inCircleWidth,progressColor,inCircleRedius,canvas);
        drawVerticalTubeCurrentProgressTv(progress, inCircleWidth, canvas);
    }

    private void setDataPaint(String font){
        float size = 25;
        if (font != null && !"".equals(font)) {
            if (font.indexOf(";") != -1) {
                String[] strs = font.split(";");
                for (String str : strs) {
                    if (str.startsWith("font-size")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        size = Float.parseFloat(s.replace("px", "").trim());
                        dataPaint.setTextSize(size);
                    } else if(str.startsWith("style")) {
                        int index = str.indexOf(":");
                        if (index == -1)
                            continue;
                        String s = str.substring(index + 1).trim();
                        if ("bold".equals(s)){
                            dataPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        } else if("italic".equals(s)) {
                            dataPaint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        } else {
                            if (s.indexOf(",") != -1) {
                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                    dataPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                                }
                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                    dataPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
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
                            dataPaint.setColor(Color.rgb(r, g, b));
                        } else {
                            dataPaint.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
        dataPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 绘制当前进度文字
     *
     * @param progress
     * @param canvas
     */
    private void drawBaseCurrentProgressTv(int progress, Canvas canvas) {
        setDataPaint(dataTitleFont);
        Paint.FontMetrics fontMetrics = dataPaint.getFontMetrics();
        float baseLine1 = centerPoint[1] + (outCircleRadius / 20f * 11 - fontMetrics.top - fontMetrics.bottom);
        canvas.drawText(progressTitle, centerPoint[0], baseLine1, dataPaint);
        //drawText的第二个参数的值=要让文字的中心放在哪-（fontMetrics.top+fontMetrics.bottom）/2
        //此时求出来的baseline可以使文字竖直居中
        setDataPaint(dataFont);
        float baseLine2 = outCircleRadius / 20f * 11 - 4 * (fontMetrics.bottom + fontMetrics.top) + centerPoint[1];
        canvas.drawText(progress + "%", centerPoint[0], baseLine2, dataPaint);


    }

    /**
     * 绘制当前进度文字
     *
     * @param progress
     * @param canvas
     */
    private void drawDonutCurrentProgressTv(int progress, Canvas canvas) {
        setDataPaint(dataTitleFont);
        Paint.FontMetrics fontMetrics = dataPaint.getFontMetrics();
        float baseLine1 = centerPoint[1] - fontMetrics.top + fontMetrics.bottom;
        canvas.drawText(progressTitle, centerPoint[0], baseLine1, dataPaint);
        setDataPaint(dataFont);
        float baseLine2 = centerPoint[1] - fontMetrics.bottom + fontMetrics.top;
        canvas.drawText(progress + "%", centerPoint[0], baseLine2, dataPaint);


    }

    /**
     * 绘制当前进度文字
     *
     * @param progress
     * @param canvas
     */
    private void drawHorizontalTubeCurrentProgressTv(int progress,int width, Canvas canvas) {
        setDataPaint(dataTitleFont);
        float right = progress / 100f;
        Paint.FontMetrics fontMetrics = dataPaint.getFontMetrics();
        float baseLine1 = centerPoint[1] - width/2 - fontMetrics.bottom + fontMetrics.top;
        canvas.drawText(progressTitle, width * 2 + (centerPoint[0] * 2 - width * 4) * right, baseLine1, dataPaint);
        setDataPaint(dataFont);
        float text1Height = fontMetrics.bottom - fontMetrics.top;
        Paint.FontMetrics fontMetrics1 = dataPaint.getFontMetrics();
        float baseLine2 = centerPoint[1] - width/2 - (fontMetrics1.bottom - fontMetrics1.top)/2 - text1Height;
        canvas.drawText(progress + "%", width * 2 + (centerPoint[0] * 2 - width * 4) * right, baseLine2, dataPaint);


    }

    /**
     * 绘制当前进度文字
     *
     * @param progress
     * @param canvas
     */
    private void drawVerticalTubeCurrentProgressTv(int progress,int width, Canvas canvas) {
        setDataPaint(dataTitleFont);
        float right = progress / 100f;
        float textWidth = dataPaint.measureText(progressTitle);
        float baseLine1 = rect.bottom - width/4 - (rect.bottom-rect.top)*right- 20;
        canvas.drawText(progressTitle, centerPoint[0] - width/2 - textWidth, baseLine1, dataPaint);
        setDataPaint(dataFont);
        Paint.FontMetrics fontMetrics1 = dataPaint.getFontMetrics();
        float text1Width = dataPaint.measureText(String.valueOf(progress));
        float baseLine2 = rect.bottom - width/4 - (rect.bottom-rect.top)*right - 20 + fontMetrics1.bottom - fontMetrics1.top;
        canvas.drawText(progress + "%", centerPoint[0] - width/2 - text1Width, baseLine2, dataPaint);
    }

    /**
     * 画指针以及他的背景
     *
     * @param canvas
     */
    private void drawIndicator(Canvas canvas) {
        drawPointer(canvas);
        drawIndicatorBg(canvas);
    }

    /**
     * 指针的最远处的半径和刻度线的一样
     */
    private void drawPointer(Canvas canvas) {
        RectF rectF = new RectF(centerPoint[0] - (int) (outCircleRadius / 3f / 2 / 2),
                centerPoint[1] - (int) (outCircleRadius / 3f / 2 / 2), centerPoint[0] + (int) (outCircleRadius / 3f / 2 / 2), centerPoint[1] + (int) (outCircleRadius / 3f / 2 / 2));
        int angle = (int) ((allAngle) / (100 * 1f) * progress) + startAngle;
        //指针的定点坐标
        int[] peakPoint = getPointFromAngleAndRadius(angle, dialRadius);
        //顶点朝上，左侧的底部点的坐标
        int[] bottomLeft = getPointFromAngleAndRadius(angle - 90, (int) (outCircleRadius / 3f / 2 / 2));
        //顶点朝上，右侧的底部点的坐标
        int[] bottomRight = getPointFromAngleAndRadius(angle + 90, (int) (outCircleRadius / 3f / 2 / 2));
        Path path = new Path();
        mPaint.setColor(Color.parseColor(color_indicator_left));
        path.moveTo(centerPoint[0], centerPoint[1]);
        path.lineTo(peakPoint[0], peakPoint[1]);
        path.lineTo(bottomLeft[0], bottomLeft[1]);
        path.close();
        canvas.drawPath(path, mPaint);
        canvas.drawArc(rectF, angle - 180, 100, true, mPaint);
        Log.e("InstrumentView", "drawPointer" + angle);


        mPaint.setColor(Color.parseColor(color_indicator_right));
        path.reset();
        path.moveTo(centerPoint[0], centerPoint[1]);
        path.lineTo(peakPoint[0], peakPoint[1]);
        path.lineTo(bottomRight[0], bottomRight[1]);
        path.close();
        canvas.drawPath(path, mPaint);

        canvas.drawArc(rectF, angle + 80, 100, true, mPaint);


    }

    private void drawIndicatorBg(Canvas canvas) {
        mPaint.setColor(Color.parseColor(color_smart_circle));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerPoint[0], centerPoint[1], (outCircleRadius / 3f / 2 / 4), mPaint);
    }

    /**
     * 根据进度画进度条
     *
     * @param progress 最大进度为100.最小为0
     */
    private void drawDonutProgress(int progress, Canvas canvas) {
        float ratio = progress / 100f;
        int angle = (int) (allAngle * ratio);
        drawCircleWithRound(startAngle, angle, inCircleWidth, inCircleRedius, progressColor, canvas);

    }

    /**
     * 根据进度画进度条
     *
     * @param progress 最大进度为100.最小为0
     */
    private void drawHorizontalTubeProgress(int progress,int width, int color, int radius, Canvas canvas) {
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setColor(color);
        float right = progress / 100f;
        RectF rectF = new RectF(width * 2,centerPoint[1] - width/2,width * 2 + (centerPoint[0] *2-width * 4) * right,centerPoint[1] + width/2);
        canvas.drawRoundRect(rectF,radius,radius,mPaint);
        canvas.drawCircle(width * 2 + (centerPoint[0] * 2 - width * 4) * right,centerPoint[1],width/3*2,mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(width * 2 + (centerPoint[0] * 2 - width * 4) * right,centerPoint[1],width/3,mPaint);
    }

    /**
     * 根据进度画进度条
     *
     * @param progress 最大进度为100.最小为0
     */
    private void drawVerticalTubeProgress(int progress,int width, int color, int radius, Canvas canvas) {
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setColor(color);
        float top = progress / 100f;
        RectF rectF = new RectF(centerPoint[0] - width/2,rect.bottom - (rect.bottom - rect.top) * top - 20,centerPoint[0] + width/2,rect.bottom - 20);
        canvas.drawRoundRect(rectF,radius,radius,mPaint);
        canvas.drawCircle(centerPoint[0],rect.bottom - (rect.bottom - rect.top) * top - 20,width/3*2,mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerPoint[0],rect.bottom - (rect.bottom - rect.top) * top - 20,width/3,mPaint);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    /**
     * 画一个两端为圆弧的圆形曲线
     *
     * @param startAngle 曲线开始的角度
     * @param allAngle   曲线走过的角度
     * @param radius     曲线的半径
     * @param width      曲线的厚度
     */
    private void drawCircleWithRound(int startAngle, int allAngle, int width, int radius, int color, Canvas canvas) {
        mPaint.setStrokeWidth(width);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(color);
        RectF rectF = new RectF(centerPoint[0] - radius, centerPoint[1] - radius, centerPoint[0] + radius, centerPoint[1] + radius);
        canvas.drawArc(rectF, startAngle, allAngle, false, mPaint);
        drawArcRoune(radius, startAngle, width, canvas);
        drawArcRoune(radius, startAngle + allAngle, width, canvas);
    }

    private void drawRectWithRound(int width, int color, int radius, Canvas canvas){
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setColor(color);
        RectF rectF = new RectF(width * 2,centerPoint[1] - width/2,centerPoint[0]*2 - width * 2,centerPoint[1] + width/2);
        canvas.drawRoundRect(rectF,radius,radius,mPaint);
    }

    private void drawVerticalRectWithRound(int width, int color, int radius, Canvas canvas){
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setColor(color);
        RectF rectF = new RectF(centerPoint[0] - width/2,rect.top,centerPoint[0] + width/2,rect.bottom - 20);
        canvas.drawRoundRect(rectF,radius,radius,mPaint);
    }

    /**
     * 绘制圆弧两端的圆
     *
     * @param radius 圆弧的半径
     * @param angle  所处于圆弧的多少度的位置
     * @param width  圆弧的宽度
     */
    private void drawArcRoune(int radius, int angle, int width, Canvas canvas) {
        int[] point = getPointFromAngleAndRadius(angle, radius);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(point[0], point[1], width / 2, mPaint);
    }

    /**
     * 根据角度和半径，求一个点的坐标
     *
     * @param angle
     * @param radius
     * @return
     */
    private int[] getPointFromAngleAndRadius(int angle, int radius) {
        double x = radius * Math.cos(angle * Math.PI / 180) + centerPoint[0];
        double y = radius * Math.sin(angle * Math.PI / 180) + centerPoint[1];
        return new int[]{(int) x, (int) y};
    }

    public View getView() {
        return this;
    }
}
