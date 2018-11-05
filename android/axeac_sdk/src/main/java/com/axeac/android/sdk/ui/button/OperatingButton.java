package com.axeac.android.sdk.ui.button;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.adapters.OperatingAidsAdapter;
import com.axeac.android.sdk.adapters.OperatingChooseAdapter;
import com.axeac.android.sdk.adapters.OperatingUsersAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.dialog.OperatingDialog;
import com.axeac.android.sdk.tools.LinkedHashtable;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.GPS;
import com.axeac.android.sdk.ui.PhotoSelector;
import com.axeac.android.sdk.ui.Table;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.FilterUtils;
import com.axeac.android.sdk.utils.StaticObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * describe:Operation button (request network)
 * 操作按钮
 * <br>执行操作和跳转页面和工作流的按钮，当点击返回参数包括：
 *<br>页面上所有注册为true，且可以返回的控件。
 *<br>option=被点击按钮ID
 *<br>optionName = 被点击按钮文本
 *<br>nextActivity = 用户选中的下一环节
 *<br>nextActivityUsers = 下用户选择的下一环节的操作用户列表，以逗号分开，例如840070,840052。
 * @author axeac
 * @version 1.0.0
 */
public class OperatingButton extends SystemButton {

    /**
     * 文本方向  bottom || right
     * */
    private String textPosition;
    /**
     * 页面ID，优先级高
     * */
    private String pageId = "";
    /**
     * 操作ID，优先级低
     * */
    private String opId = "";
    private String opParam1 = "";
    private String opParam2 = "";
    private String opParam3 = "";
    private String opParam4 = "";
    private String opParam5 = "";
    private boolean validate = true;
    private List<String> aidsDataKeys = new ArrayList<String>();
    private List<String> aidsDataVals = new ArrayList<String>();
    private Map<String, ArrayList<String>> usersDataKeys = new HashMap<String, ArrayList<String>>();
    private Map<String, ArrayList<String>> usersDataVals = new HashMap<String, ArrayList<String>>();

    private Map<Integer, Integer> keys_keys = new HashMap<Integer, Integer>();
    private ArrayList<String> normalUsersDataKeys = new ArrayList<String>();
    private ArrayList<String> normalUsersDataVals = new ArrayList<String>();

    /**
     * 组件id
     * */
    private String compId;
    /**
     * Property对象
     * */
    private Property items;

    private String nextActivityUsers = "";
    private String nextActivityUserNames = "";

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    // whether to test the input
    // 是否检验输入
    private boolean isFilter = true;

    public OperatingButton(Activity ctx, String compId) {
        super(ctx);
        this.compId = compId;
        this.returnable = true;
    }

    /**
     * 返回是否验证
     * @return
     * 是否验证
     * */
    public boolean isValidate() {
        return validate;
    }

    /**
     * 设置是否验证
     * @param validate
     * true为验证，false为不验证
     * */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    /**
     * 设置文本方向
     * @param textPosition
     * 文本方向
     * */
    public void setTextPosition(String textPosition) {
        this.textPosition = textPosition;
        System.out.println(this.textPosition);
    }

    /**
     * 设置页面id
     * */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * 设置操作id
     * @param opId
     * 操作id
     * */
    public void setOpId(String opId) {
        this.opId = opId;
    }

    public void setOpParam1(String opParam1) {
        this.opParam1 = opParam1;
    }

    public void setOpParam2(String opParam2) {
        this.opParam2 = opParam2;
    }

    public void setOpParam3(String opParam3) {
        this.opParam3 = opParam3;
    }

    public void setOpParam4(String opParam4) {
        this.opParam4 = opParam4;
    }

    public void setOpParam5(String opParam5) {
        this.opParam5 = opParam5;
    }

    public void addActivityAid(String aidId, String aidVal) {
        if (aidVal != null && !"".equals(aidVal)) {
            aidsDataKeys.add(aidId);
            aidsDataVals.add(aidVal);
            usersDataKeys.put(aidId, new ArrayList<String>());
            usersDataVals.put(aidId, new ArrayList<String>());
        }
    }

    public void addUsersFromNormal(String userId, String username) {
        if (username == null || username.equals("")) {
            username = ctx.getString(R.string.axeac_msg_unknow);
        }
        normalUsersDataKeys.add(userId);
        normalUsersDataVals.add(username);
    }

    public void setItems(Property items) {
        this.items = items;
    }

    /**
     * 执行方法
     * */
    @Override
    public void executeBtn() {
        if (aidsDataKeys.size() == 0 && normalUsersDataKeys.size() == 0) {
            executeClick("");
            return;
        }
        if (normalUsersDataKeys.size() != 0) {
            aidsDataKeys.clear();
            aidsDataVals.clear();
            aidsDataKeys.add("-1");
            aidsDataVals.add(ctx.getString(R.string.axeac_msg_default_skip));
            usersDataKeys.clear();
            usersDataVals.clear();
            usersDataKeys.put("-1", normalUsersDataKeys);
            usersDataVals.put("-1", normalUsersDataVals);
        } else {
            for (String aid : aidsDataKeys) {
                usersDataKeys.get(aid).clear();
                usersDataVals.get(aid).clear();
                LinkedHashtable props = items.searchSubKey(compId + "." + aid + ".");
                if (props != null && props.size() > 0) {
                    Vector<?> subkeys = props.linkedKeys();
                    for (int i = 0; subkeys != null && i < subkeys.size(); i++) {
                        String userId = (String) subkeys.elementAt(i);
                        String username = (String) props.get(userId);
                        usersDataKeys.get(aid).add(userId);
                        usersDataVals.get(aid).add(username);
                    }
                }
            }
        }

        final OperatingDialog.Builder builder = new OperatingDialog.Builder(ctx);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String nextActivity = aidsDataKeys.get(aidIndex);
                String arg = "option = " + compId + "\r\n";
                arg += "optionName = " + text + "\r\n";
                if (nextActivity.equals("-1")) {
                    arg += "nextActivity = \r\n";
                } else {
                    arg += "nextActivity = " + nextActivity + "\r\n";
                    arg += "nextActivityName = " + aidsDataVals.get(aidIndex)
                            + "\r\n";
                }
                if (chooseAdapter.getCount() == 0) {
                    arg += "nextActivityUsers = \r\n";
                    arg += "nextActivityUserNames = \r\n";
                } else {
                    String nextActivityUsers = "";
                    String nextActivityUserNames = "";
                    for (int i = 0; i < chooseAdapter.getCount(); i++) {
                        nextActivityUsers += chooseAdapter.getIdlist().get(i) + ",";
                        nextActivityUserNames += chooseAdapter.getItem(i).toString() + ",";
                    }
                    nextActivityUsers = nextActivityUsers.substring(0,
                            nextActivityUsers.length() - 1);
                    nextActivityUserNames = nextActivityUserNames.substring(0,
                            nextActivityUserNames.length() - 1);
                    arg += "nextActivityUsers = " + nextActivityUsers + "\r\n";
                    arg += "nextActivityUserNames = " + nextActivityUserNames
                            + "\r\n";
                }
                executeClick(arg);
            }
        });
        builder.setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        aidsListView = builder.getListViewForAids();
        OperatingAidsAdapter aidsAdapter = new OperatingAidsAdapter(ctx, aidsDataVals);
        aidsListView.setAdapter(aidsAdapter);
        aidsListView.setOnItemClickListener(aidsListViewClickListener(aidsAdapter));
        usersListView = builder.getListViewForUsers();

        choiceListView = builder.getListViewForChoose();
        chooseAdapter = new OperatingChooseAdapter(ctx);
        choiceListView.setAdapter(chooseAdapter);

        OperatingUsersAdapter usersAdapter = new OperatingUsersAdapter(ctx, usersDataVals.get(aidsDataKeys.get(0)), usersDataKeys.get(aidsDataKeys.get(0)));
        usersListView.setAdapter(usersAdapter);
        usersListView.setOnItemClickListener(usersListViewClickListener(usersAdapter));

        List<String> users = usersDataVals.get(aidsDataKeys.get(aidIndex));
        for (int i = 0; i < users.size(); i++) {
            keys_keys.put(i, i);
        }
        builder.getSreachBtn().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String txt = builder.getSreachTxt().getText().toString();
                if (txt == null || "".equals(txt.trim())) {
                    List<String> users = usersDataVals.get(aidsDataKeys.get(aidIndex));
                    for (int i = 0; i < users.size(); i++) {
                        keys_keys.put(i, i);
                    }
                    OperatingUsersAdapter usersAdapter = new OperatingUsersAdapter(
                            ctx, users, usersDataKeys.get(aidsDataKeys.get(aidIndex)));
                    usersListView.setAdapter(usersAdapter);
                    usersAdapter.addChoose(chooseAdapter.getList());
                    usersListView.setOnItemClickListener(usersListViewClickListener(usersAdapter));
                } else {
                    List<String> newusers = new ArrayList<String>();
                    List<String> newKeys = new ArrayList<String>();
                    List<String> users = usersDataVals.get(aidsDataKeys.get(aidIndex));
                    List<String> keys = usersDataKeys.get(aidsDataKeys.get(aidIndex));
                    int j = 0;
                    for (int i = 0; i < users.size(); i++) {
                        String name = users.get(i);
                        String key = keys.get(i);
                        if (name.indexOf(txt) != -1) {
                            newusers.add(name);
                            newKeys.add(key);
                            keys_keys.put(j, i);
                            j++;
                        }
                    }
                    OperatingUsersAdapter usersAdapter = new OperatingUsersAdapter(
                            ctx, newusers, newKeys);
                    usersListView.setAdapter(usersAdapter);
                    usersAdapter.addChoose(chooseAdapter.getList());
                    usersListView.setOnItemClickListener(usersListViewClickListener(usersAdapter));
                }
            }

        });
    }

    private int aidIndex = 0;
    private ListView aidsListView;
    private ListView usersListView;
    private ListView choiceListView;
    /**
     * OperatingChooseAdapter对象
     * */
    private OperatingChooseAdapter chooseAdapter;

    private AdapterView.OnItemClickListener aidsListViewClickListener(
            final OperatingAidsAdapter aidsAdapter) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (aidsAdapter.getIsCheckSelected().get(position)) {
                    return;
                }
                chooseAdapter.clear();
                for (int i = 0; i < aidsAdapter.getIsCheckSelected().size(); i++) {
                    if (i == position) {
                        aidsAdapter.getItemViewMap().get(position).text.setBackgroundResource(R.drawable.axeac_operating_choice);
                        aidsAdapter.getIsCheckSelected().put(position, true);
                        aidIndex = position;
                    } else {
                        aidsAdapter.getItemViewMap().get(i).text.setBackgroundResource(R.color.white);
                        aidsAdapter.getIsCheckSelected().put(i, false);
                    }
                }
                List<String> users = usersDataVals.get(aidsDataKeys.get(aidIndex));
                for (int i = 0; i < users.size(); i++) {
                    keys_keys.put(i, i);
                }
                OperatingUsersAdapter usersAdapter = new OperatingUsersAdapter(
                        ctx, usersDataVals.get(aidsDataKeys.get(position)), usersDataKeys.get(aidsDataKeys.get(position)));
                usersListView.setAdapter(usersAdapter);
                usersListView.setOnItemClickListener(usersListViewClickListener(usersAdapter));
                aidsAdapter.notifyDataSetChanged();
            }
        };
    }

    private AdapterView.OnItemClickListener usersListViewClickListener(
            final OperatingUsersAdapter usersAdapter) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String name = usersAdapter.getItem(position).toString();
                if (usersAdapter.getChooseList().contains(name)) {
                    usersAdapter.getItemViewMap().get(position).img.setVisibility(View.INVISIBLE);
                    usersAdapter.getChooseList().remove(name);
                    chooseAdapter.remove(name, usersAdapter.getIdlist().get(position));
                } else {
                    usersAdapter.getItemViewMap().get(position).img.setVisibility(View.VISIBLE);
                    usersAdapter.getChooseList().add(name);
                    chooseAdapter.add(name, usersAdapter.getIdlist().get(position));
                }
                usersAdapter.notifyDataSetChanged();
            }
        };
    }

    /**
     * 处理点击事件
     * @param arg
     * 字段值
     * */
    private void executeClick(String arg) {
        try {
            if (pageId.equals("") && opId.equals("")) {
                return;
            }
            String parm = "";
            if (!pageId.equals("")) {
                parm = "MEIP_PAGE=" + pageId + "\r\n";
            } else {
                parm = "MEIP_ACTION=" + opId + "\r\n";
            }
            String[] keys = StaticObject.ReturnComponentMap.keySet().toArray(
                    new String[0]);
            if (isFilter)
                for (String key : keys) {
                    Component comp = StaticObject.ReturnComponentMap.get(key);
                    if (comp != null && comp.returnable == true) {
                        if (comp.getClass().getName().equals(Table.class.getName())) {
                            if (comp.getValue() != null
                                    && comp.getValue().startsWith("FAILURE:")) {
                                CustomDialog.Builder builder = new CustomDialog.Builder(
                                        ctx);
                                builder.setTitle(R.string.axeac_toast_exp_errortoast);
                                builder.setMessage(comp.getValue().replace(
                                        "FAILURE:", ""));
                                builder.setCancelable(true);
                                builder.setNeutralButton(R.string.axeac_msg_close,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.create().show();
                                return;
                            }
                        }
                        if (this.isValidate() && !FilterUtils
                                .doFilter(ctx, comp.filter, comp.getValue())) {
                            CustomDialog.Builder builder = new CustomDialog.Builder(
                                    ctx);
                            builder.setTitle(R.string.axeac_toast_exp_errortoast);
                            if (comp.filterMsg.equals("")) {
                                if (comp.filter.toLowerCase().equals("notnull")) {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_notnil);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    builder.setMessage(info);
                                } else if (comp.filter.startsWith("between")) {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_between);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    info = StringUtil.replace(info, "%%VAL%%",
                                            comp.filter);
                                    info = StringUtil.replace(info, "between:", "");
                                    info = StringUtil
                                            .replace(
                                                    info,
                                                    "and",
                                                    ctx.getString(R.string.axeac_toast_exp_between_and));
                                    builder.setMessage(info);
                                } else if (comp.filter.startsWith("include")) {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_include);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    info = StringUtil.replace(info, "%%VAL%%",
                                            comp.filter);
                                    info = StringUtil.replace(info, "include:", "");
                                    info = StringUtil.replaceAll(info, "||", ",");
                                    builder.setMessage(info);
                                } else if (comp.filter.startsWith("uninclude")) {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_uninclude);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    info = StringUtil.replace(info, "%%VAL%%",
                                            comp.filter);
                                    info = StringUtil.replace(info, "uninclude:",
                                            "");
                                    info = StringUtil.replaceAll(info, "||", ",");
                                    builder.setMessage(info);
                                } else if (comp.filter.startsWith("regex")) {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_regex);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    info = StringUtil.replace(info, "%%VAL%%",
                                            comp.filter);
                                    info = StringUtil.replace(info, "regex:", "");
                                    builder.setMessage(info);
                                } else {
                                    String info = ctx
                                            .getString(R.string.axeac_toast_exp_vilfail);
                                    if (comp instanceof LabelComponent) {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                ((LabelComponent) comp).getLabel());
                                    } else {
                                        info = StringUtil.replace(info, "%%TT%%",
                                                key);
                                    }
                                    info = StringUtil.replace(info, "%%VAL%%",
                                            comp.filter);
                                    builder.setMessage(info);
                                }
                            } else {
                                builder.setMessage(comp.filterMsg);
                            }
                            builder.setCancelable(true);
                            builder.setNeutralButton(R.string.axeac_msg_close,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                            return;
                        }
                    }
                }
            for (String key : keys) {
                Component comp = StaticObject.ReturnComponentMap.get(key);
                if (comp != null && comp.returnable == true) {
                    String val = comp.getValue();
                    if (val == null)
                        val = "";
                    if (comp.getClass().getName().equals(Table.class.getName())) {
                        parm += val + "\r\n";
                    } else if (comp.getClass().getName()
                            .equals(GPS.class.getName())) {
                        GPS gps = (GPS) comp;
                        parm += key + "=" + val + "\r\n" + key + "_X="
                                + gps.getX() + "\r\n" + key + "_Y="
                                + gps.getY() + "\r\n";
                    } else if(comp.getClass().getName()
                            .equals(PhotoSelector.class.getName())){
                        if (val.indexOf(",")!=-1) {
                            String[] strs = val.split(",");
                            for (int i=0;i<strs.length;i++){
                                parm += key + i  + "=" + strs[i] + "\r\n";
                            }
                            if(strs.length<6){
                                for (int i=strs.length;i<6;i++){
                                    parm += key + i + "=" + "" + "\r\n";
                                }
                            }
                        }else {
                            parm += key + "0=" + val + "\r\n";
                            parm += key + "1=" + "" + "\r\n";
                            parm += key + "2=" + "" + "\r\n";
                            parm += key + "3=" + "" + "\r\n";
                            parm += key + "4=" + "" + "\r\n";
                            parm += key + "5=" + "" + "\r\n";
                        }
                    }else {
                        parm += key + "=" + val + "\r\n";
                    }
                }
            }
            parm += arg;
            parm += "opparam1 = " + opParam1 + "\r\n";
            parm += "opparam2 = " + opParam2 + "\r\n";
            parm += "opparam3 = " + opParam3 + "\r\n";
            parm += "opparam4 = " + opParam4 + "\r\n";
            parm += "opparam5 = " + opParam5 + "\r\n";
            Intent intent = new Intent();
            intent.setAction(StaticObject.ismenuclick == true ? StaticObject.MENU_CLICK_ACTION : StaticObject.BUTTON_ACTION);
            intent.putExtra("meip", parm);
            LocalBroadcastManager
                    .getInstance(ctx).sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
            String info = ctx.getString(R.string.axeac_toast_exp_execop);
            info = StringUtil.replace(info, "@@TT@@", this.text);
            Toast.makeText(ctx, info, Toast.LENGTH_SHORT).show();
        }
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