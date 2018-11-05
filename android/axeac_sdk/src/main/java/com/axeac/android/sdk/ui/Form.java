package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.button.SystemButton;
import com.axeac.android.sdk.ui.container.BoxLayoutX;
import com.axeac.android.sdk.ui.container.BoxLayoutY;
import com.axeac.android.sdk.ui.container.KHMAP5Layout;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Form
 * 表单
 * @author axeac
 * @version 1.0.0
 */
public class Form extends Component {

    private KHMAP5Layout layoutView;
    /**
     * 是否可滑动
     * <br>默认值为true可滑动
     * */
    private boolean scrollLayoutFlag = true;

    /**
     * 表单标题
     * */
    private String title;

    /**
     * 设置当前页中的字体，否则使用系统设置的字体
     * */
    private String defaultFont;

    /**
     * 边距
     * <br>默认值为0
     * */
    private int padding = 0;

    /**
     * 组件之间间距
     * <br>默认值为6
     * */
    private int itemPadding = 0;

    /**
     * 当底部操作按钮数量为0时 bottom_layout是否显示
     * @param home
     * 可选值 true|false
     * */
    public void setHome(boolean home) {
        this.home = home;
    }

    /**
     * 设置提醒消息
     * <br>默认值为空
     * */
    private String message = "";

    /**
     * 表单创建时间
     * */
    private String buildDate = "";

    /**
     * 页面发送请求的源文本，用于刷新时使用
     * */
    private String lastMEIP;

    /**
     * 是否清除布局，true时设置数据时removeAll
     * */
    private boolean clear = false;
    /**
     * 当底部操作按钮数量为0时 bottom_layout是否显示
     * */
    private boolean home = false;

    /**
     * 存储操作按钮id的list集合
     * */
    private List<String> btns = new ArrayList<String>();

    /**
     * 存储操作按钮id的list集合
     * */
    private List<String> btnsAll = new ArrayList<String>();

    /**
     * Form.add的所有组件的Map集合
     * */
    private Map<String, Component> childs = new LinkedHashMap<String, Component>();

    /**
     * 设置表单标题文本
     * @param title
     * 表单标题文本
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 返回表单标题文本
     * @return
     * 表单标题文本
     * */
    public String getTitle() {
        return title;
    }

    /**
     * 设置布局（横向、纵向）
     * @param layout
     * 可选值 BoxLayoutX|BoxLayoutY
     * */
    public void setLayout(String layout) {
        if (layout != null && !"".equals(layout)) {
            if ("BoxLayoutY".toUpperCase().equals(layout.toUpperCase())) {
                this.layoutView = new BoxLayoutY(this.ctx);
            } else if ("BoxLayoutX".toUpperCase().equals(layout.toUpperCase())) {
                this.layoutView = new BoxLayoutX(this.ctx);
            }
        }
    }

    /**
     * 设置当前页中的字体，否则使用系统字体
     * @param defaultFont
     * 字体
     * */
    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

    /**
     * 设置边距
     * @param padding
     * 边距 默认值：0
     * */
    public void setPadding(String padding) {
        this.padding = Integer.parseInt(padding);
    }

    /**
     * 设置各组件之间间距
     * @param itemPadding
     * 间距 默认值：6
     * */
    public void setItemPadding(String itemPadding) {
        this.itemPadding = Integer.parseInt(itemPadding);
    }

    /**
     * 设置提醒消息
     * @param message
     * 提醒消息  默认值为空
     * */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 设置表单创建时间
     * @param buildDate
     * 表单创建时间
     * */
    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    /**
     * 返回表单创建时间
     * @return
     * 表单创建时间
     * */
    public String getBuildDate() {
        return buildDate;
    }

    /**
     * 设置页面发送请求的源文本，用于刷新时使用
     * @param lastMEIP
     * 页面发送请求时的源文本
     * */
    public void setLastMEIP(String lastMEIP) {
        this.lastMEIP = lastMEIP;
    }

    /**
     * 设置是否清除布局
     * @param clear
     * true为清除，false为不清除
     * */
    public void setClear(boolean clear) {
        this.clear = clear;
    }

    /**
     * 判断是否清除布局
     * */
    public boolean isClear() {
        return clear;
    }

    /**
     * 判断是否可滑动
     * */
    public boolean isScrollLayoutFlag() {
        return scrollLayoutFlag;
    }

    public Form(Activity ctx) {
        super(ctx);
        this.layoutView = new BoxLayoutY(ctx);
        this.alpha = 100;
    }

    private View parentView;

    public Form(View parentView, Activity ctx) {
        super(ctx);
        this.parentView = parentView;
        this.layoutView = new BoxLayoutY(ctx);
        this.alpha = 100;
    }

    /**
     * 添加组件
     * @param compId
     * 组件id
     * @param isReturn
     * 是否可返回
     * */
    public void add(String compId, boolean isReturn) {
        if (StaticObject.ComponentMap.get(compId) != null && StaticObject.ComponentMap.get(compId).addable) {
            this.childs.put(compId, StaticObject.ComponentMap.get(compId));
            StaticObject.ComponentMap.get(compId).addable = false;
            if (isReturn) {
                StaticObject.ReturnComponentMap.put(compId, StaticObject.ComponentMap.get(compId));
            }
            if (StaticObject.ComponentMap.get(compId) == null) {
                return;
            }
            if (!StaticObject.ComponentMap.get(compId).visiable) {
                StaticObject.ComponentMap.get(compId).getView().setVisibility(View.GONE);
            }
            String className = StaticObject.ComponentMap.get(compId).getClass().getName();
            className = className.substring(className.lastIndexOf(".") + 1);
            this.layoutView.addViewIn(StaticObject.ComponentMap.get(compId));
            int childCount = ((LinearLayout) layoutView.getLayout()).getChildCount();
            if (this.itemPadding != 0 && childCount > 1) { //间隔设置
                if (StaticObject.ComponentMap.get(compId) != null && StaticObject.ComponentMap.get(compId).getView() != null) {
                    View v = StaticObject.ComponentMap.get(compId).getView();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
                    if (layoutView.getLayout() instanceof BoxLayoutY) {
                        params.setMargins(0, itemPadding, 0, 0);
                    } else {
                        params.setMargins(itemPadding, 0, 0, 0);
                    }
                    v.setLayoutParams(params);
                }
            }
        }
    }

    /**
     * 返回提醒消息
     * @return
     * 提醒消息
     * */
    public String getMessage() {
        return message;
    }

    /**
     * 添加底部操作菜单，底部菜单中间为Home键，按后出现彩虹桥或九宫格的导航。其他按钮在旁边，最大显示4个，
     * 多的时候最右边的按钮显示更多，点击后出现其他按钮列表。添加的按钮为Operation、SystemButton。
     * @param compId
     * 组件id
     * @param isReturn
     * 是否可返回
     * */
    public void addButton(String compId, boolean isReturn) {
        this.childs.put(compId, StaticObject.ComponentMap.get(compId));
        if (isReturn) {
            StaticObject.ReturnComponentMap.put(compId, StaticObject.ComponentMap.get(compId));
        }
        this.btnsAll.add(compId);
    }

    /**
     * 设置按钮
     * */
    public void setButton() {

        btns.clear();
        for (int i = 0; i < this.btnsAll.size(); i++) {
            if (StaticObject.ComponentMap.get(this.btnsAll.get(i)) == null)
                continue;
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(this.btnsAll.get(i));
            if (o.visiable) {
                btns.add(this.btnsAll.get(i));
            }
        }

        RelativeLayout btnsLayout = (RelativeLayout) ctx.findViewById(R.id.layout_bottom);
        btnsLayout.setVisibility(View.VISIBLE);
        if (btns.size() == 0) {
            if (!this.home)
                btnsLayout.setVisibility(View.GONE);
        } else if (btns.size() == 1) {
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(btns.get(0));
            btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three_linear).setVisibility(View.VISIBLE);
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_three);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_three_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_three_text);
                o.setImageAndText(layout, btn, text);
            }
        } else if (btns.size() == 2) {

            btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four_linear).setVisibility(View.VISIBLE);
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(btns.get(0));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_second);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_second_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_second_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(1));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_four);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_four_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_four_text);
                o.setImageAndText(layout, btn, text);
            }
        } else if (btns.size() == 3) {
            btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_first_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five_linear).setVisibility(View.VISIBLE);
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(btns.get(0));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_first);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_first_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_first_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(1));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_three);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_three_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_three_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(2));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_five);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_five_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_five_text);
                o.setImageAndText(layout, btn, text);
            }
        } else if (btns.size() == 4) {
            btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.INVISIBLE);
            btnsLayout.findViewById(R.id.menu_item_first_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four_linear).setVisibility(View.VISIBLE);
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(btns.get(0));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_first);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_first_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_first_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(1));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_second);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_second_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_second_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(2));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_three);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_three_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_three_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(3));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_four);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_four_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_four_text);
                o.setImageAndText(layout, btn, text);
            }
        } else if (btns.size() == 5) {
            btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_first_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_second_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_three_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_four_linear).setVisibility(View.VISIBLE);
            btnsLayout.findViewById(R.id.menu_item_five_linear).setVisibility(View.VISIBLE);
            SystemButton o = (SystemButton) StaticObject.ComponentMap.get(btns.get(0));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_first);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_first_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_first_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(1));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_second);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_second_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_second_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(2));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_three);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_three_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_three_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(3));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_four);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_four_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_four_text);
                o.setImageAndText(layout, btn, text);
            }
            o = (SystemButton) StaticObject.ComponentMap.get(btns.get(4));
            if (o != null) {
                RelativeLayout layout = (RelativeLayout) btnsLayout.findViewById(R.id.menu_item_five);
                ImageView btn = (ImageView) btnsLayout.findViewById(R.id.menu_item_five_btn);
                TextView text = (TextView) btnsLayout.findViewById(R.id.menu_item_five_text);
                o.setImageAndText(layout, btn, text);
            }
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (this.padding != 0)
            this.layoutView.getLayout().setPadding(padding, padding, padding, padding);

        int r = Integer.parseInt(bgColor.substring(0, 3));
        int g = Integer.parseInt(bgColor.substring(3, 6));
        int b = Integer.parseInt(bgColor.substring(6, 9));
        final ScrollView compLayout;
        if (parentView != null) {
            compLayout = (ScrollView) parentView.findViewById(R.id.comp_layout);

        } else {
            compLayout = (ScrollView) ctx.findViewById(R.id.comp_layout);
        }
        if (compLayout.getVisibility() == View.VISIBLE) {
            if (this.bgImage != null) {
                try {
//                    BitmapDrawable draw = new BitmapDrawable(ctx.getResources(), BitmapShared.get(this.bgImage, ctx));
//                    compLayout.setBackgroundDrawable(draw);
                    Glide.with(ctx)
                            .load(StaticObject.getImageUrl(bgImage))
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    BitmapDrawable draw = new BitmapDrawable(ctx.getResources(), resource);
                                    compLayout.setBackgroundDrawable(draw);
                                }
                            });

                } catch (Exception e) {
                    compLayout.setBackgroundColor(Color.rgb(r, g, b));
                }
            } else {
                compLayout.setBackgroundColor(Color.rgb(r, g, b));
            }

            compLayout.getBackground().setAlpha((int) (255 * ((float) this.alpha / 100)));
        }
    }

    @Override
    public View getView() {
        return this.layoutView.getLayout();
    }

    @Override
    public String getValue() {
        return null;
    }

    /**
     * 重绘界面
     * */
    @Override
    public void repaint() {
        this.clear();
        for (String key : this.childs.keySet()) {
            this.layoutView.addViewIn(this.childs.get(key));

            int childCount = ((LinearLayout) layoutView.getLayout()).getChildCount();
            if (this.itemPadding != 0 && childCount > 1) {
                if (this.childs.get(key) != null && this.childs.get(key).getView() != null) {
                    View v = this.childs.get(key).getView();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
                    if (layoutView.getLayout() instanceof BoxLayoutY) {
                        params.setMargins(0, itemPadding, 0, 0);
                    } else {
                        params.setMargins(itemPadding, 0, 0, 0);
                    }
                    v.setLayoutParams(params);
                }
            }
//			if(this.itemPadding != 0){
//				if(this.childs.get(key) != null && this.childs.get(key).getView() != null){
//					this.childs.get(key).getView().setPadding(itemPadding, itemPadding, itemPadding, itemPadding);
//					KLog.e("Repaint");
//				}
//			}
        }
    }

    /**
     * 移除某组件
     * @param compId
     * 组件id
     */
    public void remove(String compId) {
        this.layoutView.removeAll(StaticObject.ComponentMap.get(compId).getView());
    }

    /**
     * 清空组件
     * */
    public void clear() {
        if (this.layoutView != null) {
            this.layoutView.removeAll();
        }
        RelativeLayout btnsLayout = (RelativeLayout) ctx.findViewById(R.id.layout_bottom);
        btnsLayout.findViewById(R.id.menu_item_first).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_second).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_three).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_four).setVisibility(View.INVISIBLE);
        btnsLayout.findViewById(R.id.menu_item_five).setVisibility(View.INVISIBLE);
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