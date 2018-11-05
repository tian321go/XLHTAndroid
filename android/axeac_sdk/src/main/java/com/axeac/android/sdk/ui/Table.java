package com.axeac.android.sdk.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.OptionAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.base.ObservableScrollView;
import com.axeac.android.sdk.ui.base.ScrollViewListener;
import com.axeac.android.sdk.ui.base.SyncHorizontalScrollView;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.FilterUtils;
import com.axeac.android.sdk.utils.StaticObject;
import com.axeac.android.sdk.utils.WaterMarkImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 表格控件
 * @author axeac
 * @version 1.0.0
 */
public class Table extends Component implements ScrollViewListener {

    /**
     * 表格高度
     * */
    private int lineHeight;

    private View table;
    private RelativeLayout viewTitle;
    private View viewLeft;
    private View viewRight;
    private TextView viewTitleTxt;
    private TextView viewAddBtn;
    private TextView viewDelBtn;
    private RelativeLayout viewList;
    private RelativeLayout viewListTitles;
    private ScrollView viewListDatas;
    private RelativeLayout viewLeftTitles;
    private ObservableScrollView scrollViewLeft;
    private ObservableScrollView scrollViewRight;
    private SyncHorizontalScrollView scrollViewRightTitles;
    private SyncHorizontalScrollView scrollViewRightDatas;
    private ImageButton navFirst;
    private ImageButton navLast;
    private ImageButton navPrev;
    private ImageButton navNext;
    private EditText navNumber;
    private TextView navTotPage;
    private Button navGo;
    private TextView table_nav_allnext;
    /***/
    private List<Integer> lengths = new ArrayList<Integer>();
    /***/
    private List<Integer> lengths_ = new ArrayList<Integer>();

    /**
     * 存储表格标题的list集合
     * */
    private List<String> titlesArray = new ArrayList<String>();
    /***/
    private List<List<String>> dataArray = new ArrayList<List<String>>();
    /**
     * 表单id
     * */
    private String formId = "";
    /**
     * 组件id
     * */
    private String compId = "";
    /**
     * 表格总页数
     * */
    private int totPage = 1;

    /**
     * 表格上方标题
     * */
    private String title;
    /**
     * 字体和字号
     * */
    private String style;
    /**
     * 字体格式（居左，居中，居右）
     * */
    private String grivaty = "center";
    /**
     * 点击时的字符串
     * */
    private String click;
    /**
     * 当横向滚动为false时，自动计算所有列的宽度之和，按百分比重新划分，当有行级按钮时，显示总宽度减去行级
     * <br>按钮的宽度。为true时，则按照指定的宽度值，超出时以滚动形式显示。
     * <br>默认值为false
     * */
    private boolean scroll = false;
    /**
     * 是否显示导航条
     * <br>默认值为true
     * */
    private boolean navigator = true;
    /**
     * 第一列开始固定的列数，0为不固定，当滚动时起作用
     * <br>默认值为0
     * */
    private int fixCol = 0;
    /**
     * 是否可以换行，在设置滚动属性后起作用
     * <br>默认值为false
     * */
    private boolean nowarp = false;
    /**
     * 记录总数，用于计算总页数
     * <br>默认值为0
     * */
    private int count = 0;
    /**
     * 每页显示的表格行数
     * <br>默认值为20
     * */
    private int pageCount = 20;
    /**
     * 记录当前传送数据集合的起始页，用于缓存多页数据的
     * <br>默认值为1
     * */
    private int startPage = 1;
    /**
     * 设置每次获取缓存页数，默认为5，每次请求的页数
     * <br>默认值为5
     * */
    private int cachePage = 5;
    /**
     * 当前页
     * <br>默认值为1
     * */
    private int page = 1;

    private String titleBgColor = "000000000";
    /**
     * 奇数行背景色
     * */
    private String rowBgColor = "255255255";
    /**
     * 偶数行背景色
     * */
    private String rowBgColor2 = "220237240";
    /**
     * 奇数行中字体颜色值
     * */
    private String rowFontColor = "000000000";
    /**
     * 偶数行字体颜色值
     * */
    private String rowFontColor2 = "000000000";

    /**
     * 存储行按钮的list集合
     * */
    private List<String> rowBtns = new ArrayList<String>();
    /**
     * 存储长按某行是出现的按钮的list集合
     * */
    private List<String> optBtns = new ArrayList<String>();

    /**
     * 存储指定列数中的字体颜色的Map集合
     * */
    private Map<Integer, String> colFontColor = new HashMap<Integer, String>();
    /**
     * 存储指定列数中的背景颜色的Map集合
     * */
    private Map<Integer, String> colBgColor = new HashMap<Integer, String>();

    /***/
    private Map<Integer, String> cellFontColor = new HashMap<Integer, String>();
    /***/
    private Map<Integer, String> cellBgColor = new HashMap<Integer, String>();

    /**
     * 是否可编辑
     * <br>默认值为false
     * */
    private boolean editor = false;
    /***/
    private List<Integer> editorCols = new ArrayList<Integer>();
    /***/
    private Map<Integer, String> editorColsFilter = new HashMap<Integer, String>();
    /***/
    private Map<Integer, String> cellClick = new HashMap<Integer, String>();

    /**
     * 数据数量
     * */
    private int dataCount;
    /***/
    private List<Integer> delRows = new ArrayList<Integer>();
    /***/
    private List<String> addRows = new ArrayList<String>();
    /***/
    private Map<Integer, Boolean> choiceMap = new LinkedHashMap<Integer, Boolean>();
    /***/
    private Map<Integer, ArrayList<EditText>> editColViews = new LinkedHashMap<Integer, ArrayList<EditText>>();
    /***/
    private Map<Integer, ArrayList<EditText>> addRowsViews = new LinkedHashMap<Integer, ArrayList<EditText>>();
    /***/
    private Map<Integer, ArrayList<LinearLayout>> tableLineViews = new LinkedHashMap<Integer, ArrayList<LinearLayout>>();

    /**
     * 加载线程Thread对象
     * */
    private Thread loadingThread;
    /**
     * 加载对话框ProgressDialog对象
     * */
    private ProgressDialog loadingDialog;

    /***/
    private int minHeight;

    public Table(Activity ctx) {
        super(ctx);
        this.minHeight = DensityUtil.dip2px(ctx, 48);
        this.lineHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
        this.returnable = false;
        table = LayoutInflater.from(ctx).inflate(R.layout.axeac_table, null);
        viewTitle = table.findViewById(R.id.table_title);
        viewLeft = table.findViewById(R.id.table_left);
        viewRight = table.findViewById(R.id.table_right);
        viewTitleTxt = (TextView) table.findViewById(R.id.table_title_txt);
        viewAddBtn = (TextView) table.findViewById(R.id.table_add_btn);
        viewDelBtn = (TextView) table.findViewById(R.id.table_del_btn);
        viewList = (RelativeLayout) table.findViewById(R.id.table_list);
        viewListTitles = (RelativeLayout) table
                .findViewById(R.id.table_list_titles);
        viewListDatas = (ScrollView) table.findViewById(R.id.table_list_datas);
        viewLeftTitles = (RelativeLayout) table
                .findViewById(R.id.table_left_titles);
        scrollViewLeft = (ObservableScrollView) table
                .findViewById(R.id.table_left_datas);
        scrollViewLeft.setScrollViewListener(this);
        scrollViewRight = (ObservableScrollView) table
                .findViewById(R.id.table_right_datas);
        scrollViewRight.setScrollViewListener(this);
        scrollViewRightTitles = (SyncHorizontalScrollView) table
                .findViewById(R.id.table_right_titles_view);
        scrollViewRightDatas = (SyncHorizontalScrollView) table
                .findViewById(R.id.table_right_datas_view);
        scrollViewRightTitles.setScrollView(scrollViewRightDatas);
        scrollViewRightDatas.setScrollView(scrollViewRightTitles);
        table_nav_allnext = (TextView) this.table
                .findViewById(R.id.table_nav_allnext);
        table_nav_allnext.setOnClickListener(navBtnOnClickListener());
        initLoadingDialog();
    }

    /**
     * 初始化加载对话框
     * */
    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(ctx);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(ctx.getString(R.string.axeac_login_loading));
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (loadingThread != null
                                && !loadingThread.isInterrupted()) {
                            loadingThread.interrupt();
                        }
                    }
                });
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    /**
     * 设置表单id
     * @param formId
     * 表单id
     * */
    public void setFormId(String formId) {
        this.formId = formId;
    }

    /**
     * 设置组件id
     * @param compId
     * 组件id
     * */
    public void setCompId(String compId) {
        this.compId = compId;
    }

    /**
     * 设置表格上方标题
     * @param title
     * 表格上方标题
     * */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * 设置表格标题字体
     * @param style
     * 标题字体
     * */
    public void setStyle(String style) {
        this.style = style;
    }
    /**
     * 设置文字内容格式（居左，居中，居右）
     * @param grivaty
     * 格式
     * */
    public void setGrivaty(String grivaty){
        this.grivaty = grivaty;
    }
    /**
     * 设置是否可横向滚动
     * @param scroll
     * 可选值 true\false
     * */
    public void setScroll(String scroll) {
        this.scroll = Boolean.parseBoolean(scroll);
    }

    /**
     * 设置是否显示导航条
     * @param navigator
     * 可选值 true\false
     * */
    public void setNavigator(String navigator) {
        this.navigator = Boolean.parseBoolean(navigator);
    }

    /**
     * 设置第一列开始固定的列数
     * @param fixCol
     * 列数
     * */
    public void setFixCol(String fixCol) {
        if (CommonUtil.isNumeric(fixCol)) {
            this.fixCol = Integer.parseInt(fixCol);
        }
    }

    /**
     * 设置是否可以换行
     * @param nowarp
     * 可选值 true\false
     * */
    public void setNowarp(String nowarp) {
        this.nowarp = Boolean.parseBoolean(nowarp);
    }

    /**
     * 设置记录总数
     * @param count
     * 记录总数
     * */
    public void setCount(String count) {
        if (CommonUtil.isNumeric(count)) {
            this.count = Integer.parseInt(count);
        }
    }

    /**
     * 设置每页显示的行数
     * @param pageCount
     * 每页显示的行数
     * */
    public void setPageCount(String pageCount) {
        if (CommonUtil.isNumeric(pageCount)) {
            this.pageCount = Integer.parseInt(pageCount);
        }
    }

    /**
     * 记录当前传送数据集合的起始页
     * @param startPage
     * 起始页
     * */
    public void setStartPage(String startPage) {
        if (CommonUtil.isNumeric(startPage)) {
            this.startPage = Integer.parseInt(startPage);
        }
    }

    /**
     * 设置每次获取缓存页数
     * @param cachePage
     * 缓存页数
     * */
    public void setCachePage(String cachePage) {
        if (CommonUtil.isNumeric(cachePage)) {
            this.cachePage = Integer.parseInt(cachePage);
        }
    }

    /**
     * 设置当前显示页
     * @param page
     * 当前显示页
     * */
    public void setPage(String page) {
        if (CommonUtil.isNumeric(page)) {
            this.page = Integer.parseInt(page);
        }
    }

    public void setTitleBgColor(String titleBgColor){
        this.titleBgColor = titleBgColor;
    }

    /**
     * 设置奇数行背景色
     * */
    public void setRowBgColor(String rowBgColor) {
        this.rowBgColor = rowBgColor;
    }

    /**
     * 设置偶数行背景色
     * */
    public void setRowBgColor2(String rowBgColor2) {
        this.rowBgColor2 = rowBgColor2;
    }

    /**
     * 设置奇数行字体颜色值
     * */
    public void setRowFontColor(String rowFontColor) {
        this.rowFontColor = rowFontColor;
    }

    /**
     * 设置偶数行字体颜色值
     * */
    public void setRowFontColor2(String rowFontColor2) {
        this.rowFontColor2 = rowFontColor2;
    }

    /**
     * 设置指定列数中的字体颜色
     * */
    public void colFontColor(String colFontColor, String col) {
        if (col.contains("[") && col.contains("]")) {
            col = col.substring(col.indexOf("[") + 1, col.indexOf("]"));
            String[] cols = StringUtil.split(col, ",");
            for (int i = 0; i < cols.length; i++) {
                if (CommonUtil.isNumeric(cols[i])) {
                    this.colFontColor.put(Integer.parseInt(cols[i]),
                            colFontColor);
                }
            }
        } else {
            col = col.substring("colFontColor".length());
            String[] cols = StringUtil.split(col, ",");
            for (int i = 0; i < cols.length; i++) {
                if (CommonUtil.isNumeric(cols[i])) {
                    this.colFontColor.put(Integer.parseInt(cols[i]),
                            colFontColor);
                }
            }
        }
    }

    /**
     * 设置指定列数中的背景色
     * */
    public void colBgColor(String colBgColor, String col) {
        if (col.contains("[") && col.contains("]")) {
            col = col.substring(col.indexOf("[") + 1, col.indexOf("]"));
            String[] cols = StringUtil.split(col, ",");
            for (int i = 0; i < cols.length; i++) {
                if (CommonUtil.isNumeric(cols[i])) {
                    this.colBgColor.put(Integer.parseInt(cols[i]), colBgColor);
                }
            }
        } else {
            col = col.substring("colBgColor".length());
            String[] cols = StringUtil.split(col, ",");
            for (int i = 0; i < cols.length; i++) {
                if (CommonUtil.isNumeric(cols[i])) {
                    this.colBgColor.put(Integer.parseInt(cols[i]), colBgColor);
                }
            }
        }
    }

    public void cellFontColor(String cellFontColor, String col) {
        if (col.contains("[") && col.contains("]")) {
            col = col.substring(col.indexOf("[") + 1, col.indexOf("]"));
            if (CommonUtil.isNumeric(col)) {
                String[] item = StringUtil.split(cellFontColor, ":");
                if (item.length == 2 && CommonUtil.validRGBColor(item[0])) {
                    this.cellFontColor.put(Integer.parseInt(col), item[0]);
                }
            }
        } else {
            col = col.substring("cellFontColor".length());
            if (CommonUtil.isNumeric(col)) {
//                String[] item = StringUtil.split(cellFontColor, ":");
//                if (item.length == 2 && CommonUtil.validRGBColor(item[0])) {
//                    this.cellFontColor.put(Integer.parseInt(col), item);
//                }

                if (CommonUtil.validRGBColor(cellFontColor)) {
                    this.cellFontColor.put(Integer.parseInt(col), cellFontColor);
                }
            }
        }
    }

    public void cellBgColor(String cellBgColor, String col) {

        if (col.contains("[") && col.contains("]")) {
            col = col.substring(col.indexOf("[") + 1, col.indexOf("]"));
            if (CommonUtil.isNumeric(col)) {
                String[] item = StringUtil.split(cellBgColor, ":");
                if (item.length == 2 && CommonUtil.validRGBColor(item[0])) {
                    this.cellBgColor.put(Integer.parseInt(col), item[0]);
                }
            }
        } else {
            col = col.substring("cellBgColor".length());
            if (CommonUtil.isNumeric(col)) {
//                String[] item = StringUtil.split(cellBgColor, ":");
//                if (item.length == 2 && CommonUtil.validRGBColor(item[0])) {
//                    this.cellBgColor.put(Integer.parseInt(col), item);
//                }
                if (CommonUtil.validRGBColor(cellBgColor)) {
                    this.cellBgColor.put(Integer.parseInt(col), cellBgColor);
                }
            }
        }
    }

    public void setClick(String click) {
        this.click = click;
    }

    /**
     * 添加标题文本
     * @param title
     * 标题文本
     * */
    public void addTitle(String title) {
        try {
            if (title == null || "".equals(title.trim()))
                return;
            String[] mTitles = title.split(",");
            if (mTitles.length == 0 || mTitles.length < 2)
                mTitles = StringUtil.split(title, "||");
            if (mTitles.length == 0 || mTitles.length < 2)
                return;
            String titleName = mTitles[0];
            if (titleName == null || "".equals(titleName))
                titleName = ctx.getString(R.string.axeac_toast_exp_notitle);
            Log.d("dxs", "[" + title + "]");
            titlesArray.add(titleName);
            if (mTitles.length >= 2) {
                String titleLength = mTitles[1];
                if (titleLength == null || "".equals(titleLength))
                    titleLength = "100";
                if (titleLength.indexOf("%") != -1) {
                    float percent = Float.parseFloat("0."
                            + titleLength.replaceAll("%", ""));
                    float length = StaticObject.deviceWidth * percent;
                    lengths.add((int) length);
                } else {
                    lengths.add(Integer.parseInt(titleLength));
                }
            }
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "addTitle");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加表格行数据
     * @param datas
     * 表格行数据
     * */
    public void addData(String datas) {
        try {
            if (datas == null || "".equals(datas.trim()))
                return;
            String[] mDatas = StringUtil.split(datas, "||");
            if (mDatas == null || mDatas.length == 0) {
                return;
            }
            List<String> mDataArray = new ArrayList<String>();
            if (mDatas.length > titlesArray.size()) {
                for (int i = 0; i < titlesArray.size(); i++) {
                    if (mDatas[i] == null || "".equals(mDatas[i].trim()))
                        mDatas[i] = " ";
                    mDataArray.add(mDatas[i]);
                }
            } else if (mDatas.length == titlesArray.size()) {
                for (String data : mDatas) {
                    if (data == null || "".equals(data.trim()))
                        data = " ";
                    mDataArray.add(data);
                }
            } else if (mDatas.length < titlesArray.size()) {
                for (String data : mDatas) {
                    if (data == null || "".equals(data.trim()))
                        data = " ";
                    mDataArray.add(data);
                }
                for (int i = 0; i < titlesArray.size() - mDatas.length; i++) {
                    mDataArray.add(" ");
                }
            }
            dataArray.add(mDataArray);
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "addData");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加行按钮，行按钮默认添加在每行的最后一列（新增），宽度自动计算
     * <br>Op1,操作1||op2,操作2||
     * @param btns
     * 按钮操作数据
     * */
    public void addRowButton(String btns) {
        if (btns == null || "".equals(btns.trim()))
            return;
        String[] mBtns = StringUtil.split(btns, "||");
        if (mBtns == null || mBtns.length == 0)
            return;
        this.rowBtns.add(btns);
    }

    /**
     * 长按某行时出现的按钮，弹出的上下文菜单标题为“可用操作[当前行的值]”
     * @param btns
     *
     * */
    public void addOptionButton(String btns) {
        if (btns == null || "".equals(btns.trim()))
            return;
        String[] mBtns = StringUtil.split(btns, "||");
        if (mBtns == null || mBtns.length == 0)
            return;
        this.optBtns.add(btns);
    }

    public void setEditor(String editor) {
        this.editor = Boolean.parseBoolean(editor);
    }

    public void setEditorCols(String editorCols) {
        String[] cols = StringUtil.split(editorCols, ",");
        for (String col : cols) {
            if (CommonUtil.isNumeric(col)) {
                this.editorCols.add(Integer.parseInt(col));
            }
        }
    }

    public void setEditorColsFilter(String editorColsFilter) {
        String[] colsFilter = StringUtil.split(editorColsFilter, "||");
        for (String colFilter : colsFilter) {
            String[] filter = StringUtil.split(colFilter, ",");
            if (filter.length == 2 && CommonUtil.isNumeric(filter[0])) {
                this.editorColsFilter.put(Integer.parseInt(filter[0]),
                        filter[1]);
            }
        }
    }

    public void setCellClick(String cellClick, String col) {
        if (col.contains("[") && col.contains("]")) {
            col = col.substring(col.indexOf("[") + 1, col.indexOf("]"));
            if (CommonUtil.isNumeric(col)) {
                this.cellClick.put(Integer.parseInt(col), cellClick);
            }
        } else {
            col = col.substring("cellClick".length());
            if (CommonUtil.isNumeric(col)) {
                this.cellClick.put(Integer.parseInt(col), cellClick);
            }
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (!this.visiable) return;
        if (this.title == null || "".equals(this.title)) {
            viewTitleTxt.setVisibility(View.GONE);
        } else {
            viewTitleTxt.setVisibility(View.VISIBLE);
            viewTitle.setBackgroundColor(Color.rgb(
                    Integer.parseInt(titleBgColor.substring(0, 3)),
                    Integer.parseInt(titleBgColor.substring(3, 6)),
                    Integer.parseInt(titleBgColor.substring(6, 9))));
            viewTitleTxt.setText(this.title);
        }
        dataCount = dataArray.size();
        if (editor) {
            viewAddBtn.setVisibility(View.VISIBLE);
            viewDelBtn.setVisibility(View.VISIBLE);
            viewAddBtn.setOnClickListener(addBtnOnClickListener);
            viewDelBtn.setOnClickListener(delBtnOnClickListener);
            if (editorCols.size() == 0) {
                for (int i = 0; i < lengths.size(); i++) {
                    editorCols.add(i);
                }
            }
        } else {
            viewAddBtn.setVisibility(View.GONE);
            viewDelBtn.setVisibility(View.GONE);
        }

        if (navigator && dataArray.size() > pageCount) {
            if (this.scroll) {
                table.findViewById(R.id.table_nav_list)
                        .setVisibility(View.GONE);
                table.findViewById(R.id.table_nav_part).setVisibility(
                        View.VISIBLE);
                navFirst = (ImageButton) table
                        .findViewById(R.id.table_nav_part_first);
                navLast = (ImageButton) table
                        .findViewById(R.id.table_nav_part_last);
                navPrev = (ImageButton) table
                        .findViewById(R.id.table_nav_part_prev);
                navNext = (ImageButton) table
                        .findViewById(R.id.table_nav_part_next);
                navNumber = (EditText) table
                        .findViewById(R.id.table_nav_part_no);
                navTotPage = (TextView) table
                        .findViewById(R.id.table_nav_part_totpage);
                navGo = (Button) table.findViewById(R.id.table_nav_part_go);
            } else {
                table.findViewById(R.id.table_nav_list).setVisibility(
                        View.VISIBLE);
                table.findViewById(R.id.table_nav_part)
                        .setVisibility(View.GONE);
                navFirst = (ImageButton) table
                        .findViewById(R.id.table_nav_list_first);
                navLast = (ImageButton) table
                        .findViewById(R.id.table_nav_list_last);
                navPrev = (ImageButton) table
                        .findViewById(R.id.table_nav_list_prev);
                navNext = (ImageButton) table
                        .findViewById(R.id.table_nav_list_next);
                navNumber = (EditText) table
                        .findViewById(R.id.table_nav_list_no);
                navTotPage = (TextView) table
                        .findViewById(R.id.table_nav_list_totpage);
                navGo = (Button) table.findViewById(R.id.table_nav_list_go);
            }
            navFirst.setOnClickListener(navBtnOnClickListener());
            navLast.setOnClickListener(navBtnOnClickListener());
            navPrev.setOnClickListener(navBtnOnClickListener());
            navNext.setOnClickListener(navBtnOnClickListener());
            navGo.setOnClickListener(navBtnOnClickListener());
            navNumber.setText(page + "");
            if (count % pageCount > 0) {
                totPage = count / pageCount + 1;
            } else {
                totPage = count / pageCount;
            }
            int lastPage = 0;
            if (dataArray.size() % pageCount > 0) {
                lastPage = dataArray.size() / pageCount + 1;
            } else {
                lastPage = dataArray.size() / pageCount;
            }
            if (totPage < lastPage) {
                totPage = lastPage;
            }
            navTotPage.setText(totPage + "");
        } else {
            table.findViewById(R.id.table_nav_list).setVisibility(View.GONE);
            table.findViewById(R.id.table_nav_part).setVisibility(View.GONE);
        }
        for (int i = 0; i < dataArray.size(); i++) {
            choiceMap.put(i, false);
        }
        execute(page);
    }

    private void execute(int index) {

        if (navigator) {
            viewListTitles.removeAllViews();
            viewLeftTitles.removeAllViews();
            scrollViewLeft.removeAllViews();
            scrollViewRightTitles.removeAllViews();
            scrollViewRightDatas.removeAllViews();
            viewListDatas.removeAllViews();
            table.findViewById(R.id.table_nav_no).setVisibility(View.GONE);
        } else {
            table.findViewById(R.id.table_nav_no).setVisibility(View.GONE);
            int t = dataArray.size() - (this.pageCount * (index));
            if (t > 0) {
                table_nav_allnext.setText(ctx.getString(R.string.axeac_download_next, Math.min(this.pageCount, t) + ""));
//                        + ((int) Math.min(this.pageCount, t)) + "条数据");
            } else {
                table_nav_allnext.setText(ctx.getString(R.string.axeac_download_nomore));
            }
        }
        Paint paint = new Paint();
        paint.setTextSize(18);
        Paint.FontMetrics fm = paint.getFontMetrics();

        List<List<String>> datas;
        if (navigator && dataArray.size() > pageCount) {
            datas = new ArrayList<List<String>>();
            for (int i = (index - 1) * pageCount; i < index * pageCount; i++) {
                if (i < dataArray.size()) {
                    datas.add(dataArray.get(i));
                }
            }
        } else {
            datas = new ArrayList<List<String>>();
            for (int i = (index - 1) * pageCount; i < index * pageCount; i++) {
                if (i < dataArray.size()) {
                    datas.add(dataArray.get(i));
                }
            }
        }
        if (this.scroll) {
            if (this.fixCol > titlesArray.size()) {
                fixCol = titlesArray.size();
            }
            if (this.fixCol < 0) {
                this.fixCol = 0;
            }
            if (this.fixCol == 0
                    || (this.fixCol != 0 && this.fixCol == this.titlesArray
                    .size())) {
                this.buildAllData(datas);
            } else {
                this.buildPartData(datas);
            }
        } else {
            int count = 0;
            for (int len : this.lengths) {
                count += len;
            }
            for (int len : this.lengths) {
                lengths_.add(((StaticObject.deviceWidth) * len) / count);
            }
            this.buildData(datas);
        }
    }

    private View.OnClickListener addBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            List<String> datas = new ArrayList<String>();
            for (int i = 0; i < lengths.size(); i++) {
                datas.add("");
            }
            dataArray.add(datas);
            int index = dataArray.size();
            addRows.add(String.valueOf(index - 1));
            choiceMap.put(index - 1, false);
            if (scroll) {
                if (fixCol > titlesArray.size()) {
                    fixCol = titlesArray.size();
                }
                if (fixCol < 0) {
                    fixCol = 0;
                }
                if (fixCol == 0
                        || (fixCol != 0 && fixCol == titlesArray.size())) {
                    LinearLayout rightDatas = (LinearLayout) scrollViewRightDatas
                            .getChildAt(0);
                    LinearLayout rightData = new LinearLayout(ctx);
                    rightData.setOrientation(LinearLayout.HORIZONTAL);
                    rightData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, lineHeight));
                    rightData.setMinimumHeight(minHeight);
                    rightData.setGravity(Gravity.CENTER);
                    rightData.setTag(datas);
                    if (optBtns.size() > 0) {
                        rightData.setOnLongClickListener(new OptionLongClickListener());
                    }
                    rightData.setOnClickListener(new OptionClickListener());
                    if (editor) {
                        rightData.addView(getChoiceItem(index));
                    }
                    int j = 0;
                    for (String data : datas) {
                        if (lengths.get(j) != 0) {
                            rightData.addView(getItemView(data, lengths, j,
                                    index - pageCount * (page - 1)));
                        }
                        j++;
                    }
                    if (index == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 0) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor2.substring(0, 3)),
                                Integer.parseInt(rowBgColor2.substring(3, 6)),
                                Integer.parseInt(rowBgColor2.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    }
                    ArrayList<LinearLayout> viewLine = new ArrayList<LinearLayout>();
                    viewLine.add(rightData);
                    int no = index - 1;
                    if (choiceMap.get(no)) {
                        rightData.setVisibility(View.GONE);
                    }
                    tableLineViews.put(no, viewLine);
                    rightDatas.addView(rightData);
                } else {
                    LinearLayout leftDatas = (LinearLayout) scrollViewLeft
                            .getChildAt(0);
                    LinearLayout leftData = new LinearLayout(ctx);
                    leftData.setOrientation(LinearLayout.HORIZONTAL);
                    leftData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, lineHeight));
                    leftData.setMinimumHeight(minHeight);
                    leftData.setGravity(Gravity.CENTER);
                    leftData.setTag(datas);
                    if (optBtns.size() > 0) {
                        leftData.setOnLongClickListener(new OptionLongClickListener());
                    }
                    leftData.setOnClickListener(new OptionClickListener());
                    if (editor) {
                        leftData.addView(getChoiceItem(index));
                    }
                    for (int i = 0; i < fixCol; i++) {
                        if (lengths.get(i) != 0) {
                            leftData.addView(getItemView(datas.get(i), lengths,
                                    i, index - pageCount * (page - 1)));
                        }
                    }
                    if (index == 1) {
                        leftData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        leftData.getBackground().setAlpha(180);
                    } else if (index % 2 == 0) {
                        leftData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor2.substring(0, 3)),
                                Integer.parseInt(rowBgColor2.substring(3, 6)),
                                Integer.parseInt(rowBgColor2.substring(6, 9))));
                        leftData.getBackground().setAlpha(180);
                    } else if (index % 2 == 1) {
                        leftData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        leftData.getBackground().setAlpha(180);
                    }
                    int no = index - 1;
                    ArrayList<LinearLayout> leftViewLine = new ArrayList<LinearLayout>();
                    leftViewLine.add(leftData);
                    if (choiceMap.get(no)) {
                        leftData.setVisibility(View.GONE);
                    }
                    tableLineViews.put(no, leftViewLine);
                    leftDatas.addView(leftData);
                    LinearLayout rightDatas = (LinearLayout) scrollViewRightDatas
                            .getChildAt(0);
                    ;
                    LinearLayout rightData = new LinearLayout(ctx);
                    rightData.setOrientation(LinearLayout.HORIZONTAL);
                    rightData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                    rightData.setMinimumHeight(minHeight);
                    rightData.setGravity(Gravity.CENTER);
                    rightData.setTag(datas);
                    if (optBtns.size() > 0) {
                        rightData
                                .setOnLongClickListener(new OptionLongClickListener());
                    }
                    rightData.setOnClickListener(new OptionClickListener());
                    for (int i = fixCol; i < titlesArray.size(); i++) {
                        if (lengths.get(i) != 0) {
                            rightData
                                    .addView(getItemView(datas.get(i), lengths,
                                            i, index - pageCount * (page - 1)));
                        }
                    }
                    if (index == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 0) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor2.substring(0, 3)),
                                Integer.parseInt(rowBgColor2.substring(3, 6)),
                                Integer.parseInt(rowBgColor2.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    }
                    if (choiceMap.get(no)) {
                        rightData.setVisibility(View.GONE);
                    }
                    if (tableLineViews.containsKey(no)) {
                        ArrayList<LinearLayout> rightViewLine = tableLineViews
                                .get(no);
                        rightViewLine.add(rightData);
                    } else {
                        ArrayList<LinearLayout> rightViewLine = new ArrayList<LinearLayout>();
                        rightViewLine.add(rightData);
                        tableLineViews.put(no, rightViewLine);
                    }
                    rightDatas.addView(rightData);
                }
            } else {
                LinearLayout rightDatas = (LinearLayout) viewListDatas
                        .getChildAt(0);
                LinearLayout rightData = new LinearLayout(ctx);
                rightData.setOrientation(LinearLayout.HORIZONTAL);
                rightData.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                rightData.setMinimumHeight(minHeight);
                rightData.setGravity(Gravity.CENTER);
                rightData.setTag(datas);
                if (optBtns.size() > 0) {
                    rightData
                            .setOnLongClickListener(new OptionLongClickListener());
                }
                rightData.setOnClickListener(new OptionClickListener());
                if (editor) {
                    rightData.addView(getChoiceItem(index));
                }
                int j = 0;
                for (String data : datas) {
                    if (lengths.get(j) != 0) {
                        rightData.addView(getItemView(data, lengths_, j, index
                                - pageCount * (page - 1)));
                    }
                    j++;
                }
                if (index == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (index % 2 == 0) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor2.substring(0, 3)),
                            Integer.parseInt(rowBgColor2.substring(3, 6)),
                            Integer.parseInt(rowBgColor2.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (index % 2 == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                }
                ArrayList<LinearLayout> viewLine = new ArrayList<LinearLayout>();
                viewLine.add(rightData);
                int no = index - 1;
                if (choiceMap.get(no)) {
                    rightData.setVisibility(View.GONE);
                }
                tableLineViews.put(no, viewLine);
                rightDatas.addView(rightData);
            }
        }
    };

    private View.OnClickListener delBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < choiceMap.size(); i++) {
                if (choiceMap.get(i) && !delRows.contains(i)) {
                    if (i < dataCount) {
                        delRows.add(i);
                    } else {
                        addRows.remove(String.valueOf(i));
                    }
                    ArrayList<LinearLayout> lineViews = tableLineViews.get(i);
                    for (int j = 0; j < lineViews.size(); j++) {
                        LinearLayout lineView = lineViews.get(j);
                        if (lineView != null) {
                            lineView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    };

    /**
     * 返回表格标题视图
     * @param title
     * 表格标题
     * @param length
     * 表格宽度
     * @param index
     * 位置
     * */
    private View getTitleView(String title, int length, int index) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(ctx).inflate(
                R.layout.axeac_table_item_title, null);
        try {
            TextView titleView = (TextView) view.findViewById(R.id.table_item);
            titleView.setPadding(5, 5, 5, 5);
            ImageButton btn = (ImageButton) view
                    .findViewById(R.id.table_item_btn);
            btn.setPadding(0, 5, 0, 5);
            titleView.setText(title);
            if (grivaty.equals("left")){
                titleView.setGravity(Gravity.LEFT);
            }else if(grivaty.equals("center")){
                titleView.setGravity(Gravity.CENTER);
            }else if(grivaty.equals("right")){
                titleView.setGravity(Gravity.RIGHT);
            }

            if(style!=null&&!"".equals(style)){
                String styles[] = style.split(":");
                if(styles.length==2){
                    if(Integer.parseInt(styles[0])!=0)
                    titleView.setTextSize(Integer.parseInt(styles[0]));
                    if(styles[1].toLowerCase().equals("true")){
                        titleView.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }
            }

            view.setLayoutParams(new ViewGroup.LayoutParams(length,
                    minHeight));
            btn.setTag(R.string.axeac_key0, index);
            btn.setTag(R.string.axeac_key1, "desc");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton btn = (ImageButton) view;
                    String status = view.getTag(R.string.axeac_key1).toString();
                    if ("desc".equals(status)) {
                        btn.setTag(R.string.axeac_key1, "asc");
                        btn.setBackgroundDrawable(ctx.getResources()
                                .getDrawable(R.drawable.axeac_asc));
                    }
                    if ("asc".equals(status)) {
                        btn.setTag(R.string.axeac_key1, "desc");
                        btn.setBackgroundDrawable(ctx.getResources()
                                .getDrawable(R.drawable.axeac_desc));
                    }
                    Toast.makeText(ctx, view.getTag(R.string.axeac_key0).toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "CreateTitle");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    /**
     * 返回表格数据视图
     * @param data
     * @param length
     * @param row
     * @param col
     * */
    private View getDataView(String data, int length, int row, int col) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(ctx).inflate(
                R.layout.axeac_table_item_data, null);
        try {
            TextView dataTxtView = (TextView) view
                    .findViewById(R.id.table_item_txt);
            EditText dataEditView = (EditText) view
                    .findViewById(R.id.table_item_edit);
            view.setLayoutParams(new ViewGroup.LayoutParams(length,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            int lineNo = row + pageCount * (page - 1) - 1;
            if (editor && addRows.contains(String.valueOf(lineNo))) {
                dataEditView.setVisibility(View.VISIBLE);
                dataTxtView.setVisibility(View.GONE);
                if (addRowsViews.containsKey(lineNo)) {
                    ArrayList<EditText> editList = addRowsViews.get(lineNo);
                    editList.add(dataEditView);
                } else {
                    ArrayList<EditText> editList = new ArrayList<EditText>();
                    editList.add(dataEditView);
                    addRowsViews.put(lineNo, editList);
                }
                setItemViewParameter(view, dataEditView, data, length, row, col);
                setItemViewCellFontColor(row, col, dataEditView);
                setItemViewCellBgColor(row, col, view, dataEditView);
            } else if (editor && editorCols.contains(col)) {
                dataEditView.setVisibility(View.VISIBLE);
                dataTxtView.setVisibility(View.GONE);
                if (editColViews.containsKey(lineNo)) {
                    ArrayList<EditText> editList = editColViews.get(lineNo);
                    editList.add(dataEditView);
                } else {
                    ArrayList<EditText> editList = new ArrayList<EditText>();
                    editList.add(dataEditView);
                    editColViews.put(lineNo, editList);
                }
                setItemViewParameter(view, dataEditView, data, length, row, col);
                setItemViewCellFontColor(row, col, dataEditView);
                setItemViewCellBgColor(row, col, view, dataEditView);
            } else {
                dataEditView.setVisibility(View.GONE);
                dataTxtView.setVisibility(View.VISIBLE);
                setItemViewParameter(view, dataTxtView, data, length, row, col);
                setItemViewCellFontColor(row, col, dataTxtView);
                setItemViewCellBgColor(row, col, view, dataTxtView);
            }
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "CreateData");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void setItemViewParameter(View view, TextView dataView,
                                      String data, int length, int row, int col) {
        dataView.setPadding(5, 5, 5, 5);
        dataView.setText(data);
        if (grivaty.equals("left")){
            dataView.setGravity(Gravity.LEFT);
        }else if(grivaty.equals("center")){
            dataView.setGravity(Gravity.CENTER);
        }else if(grivaty.equals("right")){
            dataView.setGravity(Gravity.RIGHT);
        }

        if (nowarp) {
            dataView.setSingleLine();
        }
        if (row == 1) {
            dataView.setTextColor(Color.rgb(
                    Integer.parseInt(rowFontColor.substring(0, 3)),
                    Integer.parseInt(rowFontColor.substring(3, 6)),
                    Integer.parseInt(rowFontColor.substring(6, 9))));
        } else if (row % 2 == 0) {
            dataView.setTextColor(Color.rgb(
                    Integer.parseInt(rowFontColor2.substring(0, 3)),
                    Integer.parseInt(rowFontColor2.substring(3, 6)),
                    Integer.parseInt(rowFontColor2.substring(6, 9))));
        } else if (row % 2 == 1) {
            dataView.setTextColor(Color.rgb(
                    Integer.parseInt(rowFontColor.substring(0, 3)),
                    Integer.parseInt(rowFontColor.substring(3, 6)),
                    Integer.parseInt(rowFontColor.substring(6, 9))));
        }
        if (this.colFontColor.containsKey(col)) {
            String val = this.colFontColor.get(col);
            dataView.setTextColor(Color.rgb(
                    Integer.parseInt(val.substring(0, 3)),
                    Integer.parseInt(val.substring(3, 6)),
                    Integer.parseInt(val.substring(6, 9))));
        }
        if (this.colBgColor.containsKey(col)) {
            String val = this.colBgColor.get(col);
            view.setBackgroundColor(Color.rgb(
                    Integer.parseInt(val.substring(0, 3)),
                    Integer.parseInt(val.substring(3, 6)),
                    Integer.parseInt(val.substring(6, 9))));
            view.getBackground().setAlpha(180);
        }

    }

    private String getVal(int row, int col) {
        if (row < this.dataArray.size()) {
            List<String> ar = this.dataArray.get(row);
            if (col < ar.size()) {
                return ar.get(col);
            }
        }
        return null;
    }

    private float toFloat(int row, int col) {
        String val = getVal(row, col);
        if (val == null)
            return Float.MIN_VALUE;
        if (CommonUtil.isNumeric(val)) {
            return Float.parseFloat(val);
        }
        return Float.MIN_VALUE;
    }

    private void setItemViewCellFontColor(int row, int col, TextView dataView) {
        String txt = dataView.getText().toString();
//        if (!CommonUtil.isFloat(txt)) {
//            return;
//        }
        if (!this.cellFontColor.containsKey(row)) {
            return;
        }
//        String[] item = this.cellFontColor.get(col);
        String color = this.cellFontColor.get(row);
        String condition = this.cellFontColor.get(row);
        int index1 = condition.indexOf("<=");
        int index2 = condition.indexOf(">=");
        int index3 = condition.indexOf("<");
        int index4 = condition.indexOf(">");
        int index5 = condition.indexOf("=");
        if (index1 != -1) {
            String[] str = StringUtil.split(condition, "<=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {
                    if (toFloat(row, Integer.parseInt(no)) <= Float
                            .parseFloat(val)) {
                        dataView.setTextColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                    }
                }
            }
        } else if (index2 != -1) {
            String[] str = StringUtil.split(condition, ">=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {
                    if (toFloat(row, Integer.parseInt(no)) >= Float
                            .parseFloat(val)) {
                        dataView.setTextColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                    }
                }
            }
        } else if (index3 != -1) {
            String[] str = StringUtil.split(condition, "<");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {

                    if (toFloat(row, Integer.parseInt(no)) < Float
                            .parseFloat(val)) {
                        dataView.setTextColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                    }

                }
            }
        } else if (index4 != -1) {
            String[] str = StringUtil.split(condition, ">");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {

                    if (toFloat(row, Integer.parseInt(no)) > Float
                            .parseFloat(val)) {
                        dataView.setTextColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                    }

                }
            }
        } else if (index5 != -1) {
            String[] str = StringUtil.split(condition, "=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {

                    if (toFloat(row, Integer.parseInt(no)) == Float
                            .parseFloat(val)) {
                        dataView.setTextColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                    }

                }
            }
        }else{
            dataView.setTextColor(Color.rgb(
                    Integer.parseInt(color.substring(0, 3)),
                    Integer.parseInt(color.substring(3, 6)),
                    Integer.parseInt(color.substring(6, 9))));
        }
    }

    private void setItemViewCellBgColor(int row, int col, View view,
                                        TextView dataView) {
        String txt = dataView.getText().toString();
//        if (!CommonUtil.isFloat(txt)) {
//            return;
//        }
//        if (!this.cellBgColor.containsKey(col)) {
//            return;
//        }
        if (!this.cellBgColor.containsKey(row)) {
            return;
        }
//        String[] item = this.cellBgColor.get(row);
        String color = this.cellBgColor.get(row);
        String condition = this.cellBgColor.get(row);
        int index1 = condition.indexOf("<=");
        int index2 = condition.indexOf(">=");
        int index3 = condition.indexOf("<");
        int index4 = condition.indexOf(">");
        int index5 = condition.indexOf("=");
        if (index1 != -1) {
            String[] str = StringUtil.split(condition, "<=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {
                    if (Float.parseFloat(txt) <= Float.parseFloat(val)) {
                        view.setBackgroundColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                        view.getBackground().setAlpha(180);
                    }
                }
            }
        } else if (index2 != -1) {
            String[] str = StringUtil.split(condition, ">=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {
                    if (Float.parseFloat(txt) >= Float.parseFloat(val)) {
                        view.setBackgroundColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                        view.getBackground().setAlpha(180);
                    }
                }
            }
        } else if (index3 != -1) {
            String[] str = StringUtil.split(condition, "<");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {

                    if (Float.parseFloat(txt) < Float.parseFloat(val)) {
                        view.setBackgroundColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                        view.getBackground().setAlpha(180);
                    }

                }
            }
        } else if (index4 != -1) {
            String[] str = StringUtil.split(condition, ">");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {
                    if (Float.parseFloat(txt) > Float.parseFloat(val)) {
                        view.setBackgroundColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                        view.getBackground().setAlpha(180);
                    }

                }
            }
        } else if (index5 != -1) {
            String[] str = StringUtil.split(condition, "=");
            if (str.length == 2) {
                String no = StringUtil.replaceAll(str[0], "@", "");
                String val = str[1];
                if (CommonUtil.isNumeric(no) && CommonUtil.isFloat(val)) {

                    if (Float.parseFloat(txt) == Float.parseFloat(val)) {
                        view.setBackgroundColor(Color.rgb(
                                Integer.parseInt(color.substring(0, 3)),
                                Integer.parseInt(color.substring(3, 6)),
                                Integer.parseInt(color.substring(6, 9))));
                        view.getBackground().setAlpha(180);
                    }
                }
            }
        }else{
            view.setBackgroundColor(Color.rgb(
                    Integer.parseInt(color.substring(0, 3)),
                    Integer.parseInt(color.substring(3, 6)),
                    Integer.parseInt(color.substring(6, 9))));
            view.getBackground().setAlpha(180);
        }
    }

    @SuppressWarnings("unused")
    private View getRowButton(List<String> vals) {
        if (this.rowBtns.size() > 0) {
            LinearLayout btn_lyaout = new LinearLayout(ctx);
            btn_lyaout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btn_lyaout.setOrientation(LinearLayout.HORIZONTAL);

            for (String btn_str : this.rowBtns) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(ctx)
                        .inflate(R.layout.axeac_table_item_btn, null);
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                Button btn = (Button) view.findViewById(R.id.table_item);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String optStr = (String) view.getTag(R.string.axeac_key0);
                        List<String> vals = (List<String>) view
                                .getTag(R.string.axeac_key1);
                        String[] optStrs = StringUtil.split(optStr, "||");
                        if (optStrs.length < 4) {
                            return;
                        }
                        String click = optStrs[3];
                        String str = "";
                        String vs[] = StringUtil.split(click, ":");
                        if (vs.length >= 2) {
                            String pageid = vs[1];
                            String params[];
                            if (vs.length >= 3) {
                                params = StringUtil.split(vs[2], ",");
                                for (String param : params) {
                                    String kv[] = StringUtil.split(param, "=");
                                    if (kv.length >= 2) {
                                        str += kv[0] + "=" + vals.get(Integer.parseInt(kv[1])) + "\r\n";
                                    }
                                }
                            }
                            if (click.startsWith("PAGE")) {
                                Intent intent = new Intent();
                                intent.putExtra("meip", "MEIP_PAGE=" + pageid + "\r\n" + str);
                                LocalBroadcastManager
                                        .getInstance(ctx).sendBroadcast(intent);
                            } else if (click.startsWith("OP")) {
                                Intent intent = new Intent();
                                intent.putExtra("meip", "MEIP_ACTION=" + pageid + "\r\n" + str);
                                LocalBroadcastManager
                                        .getInstance(ctx).sendBroadcast(intent);
                            }
                        }
                    }
                });
                String name = StringUtil.split(btn_str, "||")[1];
                btn.setText(name);
                btn.setTag(R.string.axeac_key0, btn_str);
                btn.setTag(R.string.axeac_key1, vals);
                btn_lyaout.addView(view);
            }
            return btn_lyaout;
        } else {
            return null;
        }
    }

    private View.OnClickListener navBtnOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == navFirst) {
                    skipPage("firstpage");
                    return;
                }
                if (v == navLast) {
                    skipPage("lastpage");
                    return;
                }
                if (v == navPrev) {
                    skipPage("prevpage");
                    return;
                }
                if (v == navNext || v == table_nav_allnext) {
                    skipPage("nextpage");
                    return;
                }
                if (v == navGo) {
                    skipPage("gopage");
                    return;
                }
            }
        };
    }

    private void skipPage(String msg) {
        if (msg.equals("firstpage")) {
            page = 1;
            if (navNumber != null)
                navNumber.setText(page + "");
            execute(page);
            getView();
            return;
        }
        if (msg.equals("prevpage")) {
            if (page > 1) {
                page -= 1;
                if (navNumber != null)
                    navNumber.setText(page + "");
                execute(page);
                getView();
            }
            return;
        }
        if (msg.equals("nextpage")) {
            int lastPage = 1;
            if (dataArray.size() % pageCount > 0) {
                lastPage = dataArray.size() / pageCount + 1;
            } else {
                lastPage = dataArray.size() / pageCount;
            }
            if (lastPage >= totPage) {
                if (page < lastPage) {
                    page += 1;
                    if (navNumber != null)
                        navNumber.setText(page + "");
                    execute(page);
                    getView();
                }
            } else {
                if (page < lastPage) {
                    page += 1;
                    if (navNumber != null)
                        navNumber.setText(page + "");
                    execute(page);
                    getView();
                } else {
                    String parm = "MEIP_PAGE_COMPONENT=" + compId + "\r\n"
                            + "MEIP_PAGE_COMPONENT_PAGE=" + (page + 1) + "\r\n"
                            + "MEIP_PAGE_COMPONENT_CACHEPAGE=" + cachePage
                            + "\r\n";
                    doLoading(1, "MEIP_PAGE=" + formId + "\r\n" + parm);
                }
            }
            return;
        }
        if (msg.equals("lastpage")) {
            int lastPage = 0;
            if (dataArray.size() % pageCount > 0) {
                lastPage = dataArray.size() / pageCount + 1;
            } else {
                lastPage = dataArray.size() / pageCount;
            }
            if (lastPage >= totPage) {
                if (page < lastPage) {
                    page = lastPage;
                    if (navNumber != null)
                        navNumber.setText(page + "");
                    execute(page);
                    getView();
                }
            } else {
                String parm = "MEIP_PAGE_COMPONENT=" + compId + "\r\n"
                        + "MEIP_PAGE_COMPONENT_PAGE=" + (lastPage + 1) + "\r\n"
                        + "MEIP_PAGE_COMPONENT_CACHEPAGE="
                        + (totPage - cachePage) + "\r\n";
                doLoading(2, "MEIP_PAGE=" + formId + "\r\n" + parm);
            }
            return;
        }
        if (msg.equals("gopage")) {
            int lastPage = 1;
            if (dataArray.size() % pageCount > 0) {
                lastPage = dataArray.size() / pageCount + 1;
            } else {
                lastPage = dataArray.size() / pageCount;
            }
            if (navNumber != null) {
                if (navNumber.getText().toString() == null
                        || "".equals(navNumber.getText().toString())) {
                    Toast.makeText(ctx, R.string.axeac_msg_table_go_error,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int curPage = Integer.parseInt(navNumber.getText().toString());
            if (curPage > totPage || curPage < 1) {
                Toast.makeText(ctx, R.string.axeac_msg_table_go_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                if (curPage <= lastPage) {
                    page = curPage;
                    execute(page);
                    getView();
                } else {
                    String parm = "MEIP_PAGE_COMPONENT=" + compId + "\r\n"
                            + "MEIP_PAGE_COMPONENT_PAGE=" + (lastPage + 1)
                            + "\r\n" + "MEIP_PAGE_COMPONENT_CACHEPAGE="
                            + (curPage - cachePage) + "\r\n";
                    doLoading(3, "MEIP_PAGE=" + formId + "\r\n" + parm);
                }
            }
            return;
        }
    }

    private void doLoading(final int flag, final String parm) {

        UIHelper.send(ctx, parm, new OnRequestCallBack() {
            @Override
            public void onStart() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.show();
                }
            }

            @Override
            public void onCompleted() {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                        Toast.makeText(ctx, response.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    Property items = new Property();
                    items.setSplit("\n");
                    items.load(response.getData());
                    LinkedHashtable props = items.searchSubKey(compId + ".");
                    if (props != null && props.size() > 0) {
                        Vector<?> subkeys = props.linkedKeys();
                        if (subkeys != null && subkeys.size() > 0) {
                            for (int j = 0; j < subkeys.size(); j++) {
                                String prop = (String) subkeys.elementAt(j);
                                String val = (String) props.get(prop);
                                if (prop.trim().toLowerCase().startsWith("adddata")) {
                                    addData(val);
                                }
                            }
                        }
                    }
                    if (flag == 1) {
                        page += 1;
                    } else if (flag == 2) {
                        page = totPage;
                    } else if (flag == 3) {
                        page = Integer.parseInt(navNumber.getText().toString());
                    }
                    startPage = page;
                    navNumber.setText(page + "");
                    execute(page);
                    getView();
                } else {
                    Toast.makeText(ctx, response.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onfailed(Throwable e) {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private View getItemView(final String data, List<Integer> lengths,
                             final int i, final int index) {
        View itemView = this.getDataView(data, lengths.get(i), index, i);
        Paint paint = new Paint();
        paint.setTextSize(18);
        int itemWidth = (int) paint.measureText(data);
        if (cellClick.containsKey(i)) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String click = cellClick.get(i);
                        if (click != null && !"".equals(click)) {
                            int lineNo = index + pageCount * (page - 1) - 1;
                            List<String> vals = dataArray.get(lineNo);
                            String str = "";
                            String vs[] = StringUtil.split(click, ":");
                            if (vs.length >= 2) {
                                String pageid = vs[1];
                                String params[];
                                if (vs.length >= 3) {
                                    params = StringUtil.split(vs[2], ",");
                                    for (String param : params) {
                                        String kv[] = StringUtil.split(param,
                                                "=");
                                        if (kv.length >= 2) {
                                            if (CommonUtil.isNumeric(kv[1])) {
                                                str += kv[0]
                                                        + "="
                                                        + vals.get(Integer
                                                        .parseInt(kv[1]))
                                                        + "\r\n";
                                            } else {
                                                str += kv[0] + "=" + kv[1]
                                                        + "\r\n";
                                            }
                                        }
                                    }
                                }
                                if (click.startsWith("PAGE")) {
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                    intent.putExtra("meip", "MEIP_PAGE="
                                            + pageid + "\r\n" + str);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                } else if (click.startsWith("OP")) {
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                                    intent.putExtra("meip", "MEIP_ACTION="
                                            + pageid + "\r\n" + str);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        String clsName = this.getClass().getName();
                        clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                        String info = ctx.getString(R.string.axeac_toast_exp_click);
                        Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            });
        } else {
            if (nowarp && !scroll && itemWidth > lengths_.get(i)) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ctx, data, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        return itemView;
    }

    private View getChoiceTitle() {
        TextView view = new TextView(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(70, lineHeight));
        view.setMinimumHeight(minHeight);
        return view;
    }

    private View getChoiceItem(int index) {
        final ImageView view = new ImageView(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(70, 50));
        view.setPadding(20, 0, 0, 0);
        view.setImageResource(R.drawable.axeac_label_mutichoose_disable);
        final int lineNo = index;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no = lineNo - 1 + pageCount * (page - 1);
                if (choiceMap.get(no)) {
                    choiceMap.put(no, false);
                    view.setImageResource(R.drawable.axeac_label_mutichoose_disable);
                } else {
                    choiceMap.put(no, true);
                    view.setImageResource(R.drawable.axeac_label_mutichoose_enable);
                }
            }
        });
        return view;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void buildData(List<List<String>> dataArray) {
        try {
            viewLeft.setVisibility(View.GONE);
            viewRight.setVisibility(View.GONE);
            viewList.setVisibility(View.VISIBLE);
            viewList.setBackground(WaterMarkImage.getDrawable(StaticObject.read.getString(StaticObject.USERNAME,""),240,240,30));
            if (navigator || viewListTitles.getChildCount() == 0) {
                LinearLayout rightTitles = new LinearLayout(ctx);
                rightTitles.setBackgroundColor(Color.WHITE);
                rightTitles.getBackground().setAlpha(180);
                rightTitles.setOrientation(LinearLayout.HORIZONTAL);
                rightTitles.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                rightTitles.setMinimumHeight(minHeight);
                rightTitles.setGravity(Gravity.CENTER);
                if (editor) {
                    rightTitles.addView(getChoiceTitle());
                }
                int i = 0;
                if (this.rowBtns.size() > 0) {
                    titlesArray.add(ctx.getString(R.string.axeac_download_operate));
                    lengths.add(150 * rowBtns.size());
                }
                for (String title : this.titlesArray) {
                    if (lengths.get(i) != 0) {
                        rightTitles.addView(this.getTitleView(title,
                                lengths_.get(i), i));
                    }
                    i++;
                }
                if (this.rowBtns.size() > 0) {
                    titlesArray.remove(titlesArray.size() - 1);
                    lengths.remove(lengths.size() - 1);
                }
                viewListTitles.addView(rightTitles);
            }
            if (navigator || viewListDatas.getChildCount() == 0) {
                LinearLayout rightDatas = new LinearLayout(ctx);
                rightDatas.setOrientation(LinearLayout.VERTICAL);
                rightDatas.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout rightData;
                int index = 1;
                for (List<String> datas : dataArray) {
                    rightData = new LinearLayout(ctx);
                    rightData.setOrientation(LinearLayout.HORIZONTAL);
                    rightData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                    rightData.setMinimumHeight(minHeight);
                    rightData.setGravity(Gravity.CENTER);
                    rightData.setTag(datas);
                    if (this.optBtns.size() > 0) {
                        rightData
                                .setOnLongClickListener(new OptionLongClickListener());
                    }
                    rightData.setOnClickListener(new OptionClickListener());
                    if (editor) {
                        rightData.addView(getChoiceItem(index));
                    }
                    int j = 0;
                    for (String data : datas) {
                        if (lengths.get(j) != 0) {
                            rightData.addView(getItemView(data, lengths_, j,
                                    index));
                        }
                        j++;
                    }
                    View btnView = this.getRowButton(datas);
                    if (btnView != null)
                        rightData.addView(btnView);
                    if (index == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 0) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor2.substring(0, 3)),
                                Integer.parseInt(rowBgColor2.substring(3, 6)),
                                Integer.parseInt(rowBgColor2.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    } else if (index % 2 == 1) {
                        rightData.setBackgroundColor(Color.rgb(
                                Integer.parseInt(rowBgColor.substring(0, 3)),
                                Integer.parseInt(rowBgColor.substring(3, 6)),
                                Integer.parseInt(rowBgColor.substring(6, 9))));
                        rightData.getBackground().setAlpha(180);
                    }
                    ArrayList<LinearLayout> viewLine = new ArrayList<LinearLayout>();
                    viewLine.add(rightData);
                    int no = index - 1 + pageCount * (page - 1);
                    if (choiceMap.get(no)) {
                        rightData.setVisibility(View.GONE);
                    }
                    tableLineViews.put(no, viewLine);
                    rightDatas.addView(rightData);
                    index++;
                }
                viewListDatas.addView(rightDatas);
            }
            table.invalidate();
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "buildData");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
    }

    private void buildAllData(List<List<String>> dataArray) {
        try {
            viewLeft.setVisibility(View.GONE);
            LinearLayout rightTitles = new LinearLayout(ctx);
            rightTitles.setBackgroundColor(Color.WHITE);
            rightTitles.getBackground().setAlpha(180);
            rightTitles.setOrientation(LinearLayout.HORIZONTAL);
            rightTitles.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
            rightTitles.setMinimumHeight(minHeight);
            rightTitles.setGravity(Gravity.CENTER);
            if (editor) {
                rightTitles.addView(getChoiceTitle());
            }
            int i = 0;
            if (this.rowBtns.size() > 0) {
                titlesArray.add(ctx.getString(R.string.axeac_download_operate));
                lengths.add(150 * rowBtns.size());
            }
            for (String title : this.titlesArray) {
                if (lengths.get(i) != 0) {
                    rightTitles.addView(this.getTitleView(title,
                            lengths.get(i), i));
                }
                i++;
            }
            if (this.rowBtns.size() > 0) {
                titlesArray.remove(titlesArray.size() - 1);
                lengths.remove(lengths.size() - 1);
            }
            LinearLayout rightDatas = new LinearLayout(ctx);
            rightDatas.setOrientation(LinearLayout.VERTICAL);
            rightDatas.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout rightData;
            int index = 1;
            for (List<String> datas : dataArray) {
                rightData = new LinearLayout(ctx);
                rightData.setOrientation(LinearLayout.HORIZONTAL);
                rightData.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                rightData.setMinimumHeight(minHeight);
                rightData.setGravity(Gravity.CENTER);
                rightData.setTag(datas);
                if (this.optBtns.size() > 0) {
                    rightData
                            .setOnLongClickListener(new OptionLongClickListener());
                }
                rightData.setOnClickListener(new OptionClickListener());
                if (editor) {
                    rightData.addView(getChoiceItem(index));
                }
                int j = 0;
                for (String data : datas) {
                    if (lengths.get(j) != 0) {
                        rightData.addView(getItemView(data, lengths, j, index));
                    }
                    j++;
                }
                View btnView = this.getRowButton(datas);
                if (btnView != null)
                    rightData.addView(btnView);
                if (index == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (index % 2 == 0) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor2.substring(0, 3)),
                            Integer.parseInt(rowBgColor2.substring(3, 6)),
                            Integer.parseInt(rowBgColor2.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (index % 2 == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                }
                ArrayList<LinearLayout> viewLine = new ArrayList<LinearLayout>();
                viewLine.add(rightData);
                int no = index - 1 + pageCount * (page - 1);
                if (choiceMap.get(no)) {
                    rightData.setVisibility(View.GONE);
                }
                tableLineViews.put(no, viewLine);
                rightDatas.addView(rightData);
                index++;
            }
            scrollViewRightTitles.addView(rightTitles);
            scrollViewRightDatas.addView(rightDatas);
            this.table.invalidate();
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "buildAllData");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
    }

    private void buildPartData(List<List<String>> dataArray) {
        try {
            if (navigator || viewLeftTitles.getChildCount() == 0) {
                LinearLayout leftTitles = new LinearLayout(ctx);
                leftTitles.setBackgroundColor(Color.WHITE);
                leftTitles.getBackground().setAlpha(180);
                leftTitles.setOrientation(LinearLayout.HORIZONTAL);
                leftTitles.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, lineHeight));
                leftTitles.setMinimumHeight(minHeight);
                leftTitles.setGravity(Gravity.CENTER);
                if (editor) {
                    leftTitles.addView(getChoiceTitle());
                }
                for (int i = 0; i < this.fixCol; i++) {
                    if (lengths.get(i) != 0) {
                        leftTitles.addView(this.getTitleView(this.titlesArray.get(i), this.lengths.get(i), i));
                    }
                }
                viewLeftTitles.addView(leftTitles);
            } else {
                View v = viewLeftTitles.getChildAt(0);
                viewLeftTitles.removeAllViews();
                viewLeftTitles.addView(v);
            }
            if (navigator || scrollViewRightTitles.getChildCount() == 0) {
                LinearLayout rightTitles = new LinearLayout(ctx);
                rightTitles.setBackgroundColor(Color.WHITE);
                rightTitles.getBackground().setAlpha(180);
                rightTitles.setOrientation(LinearLayout.HORIZONTAL);
                rightTitles.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                rightTitles.setMinimumHeight(minHeight);
                rightTitles.setGravity(Gravity.CENTER);
                if (this.rowBtns.size() > 0) {
                    titlesArray.add(ctx.getString(R.string.axeac_download_operate));
                    lengths.add(150 * rowBtns.size());
                }
                for (int i = this.fixCol; i < this.titlesArray.size(); i++) {
                    if (lengths.get(i) != 0) {
                        rightTitles.addView(this.getTitleView(this.titlesArray.get(i), this.lengths.get(i), i));
                    }
                }
                if (this.rowBtns.size() > 0) {
                    titlesArray.remove(titlesArray.size() - 1);
                    lengths.remove(lengths.size() - 1);
                }
                scrollViewRightTitles.addView(rightTitles);
            } else {
                View v = scrollViewRightTitles.getChildAt(0);
                scrollViewRightTitles.removeAllViews();
                scrollViewRightTitles.addView(v);
            }
            LinearLayout leftDatas = ((navigator || scrollViewLeft
                    .getChildCount() == 0) ? new LinearLayout(ctx)
                    : (LinearLayout) scrollViewLeft.getChildAt(0));
            leftDatas.setOrientation(LinearLayout.VERTICAL);
            leftDatas.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout leftData;
            int indexLeft = 1;
            for (List<String> datas : dataArray) {

            }


            int indexRight = 1;
            LinearLayout rightDatas = ((navigator || scrollViewRightDatas
                    .getChildCount() == 0) ? new LinearLayout(ctx)
                    : (LinearLayout) scrollViewRightDatas.getChildAt(0));

            rightDatas.setOrientation(LinearLayout.VERTICAL);
            rightDatas.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout rightData;


            for (List<String> datas : dataArray) {

                leftData = new LinearLayout(ctx);
                leftData.setOrientation(LinearLayout.HORIZONTAL);
                leftData.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, lineHeight));
                leftData.setMinimumHeight(minHeight);
                leftData.setGravity(Gravity.CENTER);
                leftData.setTag(datas);
                if (this.optBtns.size() > 0) {
                    leftData.setOnLongClickListener(new OptionLongClickListener());
                }
                leftData.setOnClickListener(new OptionClickListener());
                if (editor) {
                    leftData.addView(getChoiceItem(indexLeft));
                }
                for (int i = 0; i < this.fixCol; i++) {
                    if (lengths.get(i) != 0) {
                        leftData.addView(getItemView(datas.get(i), lengths, i,
                                indexLeft));
                    }
                }
                if (indexLeft == 1) {
                    leftData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    leftData.getBackground().setAlpha(180);
                } else if (indexLeft % 2 == 0) {
                    leftData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor2.substring(0, 3)),
                            Integer.parseInt(rowBgColor2.substring(3, 6)),
                            Integer.parseInt(rowBgColor2.substring(6, 9))));
                    leftData.getBackground().setAlpha(180);
                } else if (indexLeft % 2 == 1) {
                    leftData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    leftData.getBackground().setAlpha(180);
                }
                ArrayList<LinearLayout> viewLine = new ArrayList<LinearLayout>();
                viewLine.add(leftData);
                int no = indexLeft - 1
                        + (this.navigator ? pageCount * (page - 1) : 0);
                if (choiceMap.get(no)) {
                    leftData.setVisibility(View.GONE);
                }
                tableLineViews.put(no, viewLine);


                CommonUtil.measureView(leftData);
                int h1 = leftData.getMeasuredHeight();


                rightData = new LinearLayout(ctx);
                rightData.setOrientation(LinearLayout.HORIZONTAL);
                rightData.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, lineHeight));
                rightData.setMinimumHeight(minHeight);
                rightData.setGravity(Gravity.CENTER);
                rightData.setTag(datas);
                if (this.optBtns.size() > 0) {
                    rightData
                            .setOnLongClickListener(new OptionLongClickListener());
                }
                rightData.setOnClickListener(new OptionClickListener());
                for (int i = this.fixCol; i < this.titlesArray.size(); i++) {
                    if (lengths.get(i) != 0) {
                        rightData.addView(getItemView(datas.get(i), lengths, i,
                                indexRight));
                    }
                }
                View btnView = this.getRowButton(datas);
                if (btnView != null)
                    rightData.addView(btnView);
                if (indexRight == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (indexRight % 2 == 0) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor2.substring(0, 3)),
                            Integer.parseInt(rowBgColor2.substring(3, 6)),
                            Integer.parseInt(rowBgColor2.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                } else if (indexRight % 2 == 1) {
                    rightData.setBackgroundColor(Color.rgb(
                            Integer.parseInt(rowBgColor.substring(0, 3)),
                            Integer.parseInt(rowBgColor.substring(3, 6)),
                            Integer.parseInt(rowBgColor.substring(6, 9))));
                    rightData.getBackground().setAlpha(180);
                }
                int no1 = indexRight - 1
                        + (this.navigator ? pageCount * (page - 1) : 0);
                if (choiceMap.get(no1)) {
                    rightData.setVisibility(View.GONE);
                }
                if (tableLineViews.containsKey(no1)) {
                    ArrayList<LinearLayout> viewLine1 = tableLineViews.get(no);
                    viewLine1.add(rightData);
                } else {
                    ArrayList<LinearLayout> viewLine1 = new ArrayList<LinearLayout>();
                    viewLine1.add(rightData);
                    tableLineViews.put(no, viewLine1);
                }
                CommonUtil.measureView(rightData);
                int h2 = rightData.getMeasuredHeight();

                if (h1 < h2) {
                    leftData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, h2));
                } else {
                    rightData.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT, h1));
                }


                leftDatas.addView(leftData);
                indexLeft++;


                rightDatas.addView(rightData);
                indexRight++;
            }
            if (navigator || scrollViewLeft.getChildCount() == 0)
                scrollViewLeft.addView(leftDatas);
            else {
                View v = scrollViewLeft.getChildAt(0);
                scrollViewLeft.removeAllViews();
                scrollViewLeft.addView(v);
            }

            if (navigator || scrollViewRightDatas.getChildCount() == 0)
                scrollViewRightDatas.addView(rightDatas);
            else {
                View v = scrollViewRightDatas.getChildAt(0);
                scrollViewRightDatas.removeAllViews();
                scrollViewRightDatas.addView(v);
            }
        } catch (Throwable e) {
            Log.e("dxs", "添加失败", e);
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_funexp);
            info = StringUtil.replace(info, "@@TT@@", "buildPartData");
            Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击监听事件
     * */
    private class OptionClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                if (click != null && !"".equals(click)) {
                    List<String> vals = (List<String>) view.getTag();
                    String str = "";
                    String vs[] = StringUtil.split(click, ":");
                    if (vs.length >= 2) {
                        String pageid = vs[1];
                        if (pageid == null
                                || pageid.toLowerCase().equals("null")
                                || pageid.equals(""))
                            return;
                        String params[];
                        if (vs.length >= 3) {
                            params = StringUtil.split(vs[2], ",");
                            for (String param : params) {
                                String kv[] = StringUtil.split(param, "=");
                                if (kv.length >= 2) {
                                    str += kv[0] + "="
                                            + vals.get(Integer.parseInt(kv[1]))
                                            + "\r\n";
                                }
                            }
                        }
                        if (click.startsWith("PAGE")) {
                            Intent intent = new Intent();
                            intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                            intent.putExtra("meip", "MEIP_PAGE=" + pageid
                                    + "\r\n" + str);
                            LocalBroadcastManager
                                    .getInstance(ctx).sendBroadcast(intent);
                        } else if (click.startsWith("OP")) {
                            Intent intent = new Intent();
                            intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.CLICK_ACTION);
                            intent.putExtra("meip", "MEIP_ACTION=" + pageid
                                    + "\r\n" + str);
                            LocalBroadcastManager
                                    .getInstance(ctx).sendBroadcast(intent);
                        }
                    }
                }
            } catch (Throwable e) {
                String clsName = this.getClass().getName();
                clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                String info = ctx.getString(R.string.axeac_toast_exp_click);
                Toast.makeText(ctx, clsName + info, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 点击长按监听事件
     * */
    private class OptionLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            CustomDialog.Builder btnsDialog = new CustomDialog.Builder(ctx);
            btnsDialog.setTitle(R.string.axeac_msg_choice);
            btnsDialog.setCancelable(false);
            ListView lv = new ListView(ctx);
            lv.setBackgroundColor(Color.WHITE);
            lv.getBackground().setAlpha(180);
            lv.setCacheColorHint(Color.TRANSPARENT);
            lv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            btnsDialog.setContentView(lv);
            CustomDialog dialog = btnsDialog.create();
            lv.setAdapter(new OptionAdapter(ctx, optBtns, dialog, view.getTag()));
            dialog.show();
            return true;
        }
    }

    /**
     * 当ScrollView滚动时调用的方法，为ScrollView设置监听
     * @param x 滚动后横坐标
     * @param y 滚动后纵坐标
     * @param oldx
     * 滚动前横坐标
     * @param oldy
     * 滚动前纵坐标
     * */
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
                                int oldx, int oldy) {
        if (scrollView == scrollViewLeft) {
            scrollViewRight.scrollTo(x, y);
        } else if (scrollView == scrollViewRight
                && scrollViewLeft.getVisibility() == View.VISIBLE) {
            scrollViewLeft.scrollTo(x, y);
        }
    }

    /**
     * 返回当前视图
     * */
    @Override
    public View getView() {
        if (editor)
            this.returnable = true;
        else
            this.returnable = false;
        return table;
    }

    @Override
    public String getValue() {
        if (!editor) {
            return null;
        }
        String returnVals = "";
        int addNo = 0;
        for (int i = 0; i < dataArray.size(); i++) {
            String lineVal = "";
            if (i < dataCount) {
                if (delRows.contains(i)) {
                    lineVal = "-" + compId + "_" + i + "=";
                    List<String> lineData = dataArray.get(i);
                    for (int j = 0; j < lineData.size(); j++) {
                        lineVal += lineData.get(j) + "||";
                    }
                } else {
                    boolean isEdit = false;
                    List<String> lineData = dataArray.get(i);
                    ArrayList<EditText> temps = editColViews.get(i);
                    int c = 0;
                    for (int j = 0; j < lengths.size(); j++) {
                        if (lengths.get(j) != 0 && editorCols.contains(j)) {
                            String curData = temps.get(c).getText().toString();
                            String oriData = lineData.get(j);
                            String info = filter(curData, j);
                            if (info.startsWith("FAILURE:")) {
                                return info;
                            }
                            if (!oriData.equals(curData)) {
                                isEdit = true;
                            }
                            lineVal += curData + "||";
                            c++;
                        } else {
                            lineVal += lineData.get(j) + "||";
                        }
                    }
                    if (isEdit) {
                        lineVal = "*" + compId + "_" + i + "=" + lineVal;
                    } else {
                        lineVal = compId + "_" + i + "=" + lineVal;
                    }
                }
            } else {
                if (addRows.contains(String.valueOf(i))) {
                    lineVal = "+" + compId + "_" + addNo + "=";
                    List<String> lineData = dataArray.get(i);
                    ArrayList<EditText> temps = addRowsViews.get(i);
                    int c = 0;
                    for (int j = 0; j < lengths.size(); j++) {
                        if (lengths.get(j) != 0) {
                            String curData = temps.get(c).getText().toString();
                            String info = filter(curData, j);
                            if (info.startsWith("FAILURE:")) {
                                return info;
                            }
                            lineVal += curData + "||";
                            c++;
                        } else {
                            lineVal += lineData.get(j) + "||";
                        }
                    }
                    addNo++;
                }
            }
            if (lineVal.endsWith("||")) {
                lineVal = lineVal.substring(0, lineVal.length() - 2);
            }
            if (!lineVal.equals("")) {
                returnVals += lineVal + "\r\n";
            }
        }
        if (returnVals.endsWith("\r\n")) {
            returnVals = returnVals.substring(0, returnVals.length() - 2);
        }
        return returnVals;
    }

    private String filter(String data, int col) {
        if (editorColsFilter.containsKey(col)) {
            String filter = editorColsFilter.get(col);
            if (!FilterUtils.doFilter(ctx, filter, data)) {
                String info = "FAILURE:";
                if (filter.toLowerCase().equals("notnull")) {
                    info = ctx.getString(R.string.axeac_toast_exp_notnil);
                    info = StringUtil.replace(info, "%%TT%%", ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                } else if (filter.startsWith("between")) {
                    info = ctx.getString(R.string.axeac_toast_exp_between);
                    info = StringUtil.replace(info, "%%TT%%", ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                    info = StringUtil.replace(info, "%%VAL%%", filter);
                    info = StringUtil.replace(info, "between:", "");
                    info = StringUtil.replace(info, "and",
                            ctx.getString(R.string.axeac_toast_exp_between_and));
                } else if (filter.startsWith("include")) {
                    info = ctx.getString(R.string.axeac_toast_exp_include);
                    info = StringUtil.replace(info, "%%TT%%", ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                    info = StringUtil.replace(info, "%%VAL%%", filter);
                    info = StringUtil.replace(info, "include:", "");
                    info = StringUtil.replaceAll(info, "||", ",");
                } else if (filter.startsWith("uninclude")) {
                    info = ctx.getString(R.string.axeac_toast_exp_uninclude);
                    info = StringUtil.replace(info, "%%TT%%", ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                    info = StringUtil.replace(info, "%%VAL%%", filter);
                    info = StringUtil.replace(info, "uninclude:", "");
                    info = StringUtil.replaceAll(info, "||", ",");
                } else if (filter.startsWith("regex")) {
                    info = ctx.getString(R.string.axeac_toast_exp_regex);
                    info = StringUtil.replace(info, "%%TT%%",ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                    info = StringUtil.replace(info, "%%VAL%%", filter);
                    info = StringUtil.replace(info, "regex:", "");
                } else {
                    info = ctx.getString(R.string.axeac_toast_exp_vilfail);
                    info = StringUtil.replace(info, "%%TT%%", ctx.getString(R.string.axeac_table_number,(col + 1)+""));
                    info = StringUtil.replace(info, "%%VAL%%", filter);
                }
                return info;
            }
        }
        return "";
    }

    @Override
    public void repaint() {
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