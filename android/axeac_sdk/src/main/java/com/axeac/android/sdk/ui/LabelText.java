package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.SignActivity;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Text controls
 * 文本控件
 * @author axeac
 * @version 1.0.0
 */
public class LabelText extends LabelComponent {

    private RelativeLayout valLayout;
    private EditText textField;

    /**
     * 信息区文本
     * <br>默认值为空
     * */
    private String text = "";
    /**
     * 默认为-1，即单行文本，设置rows后，为大文本编辑区，在文本区下加上按钮“打开全屏编辑区”，
     * <br>自动检测大文本区内的文字数量，如果行数>=2*rows，自动弹出全屏编辑区。
     * */
    private int rows = -1;
    /**
     * 信息类型：Text||PHONE||MAIL||URL||AUTO，当类型为Phone时出现拨打电话及发送短信，
     * <br>当Mail时显示发送邮件，URL显示打开网址。AUTO为自动识别，根据“,”、“;”、“空格”
     * <br>拆分，分别判断，带@即.的为MAIL，HTTP://、HTTPS://、FTP:://开头为URL，存在多个时，
     * <br>在屏幕下方出现按钮列表选择，按钮文本附带信息内容，例如“拨打电话xxxxxx”，
     * <br>用户点击按钮进行相关操作。右侧固定两个占位符显示当前文本类型对应的图标。
     * <br>默认值为TEXT
     * */
    private String type = "TEXT";
    /**
     * 设置邮件时显示的标题内容，内容可使用当前页面内容，即替换字符串中的@@组件ID@@
     * <br>默认值为空
     * */
    private String subject = "";
    /**
     * 设置邮件时，要发送的邮件接收人
     * <br>默认值为空
     * */
    private String mailto = "";
    /**
     * 发短信、邮件时显示的文本内容，内容可使用当前页面内容@@组件ID@@
     * <br>默认值为空
     * */
    private String info = "";
    /**
     * 按钮名称
     * <br>默认值为空
     * */
    private String buttonName = "";
    /**
     * 按钮图标
     * <br>默认值为空
     * */
    private String buttonIcon = "";
    /**
     * 点击跳转事件判断，||分割，分为PAGE、OP、TT
     * */
    private String click = "";

    private String hint = "请输入";

    private List<String> ideas;

    public LabelText(Activity ctx) {
        super(ctx);
        this.returnable = true;
        ideas = new ArrayList<>();
    }

    public void addIdeas(String idea) {
        if (idea != null && !idea.trim().equals("") && !ideas.contains(idea)) {
            ideas.add(idea);
        }
    }

    /**
     * 为TextView设置显示文本
     * @param text
     * 显示的文本
     * */
    public void setValue(String text) {
        textField.setText(text);
    }

    /**
     * 设置显示文本
     * @param text
     * 显示文本
     * */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 设置编辑区行数
     * @param rows
     * 行数
     * */
    public void setRows(String rows) {
        if (CommonUtil.isNumeric(rows)) {
            this.rows = Integer.parseInt(rows);
        }
    }

    /**
     * 设置信息类型
     * @param type
     * 可选值 Text||PHONE||MAIL||URL||AUTO||TELPHONE
     * */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置邮件是显示的标题内容
     * @param subject
     * 标题内容
     * */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 设置邮件时，要发送的邮件接收人
     * @param mailto
     * 邮件接收人
     * */
    public void setMailto(String mailto) {
        this.mailto = mailto;
    }

    /**
     * 设置发短信、邮件时显示的文本内容
     * @param info
     * 文本内容
     * */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * 设置按钮文字
     * @param buttonName
     * 按钮文字
     * */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * 设置按钮图标
     * @param buttonIcon
     * 按钮图标
     * */
    public void setButtonIcon(String buttonIcon) {
        this.buttonIcon = buttonIcon;
    }

    /**
     * 设置点击事件跳转判断
     * @param click
     * 点击事件跳转判断
     * */
    public void setClick(String click) {
        this.click = click;
    }

    public void setHint(String hint){
        this.hint = hint;
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        initDialog();
    }

    private CustomDialog multDialog;
    private EditText multDialogEdit;

    /**
     * 初始化对话框
     * */
    private void initDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
        builder.setTitle(R.string.axeac_toast_exp_inputing);
        multDialogEdit = new EditText(ctx);
        multDialogEdit.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                (int) (StaticObject.deviceHeight * 0.6)));
        multDialogEdit.setGravity(Gravity.TOP);
        builder.setContentView(multDialogEdit);
        builder.setNegativeButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textField.setText(multDialogEdit.getText().toString());
                CharSequence cs = textField.getText();
                if (cs instanceof Spannable) {
                    Selection.setSelection((Spannable) cs, cs.length());
                }
                dialog.dismiss();
            }
        });
        multDialog = builder.create();
    }

//    /**
//     * 本类点击事件
//     * @param dialog
//     * CustomDialog对象
//     * @param msg
//     * 文本
//     * */
//    private View.OnClickListener listener(final CustomDialog dialog, final String msg) {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//                String text = "";
//                if (msg.equals("")) {
//                    text = textField.getText().toString();
//                } else {
//                    text = msg;
//                }
//                if ("".equals(text)) {
//                    return;
//                }
//                if (v.getId() == R.id.label_text_phone) {
//                    if (CommonUtil.isNumeric(text)) {
//                        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
//                        ctx.startActivity(call);
//                    } else {
//                        Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//                if (v.getId() == R.id.label_text_telphone) {
//                    if (CommonUtil.isNumeric(text)) {
//                        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
//                        ctx.startActivity(call);
//                    } else {
//                        Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//                if (v.getId() == R.id.label_text_msg) {
//                    if (CommonUtil.isNumeric(text)) {
//                        Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + text));
//                        sms.putExtra("sms_body", info);
//                        ctx.startActivity(sms);
//                    } else {
//                        Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//                if (v.getId() == R.id.label_text_mail) {
//                    Intent email = new Intent(Intent.ACTION_SEND);
//                    email.setType("plain/text");
//                    email.putExtra(Intent.EXTRA_EMAIL, text);
//                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
//                    email.putExtra(Intent.EXTRA_CC, mailto);
//                    email.putExtra(Intent.EXTRA_TEXT, info);
//                    ctx.startActivity(Intent.createChooser(email, ctx.getString(R.string.axeac_msg_type_sendmail_soft)));
//                }
//                if (v.getId() == R.id.label_text_url) {
//                    String url1 = "";
////                    if (text.startsWith("http:")) {
////                        url = text;
////                    } else {
////                        url = "http://" + text + "/";
////                    }
//                    if (text.startsWith("http:") || text.startsWith("https:")) {
//                        url1 = text;
//                    } else {
//                        boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
//                        url1 = (isHttps ? "https://" : "http://") + text + "/";
//                    }
//                    String url = Uri.encode(url1, "-![.:/,%?&=]");
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(url));
//                    ctx.startActivity(intent);
//                }
//                if (v.getId() == R.id.label_text_auto) {
//                    String temp = text.trim();
//                    ArrayList<String> argsList = new ArrayList<String>();
//                    while (temp.contains(",") || temp.contains(";") || temp.contains(" ")) {
//                        String tempStr = autoCheck(temp);
//                        if (!tempStr.trim().equals("")) {
//                            argsList.add(tempStr.trim());
//                        }
//                        temp = temp.substring(tempStr.length() + 1, temp.length()).trim();
//                    }
//                    temp = temp.trim();
//                    if (temp.length() > 0) {
//                        argsList.add(temp);
//                    }
//                    ArrayList<String> typeList = new ArrayList<String>();
//                    ArrayList<String> valueList = new ArrayList<String>();
//                    for (int i = 0; i < argsList.size(); i++) {
//                        if (argsList.get(i).contains("@") && argsList.get(i).contains(".")) {
//                            typeList.add("MAIL");
//                        } else if (argsList.get(i).startsWith("HTTP://") || argsList.get(i).startsWith("http://")
//                                || argsList.get(i).startsWith("HTTPS://") || argsList.get(i).startsWith("https://")
//                                || argsList.get(i).startsWith("FTP://") || argsList.get(i).startsWith("ftp://")) {
//                            typeList.add("URL");
//                        } else if (CommonUtil.isNumeric(argsList.get(i))) {
//                            typeList.add("PHONE");
//                        } else {
//                            typeList.add("TEXT");
//                        }
//                        valueList.add(argsList.get(i));
//                    }
//                    if (valueList.size() > 0) {
//                        CustomDialog.Builder autoBuilder = new CustomDialog.Builder(ctx);
//                        autoBuilder.setTitle(R.string.axeac_msg_choice);
//                        LinearLayout layout = new LinearLayout(ctx);
//                        layout.setLayoutParams(new LinearLayout.LayoutParams(
//                                LinearLayout.LayoutParams.FILL_PARENT,
//                                LinearLayout.LayoutParams.WRAP_CONTENT));
//                        layout.setOrientation(LinearLayout.VERTICAL);
//                        layout.setPadding(15, 5, 15, 5);
//                        autoBuilder.setContentView(layout);
//                        autoBuilder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        CustomDialog dialog = autoBuilder.create();
//                        for (int i = 0; i < valueList.size(); i++) {
//                            RelativeLayout itemLayout = null;
//                            EditText itemTextField;
//                            if (typeList.get(i).equals("TEXT")) {
//                                itemLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text, null);
//                            } else if (typeList.get(i).equals("PHONE")) {
//                                itemLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_phone, null);
//                                itemLayout.findViewById(R.id.label_text_phone).setOnClickListener(listener(dialog, valueList.get(i)));
//                                itemLayout.findViewById(R.id.label_text_msg).setOnClickListener(listener(dialog, valueList.get(i)));
//                            } else if (typeList.get(i).equals("MAIL")) {
//                                itemLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_mail, null);
//                                itemLayout.findViewById(R.id.label_text_mail).setOnClickListener(listener(dialog, valueList.get(i)));
//                            } else if (typeList.get(i).equals("URL")) {
//                                itemLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_url, null);
//                                itemLayout.findViewById(R.id.label_text_url).setOnClickListener(listener(dialog, valueList.get(i)));
//                            }
//                            itemTextField = (EditText) itemLayout.findViewById(R.id.label_text_single);
//                            itemLayout.findViewById(R.id.label_text_mult).setVisibility(View.GONE);
//                            itemTextField.setFocusable(false);
//                            itemTextField.setText(valueList.get(i));
//                            itemTextField.setSingleLine();
//                            itemTextField.setBackgroundColor(Color.WHITE);
//                            layout.addView(itemLayout);
//                            if (i != valueList.size() - 1) {
//                                TextView line = new TextView(ctx);
//                                line.setLayoutParams(new LinearLayout.LayoutParams(
//                                        LinearLayout.LayoutParams.FILL_PARENT, 1));
//                                line.setBackgroundResource(R.drawable.axeac_dashed);
//                                layout.addView(line);
//                            }
//                        }
//                        dialog.show();
//                    }
//                }
//            }
//        };
//    }


    private View.OnClickListener listener(final CustomDialog dialog, final String msg,final String autotype) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                String text = "";
                if (msg.equals("")) {
                    text = textField.getText().toString();
                } else {
                    text = msg;
                }
                if ("".equals(text)) {
                    return;
                }
                if (type.toUpperCase().equals("PHONE")||autotype.toUpperCase().equals("PHONE")) {
                    CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                    builder.setTitle(text);
                    final String finalText = text;
                    builder.setPositiveButton(R.string.axeac_labeltext_phone, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (CommonUtil.isNumeric(finalText)) {
                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + finalText));
                                ctx.startActivity(call);
                            } else {
                                Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNeutralButton(R.string.axeac_labeltext_msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();
                            if (CommonUtil.isNumeric(finalText)) {
                                Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + finalText));
                                sms.putExtra("sms_body", info);
                                ctx.startActivity(sms);
                            } else {
                                Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                    return;
                }
                if (type.toUpperCase().equals("TELPHONE")||autotype.toUpperCase().equals("TELPHONE")) {
                    if (CommonUtil.isNumeric(text)) {
                        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
                        ctx.startActivity(call);
                    } else {
                        Toast.makeText(ctx, R.string.axeac_msg_type_input_error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (type.toUpperCase().equals("MAIL")||autotype.toUpperCase().equals("MAIL")) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    email.putExtra(Intent.EXTRA_EMAIL, text);
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_CC, mailto);
                    email.putExtra(Intent.EXTRA_TEXT, info);
                    ctx.startActivity(Intent.createChooser(email, ctx.getString(R.string.axeac_msg_type_sendmail_soft)));
                }
                if (type.toUpperCase().equals("URL")||autotype.toUpperCase().equals("URL")) {
                    String url1 = "";
//                    if (text.startsWith("http:")) {
//                        url = text;
//                    } else {
//                        url = "http://" + text + "/";
//                    }
                    if (text.startsWith("http:") || text.startsWith("https:")) {
                        url1 = text;
                    } else {
                        boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                        url1 = (isHttps ? "https://" : "http://") + text + "/";
                    }
                    String url = Uri.encode(url1, "-![.:/,%?&=]");
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    ctx.startActivity(intent);
                }
                if (type.toUpperCase().equals("AUTO")) {
                    String temp = text.trim();
                    ArrayList<String> argsList = new ArrayList<String>();
                    while (temp.contains(",") || temp.contains(";") || temp.contains(" ")) {
                        String tempStr = autoCheck(temp);
                        if (!tempStr.trim().equals("")) {
                            argsList.add(tempStr.trim());
                        }
                        temp = temp.substring(tempStr.length() + 1, temp.length()).trim();
                    }
                    temp = temp.trim();
                    if (temp.length() > 0) {
                        argsList.add(temp);
                    }
                    ArrayList<String> typeList = new ArrayList<String>();
                    ArrayList<String> valueList = new ArrayList<String>();
                    for (int i = 0; i < argsList.size(); i++) {
                        if (argsList.get(i).contains("@") && argsList.get(i).contains(".")) {
                            typeList.add("MAIL");
                        } else if (argsList.get(i).startsWith("HTTP://") || argsList.get(i).startsWith("http://")
                                || argsList.get(i).startsWith("HTTPS://") || argsList.get(i).startsWith("https://")
                                || argsList.get(i).startsWith("FTP://") || argsList.get(i).startsWith("ftp://")) {
                            typeList.add("URL");
                        } else if (CommonUtil.isNumeric(argsList.get(i))) {
                            typeList.add("PHONE");
                        } else {
                            typeList.add("TEXT");
                        }
                        valueList.add(argsList.get(i));
                    }
                    if (valueList.size() > 0) {
                        CustomDialog.Builder autoBuilder = new CustomDialog.Builder(ctx);
                        autoBuilder.setTitle(R.string.axeac_msg_choice);
                        LinearLayout layout = new LinearLayout(ctx);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.FILL_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(15, 5, 15, 5);
                        autoBuilder.setContentView(layout);
                        autoBuilder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        CustomDialog dialog = autoBuilder.create();
                        for (int i = 0; i < valueList.size(); i++) {
                            RelativeLayout itemLayout = null;
                            EditText itemTextField;
                            itemLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text, null);
                            itemTextField = (EditText) itemLayout.findViewById(R.id.label_text_single);
                            itemLayout.findViewById(R.id.label_text_mult).setVisibility(View.GONE);
                            itemTextField.setFocusable(false);
                            itemTextField.setText(valueList.get(i));
                            itemTextField.setSingleLine();
                            itemTextField.setBackgroundColor(Color.WHITE);
                            if (!typeList.get(i).equals("TEXT")){
                                itemTextField.setTextColor(ctx.getResources().getColor(R.color.texturl));
                            }
                            if (typeList.get(i).equals("PHONE")) {
                                itemTextField.setOnClickListener(listener(dialog,valueList.get(i),"PHONE"));
                            } else if (typeList.get(i).equals("MAIL")) {
                                itemTextField.setOnClickListener(listener(dialog,valueList.get(i),"MAIL"));
                            } else if (typeList.get(i).equals("URL")) {
                                itemTextField.setOnClickListener(listener(dialog,valueList.get(i),"URL"));
                            }

                            layout.addView(itemLayout);
                            if (i != valueList.size() - 1) {
                                TextView line = new TextView(ctx);
                                line.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.FILL_PARENT, 1));
                                line.setBackgroundResource(R.drawable.axeac_dashed);
                                layout.addView(line);
                            }
                        }
                        dialog.show();
                    }
                }
            }
        };
    }

    private static String autoCheck(String temp) {
        Integer[] indexs = new Integer[3];
        indexs[0] = temp.indexOf(",");
        indexs[1] = temp.indexOf(";");
        indexs[2] = temp.indexOf(" ");
        int tempIndex = CommonUtil.sortAsc(indexs)[0];
        if (tempIndex == -1) {
            tempIndex = CommonUtil.sortAsc(indexs)[1];
        }
        if (tempIndex == -1) {
            tempIndex = CommonUtil.sortAsc(indexs)[2];
        }
        return temp.substring(0, tempIndex);
    }

    /**
     * 按钮点击事件
     * */
    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!click.equals("")) {
                String[] vs = StringUtil.split(click, "||");
                if (vs.length >= 2) {
                    String str = "";
                    String params[];
                    if (vs.length >= 3) {
                        params = StringUtil.split(vs[2], ",");
                        for (String param : params) {
                            String kv[] = StringUtil.split(param, "=");
                            if (kv.length >= 2) {
                                str += kv[0] + "=" + kv[1] + "\r\n";
                            }
                        }
                    }
                    if (vs[0].equals("PAGE")) {
                        Intent intent = new Intent();
                        intent.putExtra("meip", "MEIP_PAGE=" + vs[1] + "\r\n" + str);
                        LocalBroadcastManager
                                .getInstance(ctx).sendBroadcast(intent);
                    }
                    if (vs[0].equals("OP")) {
                        Intent intent = new Intent();
                        intent.putExtra("meip", "MEIP_ACTION=" + vs[1] + "\r\n" + str);
                        LocalBroadcastManager
                                .getInstance(ctx).sendBroadcast(intent);
                    }
                    if (vs[0].equals("TT")) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                        WebView mWebView = new WebView(ctx);
                        mWebView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.FILL_PARENT,
                                StaticObject.deviceHeight / 2));
                        mWebView.getSettings().setBuiltInZoomControls(true);
                        mWebView.getSettings().setJavaScriptEnabled(true);
                        mWebView.loadDataWithBaseURL(null, vs[1], "text/html", "UTF-8", null);
                        builder.setContentView(mWebView);
                        builder.setNeutralButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                }
            }
        }
    };

    /**
     * 设置按钮属性
     * @param btnId
     * 按钮id
     * */
    private void setBtnField(int btnId) {
        Button btn = (Button) valLayout.findViewById(btnId);
        if (!buttonName.equals("")) {
            valLayout.findViewById(btnId).setVisibility(View.VISIBLE);
            btn.setText(buttonName);
            btn.setBackgroundColor(Color.rgb(2, 163, 244));
            btn.setOnClickListener(btnOnClickListener);
        } else {
            valLayout.findViewById(btnId).setVisibility(View.GONE);
        }
    }

    /**
     * 设置布局属性
     * @param fieldId
     * 布局id
     * */
    private void setNotnilField(int fieldId) {
        if (filter != null && filter.toLowerCase().equals("notnull")) {
            valLayout.findViewById(fieldId).setVisibility(View.VISIBLE);
        } else {
            valLayout.findViewById(fieldId).setVisibility(View.GONE);
        }
    }

    /**
     * 返回当前布局
     * */
    @Override
    public View getView() {
        if (visiable) {
            valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text, null);
//            if (type.toUpperCase().equals("PHONE")) {
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_phone, null);
//                valLayout.findViewById(R.id.label_text_phone).setOnClickListener(listener(null, ""));
//                valLayout.findViewById(R.id.label_text_msg).setOnClickListener(listener(null, ""));
//            } else if (type.toUpperCase().equals("MAIL")) {
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_mail, null);
//                valLayout.findViewById(R.id.label_text_mail).setOnClickListener(listener(null, ""));
//            } else if (type.toUpperCase().equals("URL")) {
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_url, null);
//                valLayout.findViewById(R.id.label_text_url).setOnClickListener(listener(null, ""));
//            } else if (type.toUpperCase().equals("AUTO")) {
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_auto, null);
//                valLayout.findViewById(R.id.label_text_auto).setOnClickListener(listener(null, ""));
//            } else if(type.toUpperCase().equals("TELPHONE")){
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text_telphone, null);
//                valLayout.findViewById(R.id.label_text_telphone).setOnClickListener(listener(null, ""));
//            }else {
//                valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_label_text, null);
//            }
            setBtnField(R.id.label_text_btn);
            setNotnilField(R.id.label_text_notnil);
//            if (rows > -1) {
//                valLayout.setLayoutParams(new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.FILL_PARENT, 40 * rows));
//            } else {
                valLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));
//            }
            this.view = valLayout;
            if (rows == -1) {
                valLayout.findViewById(R.id.label_text_mult).setVisibility(View.GONE);
                valLayout.findViewById(R.id.label_text_single).setVisibility(View.VISIBLE);
                textField = (EditText) valLayout.findViewById(R.id.label_text_single);
            } else {
                setWrap("true");
                valLayout.findViewById(R.id.label_text_mult).setVisibility(View.VISIBLE);
                valLayout.findViewById(R.id.label_text_single).setVisibility(View.GONE);
                textField = (EditText) valLayout.findViewById(R.id.label_text_mult);
//                textField.setHeight(80*rows);
                textField.setLines(rows);
//                textField.setMaxLines(rows);
            }

            String familyName = null;
            int style = Typeface.NORMAL;
            if (this.font != null && !"".equals(this.font)) {
                if (this.font.indexOf(";") != -1) {
                    String[] strs = this.font.split(";");
                    for (String str : strs) {
                        if (str.startsWith("size")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            String s = str.substring(index + 1).trim();
                            textField.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
                        } else if (str.startsWith("family")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            familyName = str.substring(index + 1).trim();
                        } else if (str.startsWith("style")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            String s = str.substring(index + 1).trim();
                            if ("bold".equals(s)) {
                                style = Typeface.BOLD;
                            } else if ("italic".equals(s)) {
                                style = Typeface.ITALIC;
                            } else {
                                if (s.indexOf(",") != -1) {
                                    if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
                                        style = Typeface.BOLD_ITALIC;
                                    }
                                    if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
                                        style = Typeface.BOLD_ITALIC;
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
                                textField.setTextColor(Color.rgb(r, g, b));
                            }
                        }
                    }
                }
            }
            if (familyName == null || "".equals(familyName)) {
                textField.setTypeface(Typeface.defaultFromStyle(style));
            } else {
                textField.setTypeface(Typeface.create(familyName, style));
            }
            textField.setText(text);
            textField.setVisibility(View.VISIBLE);
            textField.setHint(hint);
            if (readOnly) {
                if("".equals(text)||null==text){
                    textField.setText(" ");
                }
                textField.setFocusable(false);
                int r = Integer.parseInt(labelBgColor.substring(0, 3));
                int g = Integer.parseInt(labelBgColor.substring(3, 6));
                int b = Integer.parseInt(labelBgColor.substring(6, 9));
                textField.setBackgroundColor(Color.rgb(r, g, b));

                if (!type.toUpperCase().equals("TEXT")) {
                    textField.setTextColor(ctx.getResources().getColor(R.color.texturl));
                    textField.setOnClickListener(listener(null,"","TEXT"));
                }else {
                    textField.setTextColor(ctx.getResources().getColor(R.color.texthint));
                }
            }
            textField.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN && !readOnly) {
                        if (rows > 0 && textField.getLineCount() > 2 * rows) {
                            if (multDialog != null && !multDialog.isShowing()) {
                                multDialogEdit.setText(textField.getText().toString());
                                CharSequence cs = multDialogEdit.getText();
                                if (cs instanceof Spannable) {
                                    Selection.setSelection((Spannable) cs, cs.length());
                                }
                                multDialog.show();
                            }
                        }
                    }
                    return false;
                }
            });
            textField.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (rows > 0 && textField.getLineCount() > 2 * rows) {
                        if (multDialog != null && !multDialog.isShowing()) {
                            multDialogEdit.setText(textField.getText().toString());
                            CharSequence cs = multDialogEdit.getText();
                            if (cs instanceof Spannable) {
                                Selection.setSelection((Spannable) cs, cs.length());
                            }
                            multDialog.show();
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
        super.getView();
        return linearLayout;
    }

    /**
     * 返回信息区文本
     * @return
     * 信息区文本
     * */
    @Override
    public String getValue() {
        if (textField == null) {
            return text;
        }
        return textField.getText().toString();
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