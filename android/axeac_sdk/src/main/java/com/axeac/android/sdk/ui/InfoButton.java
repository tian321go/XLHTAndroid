package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.AndroidFileUtil;
import com.axeac.android.sdk.utils.DensityUtil;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Information button (phone or cell phone number, email address, webpage)
 * 信息按钮(电话或手机号、邮件地址、网页)
 * @author axeac
 * @version 1.0.0
 */
public class InfoButton extends Component {

    /**
     * 设置显示文本
     * <br>默认为空
     * */
    private String text = "";
    /**
     * 显示文本，优先级高于text
     * <br>默认值为空
     * */
    private String name = "";
    /**
     * 类型，PHONE,MAIL,URL
     * <br>默认值为空
     * */
    private String type = "";
    /**
     * 是否可以下载
     * <br>默认值为false
     * */
    private boolean download = false;

    private TextView btn;

    /**
     * 下载线程Thread对象
     * */
    private Thread downloadThread;
    /**
     * 下载提示框ProgressDialog对象
     * */
    private ProgressDialog downloadDialog;

    public InfoButton(Activity ctx) {
        super(ctx);
        this.returnable = false;
        btn = new TextView(ctx);
        btn.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, DensityUtil.dip2px(ctx, 40)));
        btn.setBackgroundColor(ctx.getResources().getColor(android.R.color.white));
        btn.setPadding(DensityUtil.dip2px(ctx, 5), 0, DensityUtil.dip2px(ctx, 5), 0);
        btn.setGravity(Gravity.CENTER);
        btn.setSingleLine(true);
        btn.setTextSize(14);
        btn.setEllipsize(TextUtils.TruncateAt.END);
        btn.setTextColor(Color.BLACK);
        btn.setOnClickListener(listener);
        initDownloadDialog();
    }

    /**
     * 初始化下载提示框
     * */
    private void initDownloadDialog() {
        downloadDialog = new ProgressDialog(ctx);
        downloadDialog.setTitle(R.string.axeac_download_prompt);
        downloadDialog.setMessage(ctx.getString(R.string.axeac_download_prompt_msg));
        downloadDialog.setCancelable(true);
        downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadDialog.setIndeterminate(false);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (downloadThread != null && !downloadThread.isInterrupted()) {
                    downloadThread.interrupt();
                }
            }
        });
    }

    /**
     * 设置显示文本
     * @param text
     * 显示的文本
     * */
    public void setText(String text) {
        this.text = text;
        if (text.startsWith("http:")) {
            btn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    /**
     * 设置显示文本，优先级高于text
     * @param name
     * 显示文本
     * */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置类型
     * @param type
     * 可选值PHONE/MAIL/URL
     * */
    public void setType(String type) {
        type = type.toUpperCase();
        if (type.equals("PHONE") || type.equals("MAIL") || type.equals("URL")) {
            this.type = type;
        }
    }

    /**
     * 设置是否可以下载
     * @param download
     * 可选值true/false
     * */
    public void setDownload(String download) {
        this.download = Boolean.parseBoolean(download);
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (!this.visiable) return;
        if (this.width == "-1" && this.height == -1) {
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 75, 1));
        } else {
            if (this.width == "-1") {
                btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, this.height, 1));
            } else if (this.height == -1) {
                if (this.width.endsWith("%")) {
                    int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 75, viewWeight));
                } else {
                    btn.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), 75));
                }
            } else {
                if (this.width.endsWith("%")) {
                    int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 75, viewWeight));
                } else {
                    btn.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), this.height));
                }
            }
        }
        if (!name.equals("")) {
            btn.setText(name);
        } else {
            btn.setText(text);
        }
    }

    /**
     * 本类监听事件
     * */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (text.equals("") || type.equals("")) {
                Toast.makeText(ctx, R.string.axeac_info_typenotnil, Toast.LENGTH_SHORT).show();
                return;
            }
            if (type.equals("URL")) {
                String url1 = "";
                if (text.startsWith("http:") || text.startsWith("https:")) {
                    url1 = text;
                } else {
                    boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                    url1 = (isHttps ? "https://" : "http://") + text + "/";
                }
                String url = Uri.encode(url1, "-![.:/,%?&=]");
                if (download) {
                    doDownload(url);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    ctx.startActivity(intent);
                }
            } else {
                CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                builder.setTitle(R.string.axeac_msg_choice);
                LinearLayout layout = new LinearLayout(ctx);
                layout.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(15, -5, 15, -5);
                builder.setContentView(layout);
                builder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                CustomDialog dialog = builder.create();
                if (type.equals("PHONE")) {
                    TextView phoneCall = optItemTextView();
                    phoneCall.setText(R.string.axeac_info_call);
                    phoneCall.setOnClickListener(optItemListener(dialog, 11));
                    layout.addView(phoneCall);
                    TextView phoneMsg = optItemTextView();
                    phoneMsg.setText(R.string.axeac_info_sendmsg);
                    phoneMsg.setOnClickListener(optItemListener(dialog, 12));
                    layout.addView(phoneMsg);
                    TextView phoneSaved = optItemTextView();
                    phoneSaved.setText(R.string.axeac_info_savetobook);
                    phoneSaved.setOnClickListener(optItemListener(dialog, 13));
                    layout.addView(phoneSaved);
                } else if (type.equals("MAIL")) {
                    TextView mailSend = optItemTextView();
                    mailSend.setText(R.string.axeac_info_sendmail);
                    mailSend.setOnClickListener(optItemListener(dialog, 21));
                    layout.addView(mailSend);
                    TextView mailSaved = optItemTextView();
                    mailSaved.setText(R.string.axeac_info_savetobook);
                    mailSaved.setOnClickListener(optItemListener(dialog, 22));
                    layout.addView(mailSaved);
                }
                dialog.show();
            }
        }
    };

    /**
     * 自定义TextView
     * */
    private TextView optItemTextView() {
        TextView opt = new TextView(ctx);
        opt.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 65));
        opt.setBackgroundColor(Color.rgb(2, 163, 244));
        opt.setGravity(Gravity.CENTER_VERTICAL);
        opt.setPadding(30, 0, 10, 0);
        opt.setTextSize(14);
        opt.setSingleLine(true);
        opt.setTextColor(Color.WHITE);
        return opt;
    }

    /**
     * 自定义TextView的监听
     * */
    private View.OnClickListener optItemListener(final CustomDialog dialog, final int opt) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (opt == 11) {
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + text));
                    ctx.startActivity(call);
                }
                if (opt == 12) {
                    Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + text));
                    sms.putExtra("sms_body", ctx.getString(R.string.axeac_info_inputmsgcontent));
                    ctx.startActivity(sms);
                }
                if (opt == 13) {
                    Intent save = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
                    save.putExtra(Intents.Insert.PHONE, text);
                    ctx.startActivity(save);
                }
                if (opt == 21) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] emailReciver = new String[]{text};
                    email.putExtra(Intent.EXTRA_EMAIL, emailReciver);
                    email.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.axeac_info_inputmailtitle));
                    email.putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.axeac_info_inputmailcontent));
                    ctx.startActivity(Intent.createChooser(email, ctx.getString(R.string.axeac_info_choosemailsoft)));
                }
                if (opt == 33) {
                    Intent save = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
                    save.putExtra(Intents.Insert.EMAIL, text);
                    ctx.startActivity(save);
                }
            }
        };
    }

    /**
     * 下载
     * @param url
     * url地址
     * */
    private void doDownload(final String url) {
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpConn;
                Message message;
                try {
                    Thread.sleep(100);
                    httpConn = (HttpURLConnection) new URL(url).openConnection();
                    System.out.println("HTTP状态码: " + httpConn.getResponseCode());
                    String url = httpConn.getURL().toString();
                    httpConn = (HttpURLConnection) new URL(url).openConnection();
                    httpConn.setRequestMethod("GET");
                    httpConn.setDoInput(true);
                    httpConn.setDoOutput(true);
                    httpConn.setUseCaches(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.connect();
                    message = new Message();
                    message.what = 2;
                    mDownloadUIHandler.sendMessageDelayed(message, 0);
                    String savepath = "";
                    if (FileUtils.checkSDCard()) {
                        savepath = FileUtils.getSDCardPath() + FileUtils.KHPATH + "/KHDownLoaderFiles";
                        FileUtils.createDir(savepath);
                    } else {
                        message = new Message();
                        message.what = -1;
                        message.obj = ctx.getString(R.string.axeac_download_fail_nosdcard);
                        mDownloadUIHandler.sendMessageDelayed(message, 0);
                        return;
                    }
                    InputStream dis = httpConn.getInputStream();
                    String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                    File newFile = new File(savepath + File.separator + fileName);
                    if (!newFile.exists())
                        newFile.createNewFile();
                    message = new Message();
                    message.what = 0;
                    message.arg1 = httpConn.getContentLength();
                    mDownloadUIHandler.sendMessageDelayed(message, 0);
                    FileOutputStream fos = new FileOutputStream(newFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    int downloader = 0;
                    try {
                        while ((len = dis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            downloader += len;
                            message = new Message();
                            message.what = 1;
                            message.arg1 = downloader;
                            mDownloadUIHandler.sendMessageDelayed(message, 0);
                        }
                        fos.flush();
                        fos.close();
                        message = new Message();
                        message.what = 3;
                        Bundle bund = new Bundle();
                        bund.putString("filePath", newFile.getAbsolutePath());
                        bund.putString("message", ctx.getString(R.string.axeac_download_succ_toast));
                        message.setData(bund);
                        mDownloadUIHandler.sendMessageDelayed(message, 0);
                    } catch (Exception e) {
                        Log.e("downloader error", e.getMessage());
                    } finally {
                        dis.close();
                        httpConn.disconnect();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    message = new Message();
                    message.what = -1;
                    message.obj = ctx.getString(R.string.axeac_download_fail_ioexp);
                    mDownloadUIHandler.sendMessageDelayed(message, 0);
                }
            }

        });
        downloadThread.start();
    }

    /**
     * 接收下载消息的Handler对象
     * */
    private Handler mDownloadUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                downloadDialog.setMax(msg.arg1);
            } else if (msg.what == 1) {
                downloadDialog.setProgress(msg.arg1);
            } else if (msg.what == 2) {
                downloadDialog.show();
            } else if (msg.what == 3) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.dismiss();
                }
                CustomDialog.Builder dialog = new CustomDialog.Builder(ctx);
                dialog.setTitle(R.string.axeac_download_prompt);
                dialog.setMessage(msg.getData().getString("message"));
                final String filePath = msg.getData().getString("filePath");
                System.out.println(filePath);
                dialog.setPositiveButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            ctx.startActivity(AndroidFileUtil.openFile(filePath));
                        } catch (Exception e) {
                            Toast.makeText(ctx, R.string.axeac_download_open_exp, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            } else if (msg.what == -1) {
                if (downloadDialog != null && downloadDialog.isShowing()) {
                    downloadDialog.dismiss();
                }
                CustomDialog.Builder dialog = new CustomDialog.Builder(ctx);
                dialog.setTitle(R.string.axeac_download_prompt);
                dialog.setMessage(msg.obj.toString());
                dialog.setNegativeButton(R.string.axeac_msg_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View getView() {
        return btn;
    }

    @Override
    public String getValue() {
        return null;
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