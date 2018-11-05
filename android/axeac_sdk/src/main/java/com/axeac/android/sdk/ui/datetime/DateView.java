package com.axeac.android.sdk.ui.datetime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.List;

import com.axeac.android.sdk.R;
/**
 * 时间视图
 * @author axeac
 * @version 1.0.0
 * */
public class DateView extends View {

    /**
     * describe:Scrolling duration
     * 滚动持续时间
     *  默认值：400
     */
    private static final int SCROLLING_DURATION = 400;

    /**
     * describe:Minimum delta for scrolling
     * 滚动的最小增量
     * 默认值：1
     */
    private static final int MIN_DELTA_FOR_SCROLLING = 1;

    /**
     * describe:Current value & label text color
     * 当前值和标签文字颜色
     * 默认值：0xFF02a3f4
     */
    private static final int VALUE_TEXT_COLOR = 0xFF02a3f4;

    /**
     * describe:Items text color
     * item文字颜色
     * 默认值：0xFF333333
     */
    private static final int ITEMS_TEXT_COLOR = 0xFF333333;

    /**
     * describe:Top and bottom shadows colors
     * 顶部和底部阴影的颜色
     * 默认值：[0xFF111111, 0x00AAAAAA, 0x00AAAAAA]
     */
    private static final int[] SHADOWS_COLORS = new int[]{0xFF111111, 0x00AAAAAA, 0x00AAAAAA};

    /**
     * describe:Additional items height (is added to standard text item height)
     * 附加item高度（添加到标准文本项高度）
     * 默认值：15
     */
    private static final int ADDITIONAL_ITEM_HEIGHT = 15;

    /**
     * describe:Text size
     * 字体尺寸
     */
    public int TEXT_SIZE;

    /**
     * describe:Top and bottom items offset (to hide that)
     * 顶部和底部item偏移量（隐藏）
     * 默认值：TEXT_SIZE / 5
     */
    private final int ITEM_OFFSET = TEXT_SIZE / 5;

    /**
     * describe:Additional width for items layout
     * item布局的附加宽度
     * 默认值：10
     */
    private static final int ADDITIONAL_ITEMS_SPACE = 10;

    /**
     * describe:Label offset
     * 标签偏移量
     * 默认值：8
     */
    private static final int LABEL_OFFSET = 8;

    /**
     * describe:Left and right padding value
     * 左右边距值
     * 默认值：10
     */
    private static final int PADDING = 10;

    /**
     * describe:Default count of visible items
     * 可见item的默认数
     * 默认值：5
     */
    private static final int DEF_VISIBLE_ITEMS = 5;

    /**
     * DateTimeAdapter对象
     * */
    private DateTimeAdapter adapter = null;
    private int currentItem = 0;

    /**
     * item宽度
     * 默认值：0
     * */
    private int itemsWidth = 0;
    /**
     * label宽度
     * 默认值：0
     * */
    private int labelWidth = 0;

    /**
     * 可见item的数量
     * */
    private int visibleItems = DEF_VISIBLE_ITEMS;

    /**
     * item高度
     * 默认值：0
     * */
    private int itemHeight = 0;

    /**
     * item文字画笔
     * */
    private TextPaint itemsPaint;
    /**
     * 文字画笔
     * */
    private TextPaint valuePaint;

    // Layouts
    private StaticLayout itemsLayout;
    private StaticLayout labelLayout;
    private StaticLayout valueLayout;

    /**
     * 标签文字
     * */
    private String label;
    /**
     * 背景图片
     * */
    private Drawable centerDrawable;

    /**
     * 头部渐变效果GradientDrawable对象
     * */
    private GradientDrawable topShadow;
    /**
     * 底部渐变效果GradientDrawable对象
     * */
    private GradientDrawable bottomShadow;

    /**
     * 判断是否已经滚动
     * */
    private boolean isScrollingPerformed;

    /**
     * ScrollView滑动位置
     * */
    private int scrollingOffset;

    // Scrolling animation
    // 滚动动画
    /**
     * GestureDetector对象 手势识别
     * */
    private GestureDetector gestureDetector;
    /**
     * Scroller对象 处理滚动效果的工具类
     * */
    private Scroller scroller;

    /**
     * 滚动后的纵坐标的值
     * */
    private int lastScrollY;

    /**
     * 是否循环
     * 默认值：false
     * */
    boolean isCyclic = false;

    /**
     * 添加时间改变监听的list集合
     * */
    private List<OnDateChangedListener> changingListeners = new LinkedList<OnDateChangedListener>();
    /**
     * 添加时间视图滚动监听的list集合
     * */
    private List<OnDateScrollListener> scrollingListeners = new LinkedList<OnDateScrollListener>();

    public DateView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initData(ctx);
    }

    public DateView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initData(ctx);
    }

    public DateView(Context ctx) {
        super(ctx);
        initData(ctx);
    }

    /**
     * 初始化数据
     * */
    private void initData(Context ctx) {
        gestureDetector = new GestureDetector(ctx, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(ctx);
    }

    /**
     * 返回日期时间适配器对象
     * @return DateTimeAdapter
     * adapter 日期时间适配器对象
     * */
    public DateTimeAdapter getAdapter() {
        return adapter;
    }

    /**
     * 设置适配器
     * @param adapter
     * adapter 日期时间适配器
     * */
    public void setAdapter(DateTimeAdapter adapter) {
        this.adapter = adapter;
        invalidateLayouts();
        invalidate();
    }

    /**
     * describe:Set the the specified scrolling interpolator
     * 设置指定的滚动插值器
     * @param interpolator
     * Interpolator对象
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.forceFinished(true);
        scroller = new Scroller(getContext(), interpolator);
    }

    /**
     * describe:Gets count of visible items
     * 获取可见item的数量
     * @return
     * visibleItems 可见item数量
     */
    public int getVisibleItems() {
        return visibleItems;
    }

    /**
     * describe:Sets count of visible items
     * 设置可见item数量
     * @param count
     * 可见item数量
     */
    public void setVisibleItems(int count) {
        visibleItems = count;
        invalidate();
    }

    /**
     * describe:Gets label
     * 获取标签文本
     * @return
     * label 标签文本
     */
    public String getLabel() {
        return label;
    }

    /**
     * describe:Sets label
     * 设置标签文本
     * @param newLabel
     * 标签文本内容
     */
    public void setLabel(String newLabel) {
        if (label == null || !label.equals(newLabel)) {
            label = newLabel;
            labelLayout = null;
            invalidate();
        }
    }

    /**
     * describe:Adds wheel changing listener
     * 添加数据改变监听
     *
     * @param listener
     * OnDateChangeListener对象
     */
    public void addChangingListener(OnDateChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * describe:Removes wheel changing listener
     * 从集合移除改变监听
     * @param listener
     * OnDateChangedListener对象
     */
    public void removeChangingListener(OnDateChangedListener listener) {
        changingListeners.remove(listener);
    }

    /**
     * describe:Notifies changing listeners
     * 通知变更监听器
     *
     * @param oldValue
     * 旧的循环值
     *
     * @param newValue
     * 新的循环值
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnDateChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * describe:Adds wheel scrolling listener
     * 向list集合添加循环滚动监听
     * @param listener
     * OnDateScrollListener对象
     */
    public void addScrollingListener(OnDateScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * describe:Removes wheel scrolling listener
     * 从list集合移除循环滚动监听
     * @param listener
     * OnDateScrollListener对象
     */
    public void removeScrollingListener(OnDateScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * describe:Notifies listeners about starting scrolling
     * 通知监听器开始滚动
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnDateScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * describe:Notifies listeners about ending scrolling
     * 通知监听器结束滚动
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnDateScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * describe:Gets current value
     * 获取当前item
     * @return currentItem
     * 当前item
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * describe:Sets the current item. Does nothing when index is wrong.
     * 设置当前item，索引错误时，什么也不做
     *
     * @param index
     * item索引
     *
     * @param animated
     *  动画标志位
     */
    public void setCurrentItem(int index, boolean animated) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return;
        }
        if (index < 0 || index >= adapter.getItemsCount()) {
            if (isCyclic) {
                while (index < 0) {
                    index += adapter.getItemsCount();
                }
                index %= adapter.getItemsCount();
            } else {
                return;
            }
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, SCROLLING_DURATION);
            } else {
                invalidateLayouts();
                int old = currentItem;
                currentItem = index;
                notifyChangingListeners(old, currentItem);
                invalidate();
            }
        }
    }

    /**
     * describe:Sets the current item w/o animation. Does nothing when index is wrong.
     * 设置当前item w/o动画。索引错误时，什么也不做
     *
     * @param index
     * item索引
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * describe:Tests if wheel is cyclic. That means before the 1st item there is shown the last one
     * 是否循环，第一个item之前显示最后一个
     *
     * @return isCircle
     * true循环，false不循环
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * describe:Set wheel cyclic flag
     * 设置循环标志位
     *
     * @param isCyclic
     * true循环，false不循环
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidate();
        invalidateLayouts();
    }

    /**
     * describe:Invalidates layouts
     * 无效布局
     */
    private void invalidateLayouts() {
        itemsLayout = null;
        valueLayout = null;
        scrollingOffset = 0;
    }

    private int[] centerSelectGradientColors = new int[]{0x70222222,
            0x70222222, 0x70EEEEEE};

    /**
     * describe:The bottom gradient colors.
     * /**
     * 底部渐变颜色
     */
    private int[] bottomGradientColors = new int[]{0x333, 0xDDD, 0x333};

    /**
     * describe:The top gradient colors.
     * 顶部渐变颜色
     */
    private int[] topGradientColors = new int[]{0xAAA, 0xFFF, 0xAAA};

    /**
     * describe:The top stroke width.
     * 顶部线宽度
     * 默认值：1
     */
    private int topStrokeWidth = 1;

    /**
     * describe:The top stroke color.
     * 顶部线颜色
     * 默认值：0xFF333333
     */
    private int topStrokeColor = 0xFF333333;

    /**
     * 初始化
     * */
    private void initResourcesIfNecessary() {
        if (itemsPaint == null) {
            itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG );
            itemsPaint.setTextSize(TEXT_SIZE);
        }
        if (valuePaint == null) {
            valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            valuePaint.setTextSize(TEXT_SIZE);
        }

        // Use the default color if you do not set the middle of the selected bar
        // 如果没设置中间的选中条用默认的颜色
        if (centerDrawable == null) {
            centerDrawable = getResources().getDrawable(R.color.bantouming);
        }

        if (topShadow == null) {
            topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }
        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }

        setBackgroundResource(R.color.white);
    }

    /**
     * describe:Calculates desired height for layout
     * 计算所需的布局高度
     *
     * @param layout
     * 布局
     * @return
     * 布局高度
     */
    private int getDesiredHeight(Layout layout) {
        if (layout == null) {
            return 0;
        }
        int desired = getItemHeight() * visibleItems - ITEM_OFFSET * 2 - ADDITIONAL_ITEM_HEIGHT;
        // Check against our minimum height
        // 再次检查最小高度
        desired = Math.max(desired, getSuggestedMinimumHeight());
        return desired;
    }

    /**
     * describe:Returns text item by index
     * 根据索引返回文本项
     *
     * @param index
     *  item索引
     *
     * @return
     *  item或空
     */
    private String getTextItem(int index) {
        if (adapter == null || adapter.getItemsCount() == 0) {
            return null;
        }
        int count = adapter.getItemsCount();
        if ((index < 0 || index >= count) && !isCyclic) {
            return null;
        } else {
            while (index < 0) {
                index = count + index;
            }
        }
        index %= count;
        return adapter.getItem(index);
    }

    /**
     * describe:Builds text depending on current value
     * 根据当前值构建文本
     *
     * @param useCurrentValue
     * 是否使用当前值
     * @return
     * text
     */
    private String buildText(boolean useCurrentValue) {
        StringBuilder itemsText = new StringBuilder();
        int addItems = visibleItems / 2 + 1;
        for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
            if (useCurrentValue || i != currentItem) {
                String text = getTextItem(i);
                if (text != null) {
                    itemsText.append(text);
                }
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n");
            }
        }
        return itemsText.toString();
    }

    /**
     * describe:Returns the max item length that can be present
     * 返回可以存在的最大item长度
     * @return
     * 最大长度
     */
    private int getMaxTextLength() {
        DateTimeAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }
        String maxText = null;
        int addItems = visibleItems / 2;
        for (int i = Math.max(currentItem - addItems, 0); i < Math.min(
                currentItem + visibleItems, adapter.getItemsCount()); i++) {
            String text = adapter.getItem(i);
            if (text != null && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }
        return maxText != null ? maxText.length() : 0;
    }

    /**
     * describe:Returns height of wheel item
     * 返回滚动项目的高度
     *
     * @return
     * item高度
     */
    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        } else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
            itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
            return itemHeight;
        }
        return getHeight() / visibleItems;
    }

    /**
     * describe:Calculates control width and creates text layouts
     * 计算控制宽度并创建文本布局
     *
     * @param widthSize
     * 输入的宽度
     *
     * @param mode
     * mode
     *
     * @return
     * 计算后的控制宽度
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();
        int width = widthSize;
        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            float textWidth = (float) Math.ceil(Layout.getDesiredWidth("0", itemsPaint));
            itemsWidth = (int) (maxLength * textWidth);
        } else {
            itemsWidth = 0;
        }
        itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more
        labelWidth = 0;
        if (label != null && label.length() > 0) {
            labelWidth = (int) Math.ceil(Layout.getDesiredWidth(label, valuePaint));
        }
        boolean recalculate = false;
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
            recalculate = true;
        } else {
            width = itemsWidth + labelWidth + 2 * PADDING;
            if (labelWidth > 0) {
                width += LABEL_OFFSET;
            }

            // Check against our minimum width
            //再次检查最小宽度
            width = Math.max(width, getSuggestedMinimumWidth());
            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }
        if (recalculate) {
            // recalculate width
            // 重新计算宽度
            int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
            if (pureWidth <= 0) {
                itemsWidth = labelWidth = 0;
            }
            if (labelWidth > 0) {
                double newWidthItems = (double) itemsWidth * pureWidth / (itemsWidth + labelWidth);
                itemsWidth = (int) newWidthItems;
                labelWidth = pureWidth - itemsWidth;
            } else {
                itemsWidth = pureWidth + LABEL_OFFSET; // no label
            }
        }
        if (itemsWidth > 0) {
            createLayouts(itemsWidth, labelWidth);
        }
        return width;
    }

    /**
     * describe:Creates layouts
     * 创建layout
     *
     * @param widthItems
     * itemLayout的宽度
     * @param widthLabel
     * labelLayout的宽度
     */
    private void createLayouts(int widthItems, int widthLabel) {
        if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
            itemsLayout = new StaticLayout(buildText(isScrollingPerformed),
                    itemsPaint, widthItems,
                    widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER, 1,
                    ADDITIONAL_ITEM_HEIGHT, false);
        } else {
            itemsLayout.increaseWidthTo(widthItems);
        }
        if (!isScrollingPerformed && (valueLayout == null || valueLayout.getWidth() > widthItems)) {
            String text = getAdapter() != null ? getAdapter().getItem(
                    currentItem) : null;
            valueLayout = new StaticLayout(text != null ? text : "",
                    valuePaint, widthItems,
                    widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
                            : Layout.Alignment.ALIGN_CENTER, 1,
                    ADDITIONAL_ITEM_HEIGHT, false);
        } else if (isScrollingPerformed) {
            valueLayout = null;
        } else {
            valueLayout.increaseWidthTo(widthItems);
        }
        if (widthLabel > 0) {
            if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
                labelLayout = new StaticLayout(label, valuePaint, widthLabel,
                        Layout.Alignment.ALIGN_NORMAL, 1, ADDITIONAL_ITEM_HEIGHT, false);
            } else {
                labelLayout.increaseWidthTo(widthLabel);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = calculateLayoutWidth(widthSize, widthMode);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemsLayout == null) {
            if (itemsWidth == 0) {
                calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            } else {
                createLayouts(itemsWidth, labelWidth);
            }
        }
        if (itemsWidth > 0) {
            canvas.save();
            // Skip padding space and hide a part of top and bottom items
            // 跳过边缘空间并隐藏顶部和底部item的一部分
            canvas.translate(PADDING, -ITEM_OFFSET);
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }
        drawCenterRect(canvas);
    }

    /**
     * describe:Draws shadows on top and bottom of control
     * 在控件的顶部和底部绘制阴影
     *
     * @param canvas
     * Canvas对象
     */
    private void drawShadows(Canvas canvas) {
        topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
        topShadow.draw(canvas);
        bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems, getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    /**
     * describe:Draws value and label layout
     * 绘制值和标签布局
     *
     * @param canvas
     * Canvas对象
     */
    private void drawValue(Canvas canvas) {
        valuePaint.setColor(VALUE_TEXT_COLOR);
        valuePaint.drawableState = getDrawableState();
        Rect bounds = new Rect();
        itemsLayout.getLineBounds(visibleItems / 2, bounds);
        // draw label
        // 绘制标签
        if (labelLayout != null) {
            canvas.save();
            canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
            labelLayout.draw(canvas);
            canvas.restore();
        }
        // draw current value
        // 绘制当前值
        if (valueLayout != null) {
            canvas.save();
            canvas.translate(0, bounds.top + scrollingOffset);
            valueLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * describe:Draws items
     * 绘制item
     *
     * @param canvas
     * Canvas对象
     */
    private void drawItems(Canvas canvas) {
        canvas.save();
        int top = itemsLayout.getLineTop(1);
        canvas.translate(0, -top + scrollingOffset);
        itemsPaint.setColor(ITEMS_TEXT_COLOR);
        itemsPaint.drawableState = getDrawableState();
        itemsLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * describe:Draws rect for current value
     * 根据当前值绘制矩形
     *
     * @param canvas
     * Canvas对象
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2;
        int offset = getItemHeight() / 2;
        centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        centerDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DateTimeAdapter adapter = getAdapter();
        if (adapter == null) {
            return true;
        }
        if (!gestureDetector.onTouchEvent(event)
                && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }
        return true;
    }

    /**
     * describe:Scrolls the wheel
     * 滚动轮子
     *
     * @param delta
     * 滚动距离
     */
    private void doScroll(int delta) {
        scrollingOffset += delta;
        int count = scrollingOffset / getItemHeight();
        int pos = currentItem - count;
        if (isCyclic && adapter.getItemsCount() > 0) {
            // fix position by rotating
            // 通过旋转固定位置
            while (pos < 0) {
                pos += adapter.getItemsCount();
            }
            pos %= adapter.getItemsCount();
        } else if (isScrollingPerformed) {
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= adapter.getItemsCount()) {
                count = currentItem - adapter.getItemsCount() + 1;
                pos = adapter.getItemsCount() - 1;
            }
        } else {
            // fix position
            // 固定位置
            pos = Math.max(pos, 0);
            pos = Math.min(pos, adapter.getItemsCount() - 1);
        }
        int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }
        // update offset
        // 更新偏移量
        scrollingOffset = offset - count * getItemHeight();
        if (scrollingOffset > getHeight()) {
            scrollingOffset = scrollingOffset % getHeight() + getHeight();
        }
    }

    /**
     * describe:gesture listener
     * 手势监听
     * */
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
        public boolean onDown(MotionEvent e) {
            if (isScrollingPerformed) {
                scroller.forceFinished(true);
                clearMessages();
                return true;
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            startScrolling();
            doScroll((int) -distanceY);
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            lastScrollY = currentItem * getItemHeight() + scrollingOffset;
            int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount() * getItemHeight();
            int minY = isCyclic ? -maxY : 0;
            scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    /**
     * Handler发送的常量值
     * */
    private final int MESSAGE_SCROLL = 0;
    /**
     * Handler发送的常量值
     * */
    private final int MESSAGE_JUSTIFY = 1;

    /**
     * describe:Set next message to queue. Clears queue before.
     * 先清除之前的队列，然后设置下一个消息队列
     * @param message
     * 常量值 可选：MESSAGE_SCROLL、MESSAGE_JUSTIFY
     */
    private void setNextMessage(int message) {
        clearMessages();
        animationHandler.sendEmptyMessage(message);
    }

    /**
     * describe:Clears messages from queue
     * 从队列中清除message
     */
    private void clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.removeMessages(MESSAGE_JUSTIFY);
    }

    /**
     * describe:animation handler
     * 动画handler
     * */
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currY = scroller.getCurrY();
            int delta = lastScrollY - currY;
            lastScrollY = currY;
            if (delta != 0) {
                doScroll(delta);
            }
            // scrolling is not finished when it comes to final Y.so, finish it manually
            // 当滚动到Y时，滚动并没有结束，在这里，手动完成
            if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller.getFinalY();
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else if (msg.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }
        }
    };

    /**
     * describe:Justifies wheel
     * 调整轮子
     */
    private void justify() {
        if (adapter == null) {
            return;
        }
        lastScrollY = 0;
        int offset = scrollingOffset;
        int itemHeight = getItemHeight();
        boolean needToIncrease = offset > 0 ? currentItem < adapter.getItemsCount() : currentItem > 0;
        if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
            if (offset < 0)
                offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
            else
                offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
        }
        if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
            scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
            setNextMessage(MESSAGE_JUSTIFY);
        } else {
            finishScrolling();
        }
    }

    /**
     * describe:Starts scrolling
     * 开始滚动
     */
    private void startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }
    }

    /**
     * describe:Finishes scrolling
     * 完成滚动
     */
    void finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd();
            isScrollingPerformed = false;
        }
        invalidateLayouts();
        invalidate();
    }

    /**
     * describe:Scroll the wheel
     * 滚动轮子
     *
     * @param itemsToScroll
     * 滚动的item
     *
     * @param time
     * 滚动时间
     */
    public void scroll(int itemsToScroll, int time) {
        scroller.forceFinished(true);
        lastScrollY = scrollingOffset;
        int offset = itemsToScroll * getItemHeight();
        scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
        setNextMessage(MESSAGE_SCROLL);
        startScrolling();
    }
}