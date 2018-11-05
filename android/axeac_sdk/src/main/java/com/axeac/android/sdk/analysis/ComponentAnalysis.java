package com.axeac.android.sdk.analysis;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.ui.Adjunct;
import com.axeac.android.sdk.ui.BackLog;
import com.axeac.android.sdk.ui.FunList;
import com.axeac.android.sdk.ui.Image;
import com.axeac.android.sdk.ui.Bar;
import com.axeac.android.sdk.ui.Gauge;
import com.axeac.android.sdk.ui.Mixed;
import com.axeac.android.sdk.ui.Column;
import com.axeac.android.sdk.ui.Code2D;
import com.axeac.android.sdk.ui.CodeScan;
import com.axeac.android.sdk.ui.FileSelector;
import com.axeac.android.sdk.ui.Form;
import com.axeac.android.sdk.ui.GPS;
import com.axeac.android.sdk.ui.HiddenText;
import com.axeac.android.sdk.ui.HtmlContent;
import com.axeac.android.sdk.ui.HtmlListView;
import com.axeac.android.sdk.ui.InfoButton;
import com.axeac.android.sdk.ui.LabelDate;
import com.axeac.android.sdk.ui.LabelList;
import com.axeac.android.sdk.ui.LabelSwitch;
import com.axeac.android.sdk.ui.LabelText;
import com.axeac.android.sdk.ui.LabelTime;
import com.axeac.android.sdk.ui.Line;
import com.axeac.android.sdk.ui.Area;
import com.axeac.android.sdk.ui.Map;
import com.axeac.android.sdk.ui.PhotoSelector;
import com.axeac.android.sdk.ui.Pie;
import com.axeac.android.sdk.ui.Player;
import com.axeac.android.sdk.ui.Record;
import com.axeac.android.sdk.ui.Sign;
import com.axeac.android.sdk.ui.Table;
import com.axeac.android.sdk.ui.WebBrowser;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.ui.button.IndexButton;
import com.axeac.android.sdk.ui.button.OperatingButton;
import com.axeac.android.sdk.ui.button.SystemButton;
import com.axeac.android.sdk.ui.container.Container;
import com.axeac.android.sdk.ui.container.GroupContainer;
import com.axeac.android.sdk.ui.container.TabContainer;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.StaticObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * describe:interpreting data
 * <br>解析数据
 * @author axeac
 * @version 1.0.0
 */
public class ComponentAnalysis {

    private Activity ctx;

    private View parentView;

    public ComponentAnalysis(View parentView, Activity ctx, Property items) {
        this.parentView = parentView;
        this.ctx = ctx;
        init(items);
    }

    public ComponentAnalysis(Activity ctx, Property items) {
        this.ctx = ctx;
        init(items);
    }

    /**
     * 初始化
     * @param items
     * Property对象
     * */
    private void init(Property items) {
        LinkedHashtable vars = items.searchSubKey("var.");
        Vector<?> keys = vars.linkedKeys();
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.elementAt(i);
            String target = items.getProperty("var." + key);
            if (target.equals("Container")) {
                StaticObject.ComponentMap.put(key, new Container(ctx));
            } else if (target.equals("TabContainer")) {
                StaticObject.ComponentMap.put(key, new TabContainer(ctx));
            } else if (target.equals("GroupContainer")) {
                StaticObject.ComponentMap.put(key, new GroupContainer(ctx));
            } else if (target.equals("LabelText")) {
                StaticObject.ComponentMap.put(key, new LabelText(ctx));
            } else if (target.equals("LabelSwitch")) {
                StaticObject.ComponentMap.put(key, new LabelSwitch(ctx));
            } else if (target.equals("LabelDate")) {
                StaticObject.ComponentMap.put(key, new LabelDate(ctx));
            } else if (target.equals("LabelTime")) {
                StaticObject.ComponentMap.put(key, new LabelTime(ctx));
            } else if (target.equals("LabelList")) {
                StaticObject.ComponentMap.put(key, new LabelList(ctx));
            } else if (target.equals("HiddenText")) {
                StaticObject.ComponentMap.put(key, new HiddenText(ctx));
            } else if (target.equals("Table")) {
                StaticObject.ComponentMap.put(key, new Table(ctx));
            } else if (target.equals("Record")) {
                StaticObject.ComponentMap.put(key, new Record(ctx));
            } else if (target.equals("PhotoSelector")) {
                StaticObject.ComponentMap.put(key, new PhotoSelector(ctx));
            } else if (target.equals("FileSelector")) {
                StaticObject.ComponentMap.put(key, new FileSelector(ctx));
            } else if (target.equals("GPS")) {
                StaticObject.ComponentMap.put(key, new GPS(ctx));
            } else if (target.equals("Map")) {
                StaticObject.ComponentMap.put(key, new Map(ctx));
            } else if (target.equals("Code2D")) {
                StaticObject.ComponentMap.put(key, new Code2D(ctx));
            } else if (target.equals("CodeScan")) {
                StaticObject.ComponentMap.put(key, new CodeScan(ctx));
            } else if (target.equals("HtmlListView")) {
                StaticObject.ComponentMap.put(
                        key,
                        new HtmlListView(ctx, items.getProperty("Form.id",
                                "IndexId") + key));
            } else if (target.equals("HtmlContent")) {
                StaticObject.ComponentMap.put(key, new HtmlContent(ctx, key));
            } else if (target.equals("WebView")) {
                StaticObject.ComponentMap.put(key, new WebBrowser(ctx));
            } else if (target.equals("Player")) {
                StaticObject.ComponentMap.put(key, new Player(ctx));
            } else if (target.equals("Image")) {
                StaticObject.ComponentMap.put(key,new Image(ctx));
            } else if (target.equals("Adjunct")) {
                StaticObject.ComponentMap.put(key, new Adjunct(ctx));
            } else if (target.equals("Column")) {
                StaticObject.ComponentMap.put(key, new Column(ctx));
            }  else if (target.equals("Mixed")) {
                StaticObject.ComponentMap.put(key, new Mixed(ctx));
            } else if (target.equals("Bar")) {
                StaticObject.ComponentMap.put(key, new Bar(ctx));
            } else if (target.equals("Line")) {
                StaticObject.ComponentMap.put(key, new Line(ctx));
            } else if (target.equals("Area")) {
                StaticObject.ComponentMap.put(key, new Area(ctx));
            } else if (target.equals("Pie")) {
                StaticObject.ComponentMap.put(key, new Pie(ctx));
            } else if (target.equals("Gauge")) {
                StaticObject.ComponentMap.put(key, new Gauge(ctx));
            } else if (target.equals("InfoButton")) {
                StaticObject.ComponentMap.put(key, new InfoButton(ctx));
            } else if (target.equals("IndexButton")) {
                StaticObject.ComponentMap.put(key, new IndexButton(ctx));
            } else if (target.equals("Operating") || target.equals("OperatingButton")) {
                StaticObject.ComponentMap.put(key, new OperatingButton(ctx, key));
            } else if (target.equals("BackLog")) {
                StaticObject.ComponentMap.put(key, new BackLog(ctx));
            } else if (target.equals("Sign")){
                StaticObject.ComponentMap.put(key,new Sign(ctx));
            } else if (target.equals("Image")){
                StaticObject.ComponentMap.put(key,new Image(ctx));
            } else if (target.equals("FunList")){
                StaticObject.ComponentMap.put(key,new FunList(ctx));
            }
        }
    }

    /**
     * 获得Form表单
     * @param items
     * Property对象
     * */
    public Form analysis(Property items) {
        if (FileUtils.isStoreLogToSD && FileUtils.checkSDCard()) {
            File responseFolder = new File(FileUtils.getSDCardPath()
                    + FileUtils.KHPATH + "/ResponseFolder");
            if (!responseFolder.exists()) {
                responseFolder.mkdirs();
            }
            FileUtils.writeFile(items.toString(), responseFolder.getPath()
                    + "/" + System.currentTimeMillis() + ".txt");
        }
        long times = System.currentTimeMillis();
        LinkedHashtable vars = items.searchSubKey("var.");
        Vector<?> keys = vars.linkedKeys();
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.elementAt(i);
            String target = items.getProperty("var." + key);
            if (StaticObject.ComponentMap.get(key) == null)
                continue;
            StaticObject.ComponentMap.get(key).setId(key);
            if (target.equals("Container")) {
                Container view = (Container) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("TabContainer")) {
                TabContainer view = (TabContainer) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("GroupContainer")) {
                GroupContainer view = (GroupContainer) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("LabelText")) {
                LabelText view = (LabelText) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("LabelSwitch")) {
                LabelSwitch view = (LabelSwitch) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("LabelDate")) {
                LabelDate view = (LabelDate) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("LabelTime")) {
                LabelTime view = (LabelTime) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("LabelList")) {
                LabelList view = (LabelList) StaticObject.ComponentMap.get(key);
                view.setAnalysis(this);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("HiddenText")) {
                HiddenText view = (HiddenText) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Table")) {
                Table view = (Table) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                view.setFormId(items.getProperty("Form.id"));
                view.setCompId(key);
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Record")) {
                Record view = (Record) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("PhotoSelector")) {
                PhotoSelector view = (PhotoSelector) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Sign")){
                Sign view = (Sign)StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view,props);
                continue;
            }
            if (target.equals("FileSelector")) {
                FileSelector view = (FileSelector) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("GPS")) {
                GPS view = (GPS) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Map")) {
                Map view = (Map) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Code2D")) {
                Code2D view = (Code2D) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("CodeScan")) {
                CodeScan view = (CodeScan) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("HtmlListView")) {
                HtmlListView view = (HtmlListView) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
            }
            if (target.equals("HtmlContent")) {
                HtmlContent view = (HtmlContent) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("WebView")) {
                WebBrowser view = (WebBrowser) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Player")) {
                Player view = (Player) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Image")) {
                Image view = (Image) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Adjunct")) {
                Adjunct view = (Adjunct) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Column")) {
                Column view = (Column) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Mixed")) {
                Mixed view = (Mixed) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Bar")) {
                Bar view = (Bar) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Line")) {
                Line view = (Line) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Area")) {
                Area view = (Area) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Pie")) {
                Pie view = (Pie) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Gauge")) {
                Gauge view = (Gauge) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("InfoButton")) {
                InfoButton view = (InfoButton) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("IndexButton")) {
                IndexButton view = (IndexButton) StaticObject.ComponentMap
                        .get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Operating") || target.equals("OperatingButton")) {
                OperatingButton view = (OperatingButton) StaticObject.ComponentMap
                        .get(key);
                view.setItems(items);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("BackLog")) {
                BackLog view = (BackLog) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("Image")) {
                Image view = (Image) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
            if (target.equals("FunList")) {
                FunList view = (FunList) StaticObject.ComponentMap.get(key);
                LinkedHashtable props = items.searchSubKey(key + ".");
                this.setComponent(view, props);
                continue;
            }
        }
        for (int i = 0; i < keys.size(); i++) {
            Component comp = StaticObject.ComponentMap.get((String) keys
                    .elementAt(i));
            if (comp != null) {
                String clsName = comp.getClass().getName();
                clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                try {
                    if (clsName.equals("HtmlListView")) {
                        comp.execute();
                        ((HtmlListView) comp).execute(HtmlListView.pageIndexMap
                                .get(items.getProperty("Form.id", "IndexId")
                                        + (String) keys.elementAt(i)));
                    }
                    if (!clsName.equals("ListViewTemplate")) {
                        comp.execute();
                    }
                    if (clsName.equals("Container")
                            || clsName.equals("GroupContainer")
                            || clsName.equals("TabContainer")) {
                        Container c = (Container) comp;
                        ArrayList<String> removeComps = c.getRemoveComps();
                        for (String compId : removeComps) {
                            c.removeComp(compId);
                        }
                        if (c.isClear()) {
                            c.clear();
                        }
                    }
                    if (clsName.equals("Code2D")) {
                        Code2D c = (Code2D) comp;
                        if (c.isClear()) {
                            c.clear();
                        }
                        if (c.isRefresh()) {
                            c.refresh();
                        }
                    }
                } catch (Throwable e) {
                    String msg = ctx.getString(R.string.axeac_fail_warning,keys.elementAt(i),clsName);
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

                    Log.e("DFS", msg, e);
                }
            }
        }
        if (items.getProperty("Form") == null) {
            return null;
        }
        Form view = new Form(parentView, ctx);
        LinkedHashtable props = items.searchSubKey("Form.");
        this.setComponent(view, props);
        view.execute();
        if (view.isClear()) {
            view.clear();
        }
        Log.w("TIMES", "解析数据 ：" + (System.currentTimeMillis() - times) + "毫秒");
        return view;
    }

    /**
     * 设置数据
     * @param items
     * Property对象
     * */
    public void setData(Property items) {
        LinkedHashtable vars = items.searchSubKey("var.");
        Vector<?> keys = vars.linkedKeys();
        for (int i = 0; i < keys.size(); i++) {
            Component comp = StaticObject.ComponentMap.get((String) keys
                    .elementAt(i));
            if (comp != null) {
                String clsName = comp.getClass().getName();
                clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                try {
                    if (clsName.equals("HtmlListView")) {
                        comp.execute();
                        ((HtmlListView) comp).execute(HtmlListView.pageIndexMap
                                .get(items.getProperty("Form.id", "IndexId")
                                        + (String) keys.elementAt(i)));
                    }
                    if (!clsName.equals("ListViewTemplate")) {
                        comp.execute();
                    }
                    if (clsName.equals("Container")
                            || clsName.equals("GroupContainer")
                            || clsName.equals("TabContainer")) {
                        Container c = (Container) comp;
                        ArrayList<String> removeComps = c.getRemoveComps();
                        for (String compId : removeComps) {
                            c.removeComp(compId);
                        }
                        if (c.isClear()) {
                            c.clear();
                        }
                    }
                    if (clsName.equals("Code2D")) {
                        Code2D c = (Code2D) comp;
                        if (c.isClear()) {
                            c.clear();
                        }
                        if (c.isRefresh()) {
                            c.refresh();
                        }
                    }
                } catch (Throwable e) {
                    String msg = ctx.getString(R.string.axeac_fail_warning,keys.elementAt(i),clsName);
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                    Log.e("DFS", msg, e);
                }
            }
        }
    }

    /**
     * 设置组件
     * @param view
     * 组件视图
     * @param props
     * LinkedHashtable集合
     * */
    public void setComponent(Component view, LinkedHashtable props) {
        if (props != null && props.size() > 0) {
            Vector<?> subkeys = props.linkedKeys();
            if (subkeys != null && subkeys.size() > 0) {

                for (int j = 0; j < subkeys.size(); j++) {
                    String prop = (String) subkeys.elementAt(j);
                    String val = (String) props.get(prop);
                    String clsName = view.getClass().getName();
                    clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
                    if (clsName.equals("Form")) {
                        this.setForm((Form) view, prop, val);
                    } else if (clsName.equals("Container")) {
                        this.setContainer((Container) view, prop, val);
                    } else if (clsName.equals("TabContainer")) {
                        this.setTabContainer((TabContainer) view, prop, val);
                    } else if (clsName.equals("GroupContainer")) {
                        this.setGroupContainer((GroupContainer) view, prop, val);
                    } else if (clsName.equals("LabelText")) {
                        this.setLabelText((LabelText) view, prop, val);
                    } else if (clsName.equals("LabelSwitch")) {
                        this.setLabelSwitch((LabelSwitch) view, prop, val);
                    } else if (clsName.equals("LabelDate")) {
                        this.setLabelDate((LabelDate) view, prop, val);
                    } else if (clsName.equals("LabelTime")) {
                        this.setLabelTime((LabelTime) view, prop, val);
                    } else if (clsName.equals("LabelList")) {
                        this.setLabelList((LabelList) view, prop, val);
                    } else if (clsName.equals("HiddenText")) {
                        this.setHiddenText((HiddenText) view, prop, val);
                    } else if (clsName.equals("Table")) {
                        this.setTable((Table) view, prop, val);
                    } else if (clsName.equals("Record")) {
                        this.setRecord((Record) view, prop, val);
                    } else if (clsName.equals("PhotoSelector")) {
                        this.setPhotoSelector((PhotoSelector) view, prop, val);
                    } else if (clsName.equals("FileSelector")) {
                        this.setFileSelector((FileSelector) view, prop, val);
                    } else if (clsName.equals("GPS")) {
                        this.setGPS((GPS) view, prop, val);
                    } else if (clsName.equals("Map")) {
                        this.setMap((Map) view, prop, val);
                    } else if (clsName.equals("Code2D")) {
                        this.setCode2D((Code2D) view, prop, val);
                    } else if (clsName.equals("CodeScan")) {
                        this.setCodeScan((CodeScan) view, prop, val);
                    } else if (clsName.equals("HtmlListView")) {
                        this.setHtmlListView((HtmlListView) view, prop, val);
                    } else if (clsName.equals("HtmlContent")) {
                        this.setHtmlContent((HtmlContent) view, prop, val);
                    } else if (clsName.equals("WebBrowser")) {
                        this.setWebBrowser((WebBrowser) view, prop, val);
                    } else if (clsName.equals("Player")) {
                        this.setPlayer((Player) view, prop, val);
                    } else if (clsName.equals("Adjunct")) {
                        this.setAdjunct((Adjunct) view, prop, val);
                    } else if (clsName.equals("Column")) {
                        this.setColumn((Column) view, prop, val);
                    } else if (clsName.equals("Mixed")) {
                        this.setMixed((Mixed) view, prop, val);
                    } else if (clsName.equals("Bar")) {
                        this.setBar((Bar) view, prop, val);
                    } else if (clsName.equals("Line")) {
                        this.setLine((Line) view, prop, val);
                    } else if (clsName.equals("Area")) {
                        this.setArea((Area) view, prop, val);
                    } else if (clsName.equals("Pie")) {
                        this.setPie((Pie) view, prop, val);
                    } else if (clsName.equals("Gauge")) {
                        this.setGauge((Gauge) view, prop, val);
                    } else if (clsName.equals("InfoButton")) {
                        this.setInfoButton((InfoButton) view, prop, val);
                    } else if (clsName.equals("IndexButton")) {
                        this.setSystemButton((IndexButton) view, prop, val);
                    } else if (clsName.equals("OperatingButton")) {
                        this.setOperatingButton((OperatingButton) view, prop,
                                val);
                    } else if (clsName.equals("BackLog")) {
                        this.setBackLog((BackLog) view, prop, val);
                    } else if (clsName.equals("Sign")){
                        this.setSign((Sign)view,prop,val);
                    } else if (clsName.equals("Image")){
                        this.setImage((Image)view,prop,val);
                    } else if (clsName.equals("FunList")){
                        this.setFunList((FunList)view,prop,val);
                    }
                }
            }
        }
    }

    /**
     * 设置表单
     * @param view
     * 表单视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setForm(Form view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("layout")) {
            view.setLayout(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("defaultfont")) {
            view.setDefaultFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("padding")) {
            view.setPadding(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("itempadding")) {
            view.setItemPadding(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("message")) {
            view.setMessage(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("home")) {
            view.setHome("true".equals(val));
            return;
        }
        if (prop.trim().toLowerCase().equals("builddate")) {
            view.setBuildDate(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("lastmeip")) {
            view.setLastMEIP(val);
            return;
        }
        if (prop.startsWith("addButton.")) {
            view.addButton(prop.substring(10), CommonUtil.getBoolean(val));
            return;
        }
        if (prop.startsWith("add.")) {
            view.add(prop.substring(4), CommonUtil.getBoolean(val));
            return;
        }
        if (prop.trim().toLowerCase().equals("remove")) {
            view.remove(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("clear")) {
            view.setClear(true);
            return;
        }
    }

    /**
     * 设置待办列表
     * @param view
     * 待办列表视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setBackLog(BackLog view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("txt1")) {
            view.setTxt1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("txt2")) {
            view.setTxt2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("txt3")) {
            view.setTxt3(val);
            return;
        }
        if(prop.trim().toLowerCase().equals("icon")){
            view.setIcon(val);
            return;
        }
        if(prop.trim().toLowerCase().equals("gravity")){
            view.setGravity(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("count")) {
            view.setCount(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color")) {
            view.setColor(val);
            return;
        }
    }

    /**
     * 设置容器
     * @param view
     * 容器视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setContainer(Container view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("layout")) {
            view.setLayout(val.trim().toLowerCase());
            return;
        }
        if (prop.startsWith("add.")) {
            view.add(prop.substring(4), CommonUtil.getBoolean(val));
            return;
        }
        if (prop.trim().toLowerCase().equals("remove")) {
            view.remove(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("clear")) {
            view.setClear(true);
            return;
        }
    }

    /**
     * 设置分页签
     * @param view
     * 分页签视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setTabContainer(TabContainer view, String prop, String val) {
        setContainer(view, prop, val);
        if (prop.trim().toLowerCase().equals("direction")) {
            view.setDirection(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("activetab")) {
            view.setActiveTab(val);
            return;
        }
        if(prop.trim().toLowerCase().equals("isscroll")){
            view.setIsScroll(val);
            return;
        }
        if(prop.trim().toLowerCase().equals("title")){
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("names")) {
            view.setNames(val);
            return;
        }
        if (prop.startsWith("add.")) {
            view.add(prop.substring(4), val);
            return;
        }
    }

    /**
     * 设置组容器
     * @param view
     * 组容器视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setGroupContainer(GroupContainer view, String prop, String val) {
        setContainer(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("icon")) {
            view.setIcon(val);
            return;
        }
    }

    /**
     * 设置文本组件
     * @param view
     * 文本组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLabelText(LabelText view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("rows")) {
            view.setRows(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addIdeas")) {
            view.addIdeas(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subject")) {
            view.setSubject(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("mailto")) {
            view.setMailto(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("info")) {
            view.setInfo(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("buttonname")) {
            view.setButtonName(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("buttonicon")) {
            view.setButtonIcon(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("hint")){
            view.setHint(val);
            return;
        }
    }

    /**
     * 设置开关组件
     * @param view
     * 开关组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLabelSwitch(LabelSwitch view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("state")) {
            view.setState(val);
            return;
        }
    }

    /**
     * 设置日期组件
     * @param view
     * 日期组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLabelDate(LabelDate view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("times")) {
            view.setTimes(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("showday")) {
            view.setShowDay(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("format")) {
            view.setFormat(val);
            return;
        }
    }

    /**
     * 设置时间组件
     * @param view
     * 时间组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLabelTime(LabelTime view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("hour24")) {
            view.setHour24(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("minutestep")) {
            view.setMinuteStep(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("secondstep")) {
            view.setSecondStep(val);
            return;
        }
    }

    /**
     * 设置单选/多选列表组件
     * @param view
     * 单选/多选列表组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLabelList(LabelList view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("max")) {
            view.setMax(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("style")) {
            view.setStyle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("changeui")) {
            view.setChangeui(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("onchange")) {
            view.setOnChange(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addselected")) {
            view.addSelected(val);
            return;
        }
    }

    /**
     * 设置隐藏域组件
     * @param view
     * 隐藏域视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setHiddenText(HiddenText view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
    }

    /**
     * 设置表格组件
     * @param view
     * 表格视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setTable(Table view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("style")) {
            view.setStyle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("grivaty")) {
            view.setGrivaty(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("scroll")) {
            view.setScroll(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("navigator")) {
            view.setNavigator(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("fixcol")) {
            view.setFixCol(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("nowarp")) {
            view.setNowarp(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("pagecount")) {
            view.setPageCount(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("count")) {
            view.setCount(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("startpage")) {
            view.setStartPage(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("cachepage")) {
            view.setCachePage(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("page")) {
            view.setPage(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlebgcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setTitleBgColor(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().equals("rowbgcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setRowBgColor(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().equals("rowbgcolor2")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setRowBgColor2(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().equals("rowfontcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setRowFontColor(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().equals("rowfontcolor2")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setRowFontColor2(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().startsWith("colfontcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.colFontColor(val, prop);
            }
            return;
        }
        if (prop.trim().toLowerCase().startsWith("colbgcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.colBgColor(val, prop);
            }
            return;
        }
        if (prop.trim().toLowerCase().startsWith("cellfontcolor")) {
            view.cellFontColor(val, prop);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("cellbgcolor")) {
            view.cellBgColor(val, prop);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addtitle")) {
            view.addTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addrowbutton")) {
            view.addRowButton(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addoptionbutton")) {
            view.addOptionButton(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("editor")) {
            view.setEditor(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("editorcols")) {
            view.setEditorCols(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("editorcolsfilter")) {
            view.setEditorColsFilter(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("cellclick")) {
            view.setCellClick(val, prop);
            return;
        }
        if (prop.trim().toLowerCase().equals("id_titles")) {

            return;
        }
    }

    /**
     * 设置录制组件
     * @param view
     * 录制组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setRecord(Record view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filename")) {
            view.setFileName(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("option")) {
            view.setOption(val);
            return;
        }
    }

    /**
     * 设置图片选择组件
     * @param view
     * 图片选择组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setPhotoSelector(PhotoSelector view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("labeltext")) {
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("model")) {
            view.setModel(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("filename")) {
            view.addFileName(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("ftpurl")) {
            view.setFtpUrl(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("loadtype")) {
            view.setLoadType(val);
            return;
        }
    }

    /**
     * 设置签名组件
     * @param view
     * 签名组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setSign(Sign view,String prop,String val){
        setLabelComponent(view,prop,val);
        if (prop.trim().toLowerCase().equals("hint")){
            view.setHint(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("labeltext")){
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filename")){
            view.setFileName(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("loadtype")){
            view.setLoadType(Integer.parseInt(val));
            return;
        }
        if (prop.trim().toLowerCase().equals("ftpurl")){
            view.setFtpUrl(val);
            return;
        }
    }

    public void setImage(Image view, String prop, String val){
        setComponent(view,prop,val);
        if (prop.trim().toLowerCase().equals("iscycle")){
            view.setCycle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("delaytime")){
            view.setDelayTime(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("size")){
            view.setSize(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("indicator")){
            view.setIndicator(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")){
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addtitle")){
            view.addTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addclick")){
            view.addClick(val);
            return;
        }
    }

    public void setFunList(FunList view, String prop, String val){
        setComponent(view,prop,val);
        if (prop.trim().toLowerCase().equals("title")){
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("showtitle")){
            view.setShowTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")){
            view.addData(val);
            return;
        }
    }

    /**
     * 设置文件选择组件
     * @param view
     * 文件选择组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setFileSelector(FileSelector view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("labeltext")) {
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("path")) {
            view.setPath(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filter")) {
            view.setFilter(val);
            return;
        }
    }

    /**
     * 设置定位组件
     * @param view
     * 定位组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setGPS(GPS view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("x")) {
            view.setX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("y")) {
            view.setY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("position")) {
            view.setPosition(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("ignore")) {
            view.setIgnore(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("visiable")) {
            view.setVisiable(val);
            return;
        }
    }

    /**
     * 设置地图组件
     * @param view
     * 地图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setMap(Map view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("type")){
            view.setType(Integer.parseInt(val));
        }
        if (prop.trim().toLowerCase().equals("line")){
            view.setLine(val);
        }
        if (prop.trim().toLowerCase().startsWith("addposition")) {
            view.addPosition(val);
            return;
        }
    }

    /**
     * 设置二维码生成组件
     * @param view
     * 二维码生成组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setCode2D(Code2D view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("icon")) {
            view.setIcon(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("size")) {
            view.setSize(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addtext")) {
            view.addText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("clear")) {
            view.setClear(true);
            return;
        }
        if (prop.trim().toLowerCase().equals("refresh")) {
            view.setRefresh(true);
            return;
        }
        if (prop.trim().toLowerCase().equals("display")) {
            view.setDisplay(val);
            return;
        }
    }

    /**
     * 设置二维码扫描组件
     * @param view
     * 二维码扫描组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setCodeScan(CodeScan view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("option")) {
            view.setOption(val);
            return;
        }
    }

    /**
     * 设置html列表组件
     * @param view
     * html列表组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setHtmlListView(HtmlListView view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("headhtml")) {
            view.setHeadHtml(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("bottomhtml")) {
            view.setBottomHtml(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("template")) {
            view.setTemplate(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addrowdata")) {
            if (HtmlListView.pageIndexMap.get(view.getUuId()) == 0) {
                view.addRowData(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addrowclick")) {
            view.addRowClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("next")) {
            view.setNext(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("autoload")) {
            view.setAutoLoad(val);
            return;
        }
    }

    /**
     * 设置html元素组件
     * @param view
     * html元素组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setHtmlContent(HtmlContent view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("html")) {
            view.setHtml(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("property")) {
            view.setProperty(prop.trim(), val);
            return;
        }
//        if (prop.trim().toLowerCase().equals("autoheight")) {
//            view.setAutoHeight(val);
//            return;
//        }
        if (prop.trim().toLowerCase().startsWith("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("js")) {
            view.setJs(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("data")) {
            view.setData(prop.trim().toLowerCase(), val);
            return;
        }
    }

    /**
     * 设置网页组件
     * @param view
     * 网页组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setWebBrowser(WebBrowser view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("html")) {
            view.setHtml(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("url")) {
            view.setUrl(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("loadingtext")) {
            view.setLoadingText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("layout")) {
            if (val.equals("LayoutY")) {
                view.setRotation(false);
            } else if (val.equals("LayoutX")) {
                view.setRotation(true);
            }

            return;
        }

    }

    /**
     * 设置播放组件
     * @param view
     * 播放组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setPlayer(Player view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("url")) {
            view.setUrl(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("icon")) {
            view.setIcon(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("option")) {
            view.setOption(val);
            return;
        }
    }

    /**
     * 设置附件组件
     * @param view
     * 附件组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setAdjunct(Adjunct view, String prop, String val) {
        setLabelComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("preview")) {
            view.setPreview(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addpreviewurl")) {
            view.addPreviewUrl(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddataex")) {
            view.addDataEx(val);
            return;
        }
    }

    /**
     * 设置柱图组件
     * @param view
     * 柱图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setColumn(Column view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlex")) {
            view.setTitleX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlexfont")) {
            view.setTitleXFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titley")) {
            view.setTitleY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titleyfont")) {
            view.setTitleYFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddatax")) {
            view.addDataX(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置柱线图组件
     * @param view
     * 柱图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setMixed(Mixed view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlex")) {
            view.setTitleX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlexfont")) {
            view.setTitleXFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titley")) {
            view.setTitleY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titleyfont")) {
            view.setTitleYFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddatax")) {
            view.addDataX(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置柱图组件
     * @param view
     * 横向柱图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setBar(Bar view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("size")) {
            view.setSize(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlex")) {
            view.setTitleX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlexfont")) {
            view.setTitleXFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titley")) {
            view.setTitleY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titleyfont")) {
            view.setTitleYFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddatax")) {
            view.addDataX(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置折线图组件
     * @param view
     * 折线图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setLine(Line view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlex")) {
            view.setTitleX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlexfont")) {
            view.setTitleXFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titley")) {
            view.setTitleY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titleyfont")) {
            view.setTitleYFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("smoothed")) {
            view.setSmoothed(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddatax")) {
            view.addDataX(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置面积图组件
     * @param view
     * 面积图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setArea(Area view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlex")) {
            view.setTitleX(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlexfont")) {
            view.setTitleXFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titley")) {
            view.setTitleY(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titleyfont")) {
            view.setTitleYFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("smoothed")) {
            view.setSmoothed(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("areaalpha")) {
            view.setAreaAlpha(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddatax")) {
            view.addDataX(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置饼图组件
     * @param view
     * 饼图组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setPie(Pie view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("type")){
            view.setType(Integer.parseInt(val));
            return;
        }
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlecenter")) {
            view.setTitleCenter(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlecenterfont")) {
            view.setTitleCenterFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("perfont")) {
            view.setPerFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titletop")) {
            view.setTitleTop(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titletopfont")) {
            view.setTitleTopFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titletopsub")) {
            view.setTitleTopSub(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titletopsubfont")) {
            view.setTitleTopSubFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color1")) {
            view.setColor1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color2")) {
            view.setColor2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color3")) {
            view.setColor3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color4")) {
            view.setColor4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color5")) {
            view.setColor5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color6")) {
            view.setColor6(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color7")) {
            view.setColor7(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("color8")) {
            view.setColor8(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("click")) {
            view.setClick(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("adddata")) {
            view.addData(val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("additemclick")) {
            view.addItemClick(val);
            return;
        }
    }

    /**
     * 设置仪表盘组件
     * @param view
     * 仪表盘组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setGauge(Gauge view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("title")) {
            view.setTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("titlefont")) {
            view.setTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitle")) {
            view.setSubTitle(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("subtitlefont")) {
            view.setSubTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datatitlefont")) {
            view.setDataTitleFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("datafont")) {
            view.setDataFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("scalecolor")) {
            view.setScaleColor(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("progresscolor")) {
            view.setProgressColor(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("centercirclecolor")) {
            view.setCenterCircleColor(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("data")) {
            view.setData(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
    }

    /**
     * 设置信息按钮组件
     * @param view
     * 信息按钮组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setInfoButton(InfoButton view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("name")) {
            view.setName(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("type")) {
            view.setType(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("download")) {
            view.setDownload(val);
            return;
        }
    }

    /**
     * 设置Component组件
     * @param view
     * Component组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    private void setComponent(Component view, String prop, String val) {
        if (prop.trim().toLowerCase().equals("id")) {
            view.setId(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("width")) {
            view.setWidth(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("height")) {
            view.setHeight(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("styleid")) {
            view.setStyleId(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("bgcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setBgColor(val);
            }
            return;
        }
        if (prop.trim().toLowerCase().equals("bgimage")) {
            view.setBgImage(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("alpha")) {
            view.setAlpha(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("font")) {
            view.setFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filter")) {
            view.setFilter(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("visiable")) {
            view.setVisiable(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filtermsg")) {
            view.setFilterMsg(val);
            return;
        }
    }

    /**
     * 设置LabelComponent组件
     * @param view
     * LabelComponent组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    private void setLabelComponent(LabelComponent view, String prop, String val) {
        setComponent(view, prop, val);
        if(prop.trim().toLowerCase().equals("wrap")){
            view.setWrap(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("label")) {
            view.setLabel(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("labelwidth")) {
            view.setLabelWidth(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("readonly")) {
            view.setReadOnly(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("icon")) {
            view.setIcon(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("labelfont")) {
            view.setLabelFont(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("labeltextalign")) {
            view.setLabelTextAlign(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("labelbgcolor")) {
            if (CommonUtil.validRGBColor(val)) {
                view.setLabelBgColor(val);
            }
            return;
        }
    }

    /**
     * 设置操作按钮组件
     * @param view
     * 操作按钮组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setOperatingButton(OperatingButton view, String prop, String val) {
        setSystemButton(view, prop, val);
        if (prop.trim().toLowerCase().equals("textposition")) {
            view.setTextPosition(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("pageid")) {
            view.setPageId(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opid")) {
            view.setOpId(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opparam1")) {
            view.setOpParam1(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opparam2")) {
            view.setOpParam2(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opparam3")) {
            view.setOpParam3(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opparam4")) {
            view.setOpParam4(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("opparam5")) {
            view.setOpParam5(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("filter")) {
            view.setFilter(CommonUtil.getBoolean(val));
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addactivity")) {
            view.addActivityAid(
                    prop.trim().substring(prop.trim().indexOf(".") + 1), val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("addusers")) {
            view.addUsersFromNormal(
                    prop.trim().substring(prop.trim().indexOf(".") + 1), val);
            return;
        }
        if (prop.trim().toLowerCase().startsWith("validate")) {
            view.setValidate(CommonUtil.getBoolean(val));
            return;
        }
    }

    /**
     * 设置系统按钮组件
     * @param view
     * 系统按钮组件视图
     * @param prop
     * 字段名称字符串
     * @param val
     * 字段值字符串
     * */
    public void setSystemButton(SystemButton view, String prop, String val) {
        setComponent(view, prop, val);
        if (prop.trim().toLowerCase().equals("text")) {
            view.setText(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("icon")) {
            view.setIcon(val);
            return;
        }
        if (prop.trim().toLowerCase().equals("confirm")) {
            view.setConfirm(val);
            return;
        }
    }
}