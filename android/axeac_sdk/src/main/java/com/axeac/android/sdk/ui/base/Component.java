package com.axeac.android.sdk.ui.base;

import android.app.Activity;

import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:All components super class,contains a variety of basic attributes
 *
 * 所有组件超类，包含各种基本属性
 * @author axeac
 * @version 1.0.0
 * */
public abstract class Component implements KHMAP5View {

    protected Activity ctx;

    public Component(Activity ctx) {
        this.ctx = ctx;
    }


    /*
     * describe:Component ID  Default: null
     * 组件ID
     * <br>默认值：null
     */
    protected String Id;
    /*
     * describe:Set and read the actual display of the width of the value, -1 said not set
     *          In addition to the value, you can set the percentage, such as xx.width = 20%
     *          Default: -1
     *
     *设置和读取实际显示的宽度的值，-1代表未设置
     * <br>除了数值外，可以设置百分比，例如xx.width=20%
     * <br>默认值：-1
     */
    public String width = "-1";
    /*
     * describe:Set and read the actual display of the height of the value, -1 said not set
     *          Default: -1
     *
     * 设置和读取实际显示的高度的值，-1代表未设置
     * <br>默认值：-1
     */
    protected int height = -1;
    /*
     * describe:Specify the style number, optional, not specified as the default style
     *          style = bold, italic; color: 255255255;
     *          Default:null
     *
     * 指定样式编号，可选项，不指定为默认样式style=bold,italic;color:255255255;
     * <br>默认值：style=bold,italic;color:255255255;
     */
    protected String styleId;
    /*
     * describe：Sets the background color value of the component in RRRGGGBBB format,
     *          for example XX.bgColor = 255255255
     *          Default:255255255
     *
     *  设置组件的背景颜色值，格式为RRRGGGBBB，例如XX.bgColor = 255255255
     * <br>默认值：255255255
     */
    protected String bgColor = "255255255";
    /*
     * describe:Local or network pictures, formatted as local image name
     *          Default:null
     *
     * 本地或网络上图片，格式为本地图片名
     * <br>默认值：null
     */
    protected String bgImage;
    /*
     * describe:Set the background transparency, 100 is opaque, 0 is completely transparent
     *          Default:100
     *
     * 设置背景透明度，100为不透明、0为完全透明
     *<br>默认值：100
     */
    protected int alpha = 100;
    /*
     * describe:Format is size: 14px; family: Arial; style = bold, italic; color: 255255255
     *          Default:size:14px;family:宋体;style=bold,italic;color:051051051;
     *
     * 格式为size:14px;family:宋体;style=bold,italic;color:255255255;
     * <br>默认值：size:14px;family:宋体;style=bold,italic;color:051051051;
     */
    protected String font = "size:15px;family:宋体;style=bold,italic;color:136136136;";
    /*
     * describe:Valid value, NOTNULL, between: xx and yy, @ this. Attribute value> = <value,
     *          @ component name. Attribute value> = <value, include: value 1 || value 2,
     *          uninclude: value 1 || value 2, Regex: regular expression
     *          Default:null
     *
     * 有效性标识，NOTNULL、between: xx and yy、@this.属性值>=<值、@组件名.属性值>=<值、
     * <br>include:值1||值2、uninclude:值1||值2、regex：正则表达式
     * <br>默认值：null
     */
    public String filter;
    /*
     * describe:Visible defaults to true, when the component is true, the component is displayed,
     *          false when the component is hidden
     *
     * 是否隐藏组件，为true时，组件显示，为false时，组件隐藏
     * <br>默认值：true
     */
    public boolean visiable = true;
    /*
     * describe:filterMsg default is empty, when the filter fails to pass, if the filterMsg is empty,
     *          then the prompt prompt text, if filterMsg is not empty, then prompted filterMsg text
     *
     * 当filter验证没通过时，如果filterMsg为空，则提示现在的提示文本，
     * <br>如果filterMsg不为空，则提示filterMsg的文本
     * <br>默认值：空
     */
    public String filterMsg = "";

    protected boolean buildable = true;

    /**
     * 设置组件ID
     * @param id
     * 组件ID
     * */
    public void setId(String id) {
        Id = id;
    }

    /**
     * 设置控件宽度
     * @param width
     * 控件宽度
     * */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * 设置控件高度
     * @param height
     * 控件高度
     * */
    public void setHeight(String height) {
        if (height.endsWith("%")) {
            this.height = DensityUtil.dip2px(ctx, Integer.parseInt(height.substring(0, height.indexOf("%"))) * StaticObject.deviceHeight / 100);
        } else {
            this.height = DensityUtil.dip2px(ctx, Integer.parseInt(height));
        }
    }

    /**
     * 设置控件样式编号
     * @param styleId
     * 样式编号，默认样式style=bold,italic;color:255255255;
     * */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * 设置控件背景颜色
     * @param bgColor
     * 控件背景颜色，格式为RRRGGGBBB
     * */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * 设置控件背景图片
     * @param bgImage
     * 本地或网络上图片，格式为本地图片名
     * */
    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    /**
     * 设置控件背景透明度
     * @param alpha
     * 透明度，100为不透明、0为完全透明
     * */
    public void setAlpha(String alpha) {
        this.alpha = Integer.parseInt(alpha);
    }

    /**
     * 设置字体
     * @param font
     * 格式为size:14px;family:宋体;style=bold,italic;color:255255255;
     * */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * 设置有效性标识
     * @param filter
     * 有效性标识，NOTNULL、between: xx and yy、
     * <br>@this.属性值>=<值、@组件名.属性值>=<值、include:值1||值2、uninclude:值1||值2、regex：正则表达式
     * */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * 设置控件是否显示
     * @param visiable
     * true为显示，false为隐藏，默认为true
     * */
    public void setVisiable(String visiable) {
        this.visiable = Boolean.parseBoolean(visiable);
    }

    /**
     * 设置提示文本内容
     * @param filterMsg
     * 当filter验证没通过时，如果filterMsg为空，则提示现在的提示文本，
     * <br>如果filterMsg不为空，则提示filterMsg的文本
     * <br>默认为空
     */
    public void setFilterMsg(String filterMsg) {
        this.filterMsg = filterMsg;
    }

    /**
     * describe:Non-DFS attribute, set the height property automatically set the control width value
     *          (according to the contents of the current control), if not set the weight attribute,
     *          according to the actual situation of the component to return to the value of the width.
     *
     * 非DFS属性，在设置了height属性时自动计算控件宽度值（根据当前控件的内容），如果没有设置
     * <br>weight属性，则根据组件自己实际情况返回宽度值。
     */
    public int calWidth;
    /**
     * describe:Non-DFS attribute, set the width of the property automatically set the control height
     *          value (according to the contents of the current control), if not set the height attribute,
     *          according to the actual situation of the component to return to the height value.
     *
     * 非DFS属性，在设置了width属性时自动计算控件高度值（根据当前控件的内容），如果没有设置
     * <br>height属性，则根据组件自己实际情况返回高度值。
     */
    public int calHeight;
    /**
     * describe:A non-DFS attribute that identifies whether the component is returned
     *          Default:false
     *
     * 非DFS属性，用来标识该组件是否返回
     * <br>默认值：false
     */
    public boolean returnable;

    /**
     * 非DFS属性，用来标识该组件是否显示
     * <br>默认值：true
     */
    public boolean addable = true;

    /**
     * describe:When the width and height change, call the parent class (container or form) repaint
     *          method, the parent class to re-set the width of the component, according to re-generate
     *          their own width and height, when width and height does not change, only re-set their
     *          own pages. This method is no longer called in the DFS script
     *
     * 当宽高改变时，调用父类（容器或表单）repaint方法，父类重新给组件设置宽高，根据重新生成自身的
     * <br>宽度和高度。宽高不改变时，只重新设置自己页面。此方法不再DFS脚本里调用
     */
    public abstract void repaint();

    /*
     * describe:The DFS settings form starts, and the setting properties do not work at this time.
     *          This method is used when parsing DFS scripts
     *
     * DFS设置表单开始，此时设置属性不起作用，此方法在解析DFS脚本时使用
     */
    public abstract void starting();

    /*
     * describe:DFS set the form to end, call the repaint method, all the properties set to work,
     *          this method is used in parsing DFS script.
     *
     * DFS设置表单结束，调用repaint方法，所有设置的属性都起作用，此方法在解析DFS脚本时使用。
     */
    public abstract void end();

}