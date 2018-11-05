package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ImageShowActivity;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.AndroidFileUtil;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.StaticObject;

/**
 * describe:Adjunct component
 * 附件控件
 * @author axeac
 * @version 1.0.0
 */
public class Adjunct extends LabelComponent {

    /**
     * 附件下载地址
     **/
    private String adjunctUrl = "";

    /**
     * 附件预览地址
     * */
//    private String previewUrl = "";

    private LinearLayout valLayout;

    /**
     * 存储附件信息的list集合
     * */
    private List<String[]> datas = new ArrayList<String[]>();

    /*
    * 存储附件预览地址的集合
    * */
    private List<String> previewUrls = new ArrayList<>();
    /**
     * 存储附件信息的list集合
     * */
    private List<String[]> dataExs = new ArrayList<String[]>();

    /**
     * 标题
     * <br>默认值为空
     * */
    private String title = "";
    /**
     * 是否预览
     * <br>true表示预览，false表示不预览
     * */
    private boolean preview = true;

    /**
     * 异步加载线程对象
     * */
    private Thread loadingThread;
    /**
     * 加载对话框对象
     * */
    private ProgressDialog loadingDialog;

    /**
     * 异步下载线程对象
     * */
    private Thread downloadThread;
    /**
     * 异步下载对话框对象
     * */
    private ProgressDialog downloadDialog;

    public Adjunct(Activity ctx) {
        super(ctx);
        this.returnable = false;
        valLayout = new LinearLayout(ctx);
        valLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        valLayout.setOrientation(LinearLayout.VERTICAL);
        this.view = valLayout;
        title = ctx.getString(R.string.axeac_toast_exp_adjunct);
        String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
        if (!url.equals("")) {
            adjunctUrl = url.replace(StaticObject.HTTPSERVER, "");
        }
        this.labelWidth = "0%";
        initLoadingDialog();
        initDownloadDialog();
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
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loadingThread != null && !loadingThread.isInterrupted()) {
                    loadingThread.interrupt();
                }
            }
        });
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    /**
     * 初始化下载对话框
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
     * 设置是否预览
     * @param preview
     * true表示预览，false表示不预览
     * */
    public void setPreview(String preview) {
        this.preview = Boolean.parseBoolean(preview);
    }

    /**
     * 设置预览地址
     * @param previewUrl
     * 预览地址
     * */
//    public void setPreviewUrl(String previewUrl){
//        this.previewUrl = previewUrl;
//    }
    /**
     * 向集合中添加附件信息
     * */
    public void addData(String data) {
        String[] temp = StringUtil.split(data, "||");
        if (temp.length == 4) {
            datas.add(temp);
        }
    }

    public void addPreviewUrl(String data){
        previewUrls.add(data);
    }

    /**
     * 向集合中添加附件信息
     * */
    public void addDataEx(String dataEx) {
        String[] temp = StringUtil.split(dataEx, "||");
        if (temp.length >= 4) {
            dataExs.add(temp);
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (this.label == null || this.label.equals("")) {
            label = title;
        }
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                RelativeLayout layout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_adjunct_item, null);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams .setMargins((int) dipToPx(16), (int) dipToPx(8), (int) dipToPx(16), (int) dipToPx(8));
                layout.setLayoutParams(lparams);
                TextView fileNameView = (TextView) layout.findViewById(R.id.adjunct_filename);
                TextView fileSizeView = (TextView) layout.findViewById(R.id.adjunct_filesize);
                ImageView fileTypeView = (ImageView) layout.findViewById(R.id.adjunct_filetype);
                LinearLayout previewView = (LinearLayout) layout.findViewById(R.id.preview_layout);
                ImageView downloadView = (ImageView) layout.findViewById(R.id.adjunct_download);
                fileNameView.setText(datas.get(i)[1]);
                if (datas.get(i).length >= 4) {
                    String t = ctx.getString(R.string.axeac_msg_size);
                    fileSizeView.setText(t + "：" + datas.get(i)[3]);
                } else {
                    fileSizeView.setVisibility(View.GONE);
                }
                String fileType = datas.get(i)[2];
                if (fileType.equals("txt")) {
                    fileTypeView.setImageResource(R.drawable.axeac_file_icon_txt);
                } else if (fileType.equals("doc") || fileType.equals("docx")) {
                    fileTypeView.setImageResource(R.drawable.axeac_file_icon_doc);
                } else if (fileType.equals("xls") || fileType.equals("xlsx")) {
                    fileTypeView.setImageResource(R.drawable.axeac_file_icon_xls);
                } else if (fileType.equals("ppt") || fileType.equals("pptx")) {
                    fileTypeView.setImageResource(R.drawable.axeac_file_icon_ppt);
                } else if (fileType.equals("pdf")) {
                    fileTypeView.setImageResource(R.drawable.axeac_file_icon_pdf);
                }
//                preview = false;
                if (preview) {
                    previewView.setTag(i);
                    previewView.setOnClickListener(previewViewOnClickListener());
                }
                downloadView.setTag(datas.get(i)[0]);
                downloadView.setOnClickListener(downloadViewOnClickListener());
                valLayout.addView(layout);
                if (i>0&&i<datas.size()-1){
                    View view = new View(ctx);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                    lp.setMargins(0,10,0,0);
                    view.setLayoutParams(lp);
                    view.setBackgroundColor(ctx.getResources().getColor(R.color.linecolor));
                    valLayout.addView(view);
                }
                if (i==0&&datas.size()>1){
                    View view = new View(ctx);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                    lp.setMargins(0,10,0,0);
                    view.setLayoutParams(lp);
                    view.setBackgroundColor(ctx.getResources().getColor(R.color.linecolor));
                    valLayout.addView(view);
                }
            }
        }
    }

    /**
     * previewView的监听事件
     * */
    private View.OnClickListener previewViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!adjunctUrl.equals("")) {
//                    String fileId = (String) v.getTag();
//                    String previewCountUrl = adjunctUrl + "Preview?meip_fileid=" + fileId + "&type=0";
//                    String previewFileUrl = adjunctUrl + "Preview?meip_fileid=" + fileId + "&type=1&page=" + 1;
//                    System.out.println(previewFileUrl);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(previewCountUrl));
//                    ctx.startActivity(intent);
//                }
                int tag = (int) v.getTag();
                if (previewUrls.size()>tag) {
                    Intent intent = new Intent(ctx, ImageShowActivity.class);
                    String path = "Adjunct;" + previewUrls.get(tag);
                    intent.putExtra("path", path);
                    ctx.startActivity(intent);
                }else{
                    Toast.makeText(ctx,"数据错误",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * downloadView的监听事件
     * */
    private View.OnClickListener downloadViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = (String) v.getTag();
                if (data.startsWith("http://") || data.startsWith("https://")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    ctx.startActivity(intent);
                } else {
                    if (!adjunctUrl.equals("")) {
                        String downloadUrl = adjunctUrl + "Download?meip_fileid=" + data;

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(downloadUrl));
                        ctx.startActivity(intent);
                    }
                }
            }
        };
    }

    /**
     * describe:Loading attachments
     * 加载附件
     *
     * @param url
     * url地址
     */
    private void doLoading(final String url) {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
                    httpConn.connect();
                    int re = httpConn.getResponseCode();
                    Message message = new Message();
                    if (re == HttpURLConnection.HTTP_OK) {
                        message.what = 0;
                        InputStream dis = httpConn.getInputStream();
                        message.obj = dis.read();
                    } else {
                        message.what = -1;
                        message.obj = ctx.getString(R.string.axeac_toast_exp_getpagefail);
                    }
                    mLoadUIHandler.sendMessageDelayed(message, 0);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    Message message = new Message();
                    message.what = -1;
                    message.obj = e.getMessage();
                    mLoadUIHandler.sendMessageDelayed(message, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    Message message = new Message();
                    message.what = -1;
                    message.obj = e.getMessage();
                    mLoadUIHandler.sendMessageDelayed(message, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    Message message = new Message();
                    message.what = -1;
                    message.obj = e.getMessage();
                    mLoadUIHandler.sendMessageDelayed(message, 0);
                }
            }
        });
        loadingThread.start();
    }

    private final Handler mLoadUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            if (msg.what == 0) {
                Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * describe:Download attachments
     * 下载附件
     *
     * @param url
     * url地址
     * @param data
     * 附件信息
     */
    private void doDownload(final String url, final String[] data) {
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpConn;
                Message message;
                try {
                    Thread.sleep(100);
                    httpConn = (HttpURLConnection) new URL(URLEncoder.encode(url, "utf-8")).openConnection();
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
                    String fileName = data[1];
                    File newFile = new File(savepath + File.separator + fileName + ".exe");
                    if (!newFile.exists()) {
                        newFile.createNewFile();
                    }
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
                    e.printStackTrace();
                    message = new Message();
                    message.what = -1;
                    message.obj = ctx.getString(R.string.axeac_download_fail_ioexp);
                    mDownloadUIHandler.sendMessageDelayed(message, 0);
                }
            }

        });
        downloadThread.start();
    }

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

    private float dipToPx(int dipValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                ctx.getResources().getDisplayMetrics());
    }

    @Override
    public View getView() {
        return super.getView();
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