package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ImageShowActivity;
import com.axeac.android.sdk.activity.SignActivity;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.tools.Base64Coding;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.FtpUtils;
import com.axeac.android.sdk.utils.HttpAssist;
import com.axeac.android.sdk.utils.StaticObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.w3c.dom.Text;

/**
 * describe:Signature control
 * 签名控件
 * @author axeac
 * @version 1.0.0
 * */
public class Sign extends LabelComponent {
    /**
     * 文件保存本地地址
     * */
    private static final String FILE_PATH = "/KuaiHu/Sign/";
    public static final int PAINT_WITH_DATA = 3003;
    private ImageButton takebtn;
    private ImageView previewImg;
    private RelativeLayout signlayout;
    private TextView hintView;
    private String hint = "请点击图标进行签名";
    /**
     * 文件地址
     * <br>默认值为空
     * */
    private String returnVal = "";

    /**
     * 是否存在图片文件的判断标志
     * <br>默认值为false
     * */
    private boolean isHavaTaked = false;

    private Activity ctx;
    /**
     * 负责将position值赋值给curPosition的中间值
     * */
    private int pos = 0;
    /**
     * 标记handler位置的position
     * */
    public static int position = 0;
    /**
     * 标记handler位置的position
     * */
    public static int curPosition = 0;
    /**
     * 文件名称
     * */
    private String fileName;

    /**
     * 设置上传下载服务器模式，1为FTP，0为服务器，默认为0
     * */
    private int loadType = 0;
    public static String ftpUrl="";
    private String hostName;
    private String serverPort;
    private String userName;
    /**
     * 存储Handler对象的Map集合
     * */
    public static java.util.Map<Integer, Handler> handlerMap = new HashMap<Integer, Handler>();

    public Sign(Activity ctx) {
        super(ctx);
        this.ctx = ctx;
        this.returnable = true;

            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.axeac_sign, null);
            signlayout = layout.findViewById(R.id.signlayout);
            hintView = layout.findViewById(R.id.hintView);
            takebtn = (ImageButton) layout.findViewById(R.id.deletephoto);
            previewImg = (ImageView) layout.findViewById(R.id.takephoto);
            setWrap("true");
            takebtn.setOnClickListener(deletelistener);
            previewImg.setOnClickListener(tabkelistener);
            this.view = layout;
            handlerMap.put(++position, mHandler);
            pos = position;
    }

    public void setHint(String hint){
        this.hint = hint;
    }

    /**
     * 设置文件名称
     * @param fileName
     * 文件名称
     * */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 设置上传下载服务器模式，1为FTP，0为服务器，默认为0
     * */
    public void setLoadType(int loadType){
        this.loadType = loadType;
    }

    public void setFtpUrl(String ftpUrl){
        this.ftpUrl = ftpUrl;
    }
//    private View.OnClickListener continueListenner = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//                Intent intent = new Intent(ctx, SignActivity.class);
//                ctx.startActivityForResult(intent,PAINT_WITH_DATA);
//
//        }
//    };

    /**
     * 图片存在时的监听事件
     * */
    private View.OnClickListener tabkelistener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if(isHavaTaked){
                Intent i = new Intent(ctx,ImageShowActivity.class);
                String path = "Sign;"+returnVal;
                i.putExtra("path", path);
                ctx.startActivity(i);
            }else{
                curPosition = pos;
                signImage();
            }
        }
    };

    /**
     * 删除监听
     * */
    private View.OnClickListener deletelistener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            previewImg.setVisibility(View.GONE);
            signlayout.setVisibility(View.VISIBLE);
            isHavaTaked = false;
            returnVal = "";
        }
    };

    /**
     * 初始化是否签名对话框
     * */
    private void signImage() {
                CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                builder.setTitle(R.string.axeac_msg_choice);
                builder.setPositiveButton(R.string.axeac_msg_sign, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(ctx, SignActivity.class);
                        ctx.startActivityForResult(intent,PAINT_WITH_DATA);
                    }
                });
                builder.setNegativeButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
    }

    /**
     * 接收消息的Handler对象
     * */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAINT_WITH_DATA:
                    paintWithData((String) msg.obj);
                    break;
                default:
                    break;
            }
        };
    };

    /**
     * 图片为手写数据时执行的方法
     * */
    private void paintWithData(String path){
        if (path.equals("")) {
        } else {
            returnVal = path;
           Toast.makeText(ctx, returnVal, Toast.LENGTH_SHORT).show();
           previewImg.setVisibility(View.VISIBLE);
           signlayout.setVisibility(View.GONE);
           previewImg.setImageBitmap(BitmapFactory.decodeFile(path));
           isHavaTaked = true;
        }
    }
    /**
     * 上传图片的AsyncTask类
     * */
    class SetBitmapService extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                String RequestURL = (isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") + ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/Upload";
                HttpAssist.uploadFile(new File(returnVal), RequestURL, returnVal.substring(returnVal.lastIndexOf("/")+1,returnVal.length()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    /**
     * 获得图片数据
     * @return
     * 图片数据
     * */
    @SuppressWarnings("unused")
    private String getReturnValue() {
        String data = "";
        File file = new File(returnVal);
        try {
            if (file != null) {
                FileInputStream in = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
                byte[] temp = new byte[1024];
                int size = 0;
                while ((size = in.read(temp)) != -1) {
                    out.write(temp, 0, size);
                }
                in.close();
                byte[] content = out.toByteArray();
                out.close();
                data = Base64Coding.encode(content);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {

        if (isHavaTaked){
            previewImg.setOnClickListener(tabkelistener);
            takebtn.setOnClickListener(deletelistener);
        }else{
            hintView.setText(hint);
            signlayout.setVisibility(View.VISIBLE);
            previewImg.setVisibility(View.GONE);
            signlayout.setOnClickListener(tabkelistener);
        }
    }

    /**
     * 返回当前视图
     * */
    @Override
    public View getView() {
        new Sign.GetBitmapService().execute();
        return super.getView();
    }

    /**
     * 加载图片的AsyncTask类
     * */
    class GetBitmapService extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params){
            if(fileName != null && !"".equals(fileName)){
                returnVal = FileUtils.getSDCardPath() + FILE_PATH + fileName;
                File file = new File(returnVal);
                if (file.exists()){
                    isHavaTaked = true;
                    return BitmapFactory.decodeFile(returnVal);
                }else {
                    String strPath = FileUtils.getSDCardPath() + FILE_PATH;
                    if (loadType==0){
                        boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                        String RequestURL = (isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") + ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/Download?meip_filename=" + fileName;
                        Glide.with(ctx)
                            .load(RequestURL)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.BTN_OFF_ACTION);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                    if (resource != null) {
                                        previewImg.setVisibility(View.VISIBLE);
                                        previewImg.setImageBitmap(resource);
                                        previewImg.invalidate();
                                        String path = FileUtils.getSDCardPath() + FILE_PATH;
                                        File dirFile = new File(path);
                                        if(!dirFile.exists()){
                                            dirFile.mkdir();
                                        }
                                        File myCaptureFile = new File(path);
                                        BufferedOutputStream bos = null;
                                        try {
                                            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                                            resource.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                                            bos.flush();
                                            bos.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    Intent intent = new Intent();
                                    intent.setAction(StaticObject.BTN_NO_ACTION);
                                    LocalBroadcastManager
                                            .getInstance(ctx).sendBroadcast(intent);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                    Toast.makeText(ctx,"加载图片失败："+e,Toast.LENGTH_SHORT).show();
                                }
                            });
                    }else {
                        if (ftpUrl != null || !"".equals(ftpUrl)) {
                            String[] urlFtp = ftpUrl.split(";");
                            if (urlFtp.length == 5) {
                                try {
                                    new FtpUtils().downloadBitmap(urlFtp[0], urlFtp[1], urlFtp[2], urlFtp[3], urlFtp[4] + "/" + fileName, strPath, fileName, new FtpUtils.DownLoadProgressListener() {
                                        @Override
                                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                            if (currentStep.equals(FtpUtils.FTP_DOWN_SUCCESS)) {
                                            } else if (currentStep.equals(FtpUtils.FTP_DOWN_LOADING)) {
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    isHavaTaked = true;
                    return BitmapFactory.decodeFile(returnVal);
                }
            }else{
                BitmapDrawable bd =(BitmapDrawable)ctx.getResources().getDrawable(R.drawable.axeac_paizhao);
                return bd.getBitmap();
            }
        }
        @Override
        protected void onPostExecute(Object result) {
            previewImg.setImageBitmap((Bitmap)result);
            previewImg.invalidate();
        }
    }

    /**
     * 返回文件名称
     * @return
     * 文件名称
     * */
    @Override
    public String getValue() {
        if(returnVal != null && !"".equals(returnVal)){
            if (loadType==0) {
                new SetBitmapService().execute();
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(returnVal);
                        if (Sign.ftpUrl != null || !"".equals(Sign.ftpUrl)) {
                            String[] ftpUrl = Sign.ftpUrl.split(";");
                            if (ftpUrl.length == 5) {
                                try {
                                    // Get saved to the local paintingView image and upload it to the FTP server
                                    // 获取保存到本地的paintingView图片，上传到FTP服务器
                                    new FtpUtils().uploadBitmap(ftpUrl[0], ftpUrl[1], ftpUrl[2], ftpUrl[3], file, "/" + ftpUrl[4], new FtpUtils.UploadProgressListener() {
                                        @Override
                                        public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                            // TODO Auto-generated method stub
                                            Log.d("SignActivity", currentStep);
                                            if (currentStep.equals(FtpUtils.FTP_UPLOAD_SUCCESS)) {
                                                Log.d("SignActivity", "-----shangchuan--successful");
                                            } else if (currentStep.equals(FtpUtils.FTP_UPLOAD_LOADING)) {
                                                long fize = file.length();
                                                float num = (float) uploadSize / (float) fize;
                                                int result = (int) (num * 100);
                                                Log.d("SignActivity", "-----shangchuan---" + result + "%");
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }).start();
            }
            final String fileName = returnVal.substring(returnVal.lastIndexOf("/")+1, returnVal.length());
            return fileName;
        }else
            return returnVal;
    }

    public String getVal(){
        return returnVal;
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

