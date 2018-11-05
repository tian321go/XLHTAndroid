

package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.activity.ImageShowActivity;
import com.axeac.android.sdk.adapters.PhotoSelectorAdapter;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.FtpUtils;
import com.axeac.android.sdk.utils.HttpAssist;
import com.axeac.android.sdk.utils.ImageUtils;
import com.axeac.android.sdk.utils.StaticObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * describe:Photo selector
 * 图片选择
 * @author axeac
 * @version 1.0.0
 */
public class PhotoSelector extends LabelComponent {
    /**
     * 文件保存地址
     * */
    private static final String FILE_PATH = FileUtils.KHPATH + "/Camera";
    private static final String DOWM_PICTURE = FileUtils.KHPATH + "/Picture/";

    public static final int CAMERA_WITH_DATA = 3001;
    public static final int PHOTO_WITH_DATA = 3002;

    private GridView gridView;

    private RelativeLayout layout;
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
     * 文件地址
     * <br>默认值为空
     * */
    private List<String> returnVal = new ArrayList<>();
    /**
     * 文件名称集合
     * */
    private List<String> filenameList = new ArrayList<>();
    /**
     * 文件名称
     * */
    private String fileName="";
    private List<String> fileNames = new ArrayList<>();
    /**
     * 返回文件名称
     * */
    private List<String> returnFileName = new ArrayList<>();
    /**
     * 存储Handler对象的Map集合
     * */
    public static Map<Integer, Handler> handlerMap = new HashMap<>();
    /**
     * 存储TakePhoto对象的Map集合
     * */
    public static Map<Integer, TakePhoto> takePhotoMap = new HashMap<>();

    /**
     * File对象
     * */
    private static File mCurrentPhotoFile;
    /**
     * 1代表从相机选择，其他代表图库
     * <br>默认值为0
     * */
    private int model = 0;
    /**
     * 1代表ftp上传，其他代表服务器上传
     * <br>默认值为0
     * */
    private int loadType = 0;
    private boolean isHavaUp = false;
    private boolean isHavaDwon = false;
    private Activity ctx;
    /**
     * ftp地址、端口号、账户、密码以及目标文件夹
     * <br>默认值为空
     * */
    private String ftpUrl = "";

    public PhotoSelector(final Activity ctx) {
        super(ctx);
        this.ctx = ctx;
        this.returnable = true;
        setWrap("true");
        LinearLayout valLayout = new LinearLayout(ctx);
        valLayout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        valLayout.setOrientation(LinearLayout.VERTICAL);
        layout = (RelativeLayout) LayoutInflater.from(ctx).inflate(R.layout.photo, null);

        gridView = (GridView)layout.findViewById(R.id.photo_grid);
        PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(ctx,fileNames,gridView,false);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                if (fileNames.size()==0||fileNames.size()==i){
                    if(!readOnly) {
                        curPosition = pos;
                        chooseImage();
                    }
                }else{
                    Intent intent = new Intent(ctx,ImageShowActivity.class);
                    String path = "PhotoSelector;"+fileNames.get(i);
                    intent.putExtra("path", path);
                    ctx.startActivity(intent);
                }
            }
        });
        valLayout.addView(layout);
        this.view = valLayout;
        handlerMap.put(++position, mHandler);
        pos = position;
        initTakePhoto();

    }

    /**
     * 设置选择模式
     * @param model
     * 1表示从相机选择，其他表示从图库选择
     * */
    public void setModel(String model) {
        this.model = Integer.parseInt(model.trim());
    }

    /**
     * 设置ftp地址、端口号、账号、密码及目标文件夹
     * @param ftpUrl
     * ftp地址、端口号、账号、密码及目标文件夹
     * */
    public void setFtpUrl(String ftpUrl){
        this.ftpUrl = ftpUrl;
    }

    /**
     * 设置上传模式
     * @param loadType
     * 1表示ftp上传，其他表示服务器上传
     * */
    public void setLoadType(String loadType){
        this.loadType = Integer.parseInt(loadType.trim());
    }

    /**
     * 设置文件名称
     * @param fileName
     * 文件名称
     * */
    public void addFileName(String fileName) {
        if (fileName==null&&"".equals(fileName))
            return;
        filenameList.add(fileName);
    }

    /**
     * 本类监听事件
     * */
    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                curPosition = pos;
                chooseImage();
            }
            return true;
        }
    };

    /**
     * TakePhoto对象
     * */
    private TakePhoto takePhoto;

    /**
     * 初始化TakePhoto并将其添加至Map集合
     * */
    private void initTakePhoto(){
        takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(new InvokeListener() {
            @Override
            public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
                PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(ctx), invokeParam.getMethod());
                return type;
            }
        }).bind(new TakePhotoImpl(ctx, new TakePhoto.TakeResultListener() {
            @Override
            public void takeSuccess(final TResult result) {
                Handler handler = PhotoSelector.handlerMap.get(PhotoSelector.curPosition);
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = PhotoSelector.PHOTO_WITH_DATA;
                    ArrayList list  = result.getImages();
                    String[] s = new String[list.size()];
                    for (int i=0;i<list.size();i++){
                        s[i] = result.getImages().get(i).getCompressPath();
                    }
                    msg.obj = s;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void takeFail(TResult result, String msg) {
            }

            @Override
            public void takeCancel() {
            }
        }));
        takePhotoMap.put(position, takePhoto);
    }

    /**
     * 配置TakePhoto对象
     * */
    private void configTakePhoto() {

        int maxSize = 102400;
        int width = 800;
        int height = 800;
        CompressConfig config;
        config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(true)
                .create();
        takePhoto.onEnableCompress(config, false);

        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());


    }

    /**
     * 从相机或图库选择图片
     * */
    private void chooseImage() {
        if (FileUtils.checkSDCard()) {
            if (model == 1) {
                camera();
            } else {
                CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                builder.setTitle(R.string.axeac_msg_choice);
                builder.setMessage(R.string.axeac_msg_choice_img_note);
                builder.setPositiveButton(R.string.axeac_msg_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        configTakePhoto();
                        takePhoto.onPickMultiple(6 - fileNames.size());

                    }
                });
                builder.setNeutralButton(R.string.axeac_msg_camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        camera();

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
        } else {
            Toast.makeText(ctx, R.string.axeac_msg_sdcard_noexist, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开系统相机
     * */
    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(FileUtils.getSDCardPath() + FILE_PATH);
        if (!dir.exists()
                && !dir.mkdirs())
        {
            dir.mkdirs();
        }

        mCurrentPhotoFile = new File(dir, System.currentTimeMillis() + ".png");
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion<24){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            ctx.startActivityForResult(intent, CAMERA_WITH_DATA);
        }else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mCurrentPhotoFile.getAbsolutePath());
            Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ctx.startActivityForResult(intent, CAMERA_WITH_DATA);
        }
    }

    /**
     * 接收消息的Handler
     * */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CAMERA_WITH_DATA:
                    cameraWithData();
                    break;
                case PHOTO_WITH_DATA:
                    chooseWithData((String[]) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 图片为相机数据时执行的方法
     * */
    private void cameraWithData() {
        if (mCurrentPhotoFile != null && mCurrentPhotoFile.exists()) {
            returnVal.add(mCurrentPhotoFile.getAbsolutePath());
            fileNames.add(mCurrentPhotoFile.getAbsolutePath());
            if (fileNames.size()!=0){
                gridView.setVisibility(View.VISIBLE);
                PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(ctx,fileNames,gridView,false);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } else {
//            previewImg.setVisibility(View.GONE);
        }
    }

    /**
     * 图片为图库数据是执行的方法
     * @param filePath
     * 图片地址
     * */
    private void chooseWithData(String[] filePath) {
        if (filePath.length==-1) {
            gridView.setVisibility(View.GONE);
        } else {
            for (int i=0;i<filePath.length;i++){
                if (fileNames.size()<6)
                    fileNames.add(filePath[i]);
                returnVal.add(filePath[i]);
            }
            if (fileNames.size()!=0) {
                gridView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = gridView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                gridView.setLayoutParams(params);
                PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(ctx,fileNames,gridView,false);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (fileNames.size()==6){
                    params.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 208, ctx.getResources().getDisplayMetrics()));
                    gridView.setLayoutParams(params);
                }

            }
//            previewImg.setImageBitmap(getBitmapByPath(filePath));

        }
    }

    /**
     * 根据地址获得图片
     * @param imageFile
     * 图片地址
     * */
    private static Bitmap getBitmapByPath(String imageFile) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 500 * 400);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageFile, opts);
    }

    /**
     * 计算图片样品尺寸
     * @param options
     * BotmapFactory.Options对象
     * @param minSideLength
     * 最小滑动长度
     * @param maxNumOfPixels
     * 图片尺寸
     * */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    /**
     * 计算最初图片尺寸
     * @param options
     * BitmapFactory.Options对象
     * @param minSideLength
     * 最小滑动长度
     * @param maxNumOfPixels
     * 图片尺寸
     * */
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (!readOnly) {
//            previewImg.setOnTouchListener(listener);
        }
        String familyName = null;
        int style = Typeface.NORMAL;
//        if (this.font != null && !"".equals(this.font)) {
//            if (this.font.indexOf(";") != -1) {
//                String[] strs = this.font.split(";");
//                for (String str : strs) {
//                    if (str.startsWith("size")) {
//                        int index = str.indexOf(":");
//                        if (index == -1)
//                            continue;
//                        String s = str.substring(index + 1).trim();
//                        textField.setTextSize(Float.parseFloat(s.replace("px", "").trim()));
//                    } else if (str.startsWith("family")) {
//                        int index = str.indexOf(":");
//                        if (index == -1)
//                            continue;
//                        familyName = str.substring(index + 1).trim();
//                    } else if (str.startsWith("style")) {
//                        int index = str.indexOf(":");
//                        if (index == -1)
//                            continue;
//                        String s = str.substring(index + 1).trim();
//                        if ("bold".equals(s)) {
//                            style = Typeface.BOLD;
//                        } else if ("italic".equals(s)) {
//                            style = Typeface.ITALIC;
//                        } else {
//                            if (s.indexOf(",") != -1) {
//                                if ("bold".equals(s.split(",")[0]) && "italic".equals(s.split(",")[1])) {
//                                    style = Typeface.BOLD_ITALIC;
//                                }
//                                if ("bold".equals(s.split(",")[1]) && "italic".equals(s.split(",")[0])) {
//                                    style = Typeface.BOLD_ITALIC;
//                                }
//                            }
//                        }
//                    } else if (str.startsWith("color")) {
//                        int index = str.indexOf(":");
//                        if (index == -1)
//                            continue;
//                        String s = str.substring(index + 1).trim();
//                        if (CommonUtil.validRGBColor(s)) {
//                            int r = Integer.parseInt(s.substring(0, 3));
//                            int g = Integer.parseInt(s.substring(3, 6));
//                            int b = Integer.parseInt(s.substring(6, 9));
//                            textField.setTextColor(Color.rgb(r, g, b));
//                        }
//                    }
//                }
//            }
//        }
//        if (familyName == null || "".equals(familyName)) {
//            textField.setTypeface(Typeface.defaultFromStyle(style));
//        } else {
//            textField.setTypeface(Typeface.create(familyName, style));
//        }
    }

    /**
     * 返回当前视图
     * @return
     * 当前视图
     * */
    @Override
    public View getView() {
        if (!isHavaDwon) {
            isHavaDwon = true;
            if (filenameList.size() > 0) {
//                previewImg.setVisibility(View.GONE);
                if(loadType!=1){
                    boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                    for (int i=0;i<filenameList.size();i++){
                        String RequestURL = (isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") + ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/Download?meip_filename=" + filenameList.get(i);
                        final int finalI = i;
                        Glide.with(ctx).load(RequestURL).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
                            @Override
                            public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                                try {
                                    savaBitmap(filenameList.get(finalI), bytes);
                                    if (fileNames.size()>0)
                                        gridView.setAdapter(new PhotoSelectorAdapter(ctx,fileNames,gridView,true));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
//                    String RequestURL = (isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") + ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/Download?meip_filename=" + fileName;
//                    Glide.with(ctx)
//                            .load(RequestURL)
//                            .asBitmap()
//                            .into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                                    Intent intent = new Intent();
//                                    intent.setAction(StaticObject.BTN_OFF_ACTION);
//                                    LocalBroadcastManager
//                                            .getInstance(ctx).sendBroadcast(intent);
//                                    if (resource != null) {
//                                        previewImg.setVisibility(View.VISIBLE);
//                                        previewImg.setImageBitmap(resource);
//                                        previewImg.invalidate();
//                                        String path = FileUtils.getSDCardPath() + DOWM_PICTURE;
//                                        File dirFile = new File(path);
//                                        if(!dirFile.exists()){
//                                            dirFile.mkdir();
//                                        }
////                                        returnVal = path + fileName;
//                                        File myCaptureFile = new File(path);
//                                        BufferedOutputStream bos = null;
//                                        try {
//                                            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//                                            resource.compress(Bitmap.CompressFormat.JPEG, 80, bos);
//                                            bos.flush();
//                                            bos.close();
//                                        } catch (FileNotFoundException e) {
//                                            e.printStackTrace();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onLoadStarted(Drawable placeholder) {
//                                    super.onLoadStarted(placeholder);
//                                    Intent intent = new Intent();
//                                    intent.setAction(StaticObject.BTN_NO_ACTION);
//                                    LocalBroadcastManager
//                                            .getInstance(ctx).sendBroadcast(intent);
//                                }
//
//                                @Override
//                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                                    super.onLoadFailed(e, errorDrawable);
//                                    Toast.makeText(ctx,"加载图片失败："+e,Toast.LENGTH_SHORT).show();
//                                }
//                            });

                }else{
                    new GetBitmapService().execute();
                }
            }
        }
        return super.getView();
    }

    public void savaBitmap(String imgName, byte[] bytes) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = null;
            FileOutputStream fos = null;
            try {
                filePath = FileUtils.getSDCardPath() + DOWM_PICTURE;
                File imgDir = new File(filePath);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                String imgPath = filePath + "/" + imgName;
                fos = new FileOutputStream(imgPath);
                fos.write(bytes);
                fileNames.add(imgPath);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(ctx, "请检查SD卡是否可用", Toast.LENGTH_SHORT).show();
        }
    }

    class GetBitmapService extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params){
            for (int i=0;i<filenameList.size();i++) {
                returnVal.add(FileUtils.getSDCardPath() + DOWM_PICTURE + filenameList.get(i));
                File file = new File(FileUtils.getSDCardPath() + DOWM_PICTURE+filenameList.get(i));
                if (file.exists()) {
                    fileNames.add(FileUtils.getSDCardPath() + DOWM_PICTURE+filenameList.get(i));
                } else {
                    String strPath = FileUtils.getSDCardPath() + DOWM_PICTURE;
                    if (ftpUrl != null || !"".equals(ftpUrl)) {
                        String[] urlFtp = ftpUrl.split(";");
                        if (urlFtp.length == 5) {
                            try {
                                new FtpUtils().downloadBitmap(urlFtp[0], urlFtp[1], urlFtp[2], urlFtp[3], urlFtp[4] + "/" + filenameList.get(i), strPath, filenameList.get(i), new FtpUtils.DownLoadProgressListener() {
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
                    } else {
                        Toast.makeText(ctx, R.string.axeac_msg_ftp, Toast.LENGTH_SHORT).show();
                    }

                    fileNames.add(FileUtils.getSDCardPath() + DOWM_PICTURE+filenameList.get(i));
                }
                PhotoSelectorAdapter adapter = new PhotoSelectorAdapter(ctx,fileNames,gridView,false);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object result) {
//            previewImg.setVisibility(View.VISIBLE);
//            previewImg.setImageBitmap((Bitmap)result);
//            previewImg.invalidate();
        }
    }

    /**
     * 上传图片的AsyncTask类
     * */
    class SetBitmapService extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                for (int i=0;i<fileNames.size();i++){
                    ImageUtils.compressImageByPath(fileNames.get(i));
                    boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
                    String RequestURL = (isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") + ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/Upload";
                    HttpAssist.uploadFile(new File(returnVal.get(i)), RequestURL, fileNames.get(i).substring(fileNames.get(i).lastIndexOf("/"),fileNames.get(i).length()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 返回文件名称
     * @return
     * 文件名称
     * */
    @Override
    public String getValue() {
        if (fileNames.size()>0) {
            fileName="";
            for (int j=0;j<fileNames.size();j++){
                int start_index = fileNames.get(j).lastIndexOf("/")+1;
                int end_index = fileNames.get(j).length();
                if (j==fileNames.size()-1) {
                    fileName += fileNames.get(j).substring(start_index,end_index);
                }else{
                    fileName += fileNames.get(j).substring(start_index,end_index) + ",";
                }
            }
            if (!isHavaUp ) {
                isHavaUp = true;
                for (int i =0;i<fileNames.size();i++) {
//                    returnFileName = UUID.randomUUID().toString() + returnVal.get(i).substring(returnVal.get(i).lastIndexOf("."), returnVal.get(i).length());

                    if (loadType != 1) {
                        new SetBitmapService().execute(fileNames);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (ftpUrl != null || !"".equals(ftpUrl)) {
                                    String[] ftpurl = ftpUrl.split(";");
                                    if (ftpurl.length == 5) {
                                        for(int i=0;i<fileNames.size();i++) {
                                            try {
                                                // Get saved to the local paintingView image and upload it to the FTP server
                                                // 获取保存到本地的paintingView图片，上传到FTP服务器
                                                new FtpUtils().
                                                        uploadBitmap(ftpurl[0], ftpurl[1], ftpurl[2], ftpurl[3], new File(fileNames.get(i)), "/" + ftpurl[4], new FtpUtils.UploadProgressListener() {
                                                            @Override
                                                            public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                                                // TODO Auto-generated method stub
                                                                if (currentStep.equals(FtpUtils.FTP_UPLOAD_SUCCESS)) {
                                                                } else if (currentStep.equals(FtpUtils.FTP_UPLOAD_LOADING)) {
                                                                }
                                                            }
                                                        });
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                } else {
                                    Toast.makeText(ctx, R.string.axeac_msg_ftp, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).start();
                    }
                }
            }
            return fileName;
        }
        return fileName;
    }

    @Override
    public void repaint() {}

    @Override
    public void starting() {
        this.buildable = false;
    }

    @Override
    public void end() {
        this.buildable = true;
    }
}