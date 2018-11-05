package com.axeac.android.sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.scanner.AmbientLightManager;
import com.axeac.android.sdk.scanner.BeepManager;
import com.axeac.android.sdk.scanner.FinishListener;
import com.axeac.android.sdk.scanner.InactivityTimer;
import com.axeac.android.sdk.scanner.IntentSource;
import com.axeac.android.sdk.scanner.camera.CameraManager;
import com.axeac.android.sdk.scanner.common.BitmapUtils;
import com.axeac.android.sdk.scanner.common.Config;
import com.axeac.android.sdk.scanner.decode.BitmapDecoder;
import com.axeac.android.sdk.scanner.decode.CaptureActivityHandler;
import com.axeac.android.sdk.scanner.view.ViewfinderView;
import com.axeac.android.sdk.ui.CodeScan;

/**
 *this Activity dose:
 * 1、Open camera, complete the scanning task in the background independent thread;
 * 2、A scan area (viewfinder) is drawn to help the user place the bar code for accurate scanning;
 * 3、After the scan is successful, the scan results are displayed on the interface.
 *
 * <br>此Activity所做的事：
 * <br>1.开启camera，在后台独立线程中完成扫描任务；
 * <br>2.绘制了一个扫描区（viewfinder）来帮助用户将条码置于其中以准确扫描；
 * <br>3.扫描成功后会将扫描结果展示在界面上。
 *
 * @author axeac
 * @version 1.0.0
 */
public final class CaptureActivity extends BaseActivity implements
        SurfaceHolder.Callback{

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 100;

    private static final int PARSE_BARCODE_FAIL = 300;
    private static final int PARSE_BARCODE_SUC = 200;

    // describe:preview?
    /**
     * 是否有预览
     */
    private boolean hasSurface;

    // describe:Activity monitor. If the phone does not connect the power cord and  camera is turned on but not used, the service will shut down the current activity.
    // The active monitor omnidistance the active state of the entire monitor, the same as the CaptureActivity life cycle. After each scan, the monitor is reset, that is, the countdown again.
    /**
     * 活动监控器。如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
     * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
     */
    private InactivityTimer inactivityTimer;

    // describe:Sound vibration manager. If the scan is successful, you can play an audio, or you can vibrate reminder, you can configure to determine the behavior of the scan after success.
    /**
     * 声音震动管理器。如果扫描成功后可以播放一段音频，也可以震动提醒，可以通过配置来决定扫描成功后的行为。
     */
    private BeepManager beepManager;

    // describe:Flash regulator. Automatically detects ambient light intensity and determines whether or not to turn on the flash
    /**
     * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
     */
    private AmbientLightManager ambientLightManager;

    /**
     * CameraManger对象
     * */
    private CameraManager cameraManager;

    // describe:Scan area
    /**
     * 扫描区域
     */
    private ViewfinderView viewfinderView;

    /**
     * CaptureActivityHandler对象
     * */
    private CaptureActivityHandler handler;

    /**
     * Result对象
     * */
    private Result lastResult;

    /**
     * 是否开启闪光灯标志
     * */
    private boolean isFlashlightOpen;

    // [Auxiliary decoding parameters (used as arguments for MultiFormatReader)]Encoding type that tells the scanner what encoding to decode, that is, EAN-13, QR
    // Code, etc., correspond to the DecodeHintType.POSSIBLE_FORMATS type.
    // Refer to the following code in the DecodeThread constructor: hints.put (DecodeHintType.POSSIBLE_FORMATS,decodeFormats);
    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】 编码类型，该参数告诉扫描器采用何种编码方式解码，即EAN-13，QR
     * Code等等 对应于DecodeHintType.POSSIBLE_FORMATS类型
     * 参考DecodeThread构造函数中如下代码：hints.put(DecodeHintType.POSSIBLE_FORMATS,
     * decodeFormats);
     */
    private Collection<BarcodeFormat> decodeFormats;

    // [Auxiliary decoding parameters (used as arguments for MultiFormatReader)]This parameter will eventually be passed in MultiFormatReader，
    // The decodeFormats and characterSet will eventually be added to the decodeHints and eventually set to MultiFormatReader
    // Refer to the following code in the DecodeHandler constructor:multiFormatReader.setHints(hints);
    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】 该参数最终会传入MultiFormatReader，
     * 上面的decodeFormats和characterSet最终会先加入到decodeHints中 最终被设置到MultiFormatReader中
     * 参考DecodeHandler构造器中如下代码：multiFormatReader.setHints(hints);
     */
    private Map<DecodeHintType, ?> decodeHints;

    // [Auxiliary decoding parameters(used as arguments for MultiFormatReader)]A character set that tells the scanner what kind of character set to decode
    // correspond to the DecodeHintType.CHARACTER_SET type
    // Refer to the following code in the DecodeThread constructor:hints.put(DecodeHintType.CHARACTER_SET,characterSet);\
    /**
     * 【辅助解码的参数(用作MultiFormatReader的参数)】 字符集，告诉扫描器该以何种字符集进行解码
     * 对应于DecodeHintType.CHARACTER_SET类型
     * 参考DecodeThread构造器如下代码：hints.put(DecodeHintType.CHARACTER_SET,
     * characterSet);
     */
    private String characterSet;

    /**
     * Result对象
     * */
    private Result savedResultToShow;

    /**
     * IntentSource对象
     * */
    private IntentSource source;

    // describe:Picture path
    /**
     * 图片的路径
     */
    private String photoPath;

    private Handler mHandler = new MyHandler(this);

    private class MyHandler extends Handler {

        private WeakReference<Activity> activityReference;

        public MyHandler(Activity activity) {
            activityReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                //Parse picture successful
                // 解析图片成功
                case PARSE_BARCODE_SUC:
                    dealResult(msg.obj.toString());
                    break;

                //Parse picture failed
                // 解析图片失败
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(activityReference.get(), getString(R.string.axeac_faile_xiexi),
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.axeac_capture);
        setStatusBarTint(this, android.R.color.transparent);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        //listen picture recognition button
        // 监听图片识别按钮
        findViewById(R.id.capture_scan_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        findViewById(R.id.capture_flashlight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashlightOpen) {
                    //close Torch
                    // 关闭闪光灯
                    cameraManager.setTorch(false);
                    isFlashlightOpen = false;
                } else {
                    //open Torch
                    // 打开闪光灯
                    cameraManager.setTorch(true);
                    isFlashlightOpen = true;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
          The camera initialization action involves opening the camera and measuring the screen size
          It is not recommended to put it in onCreate because if you include the code for displaying the help information for the first time in onCreate,
          Bug that causes the size of the scan window to be calculated incorrectly

          相机初始化的动作需要开启相机并测量屏幕大小，这些操作
          不建议放到onCreate中，因为如果在onCreate中加上首次启动展示帮助信息的代码的话，
          会导致扫描窗口的尺寸计算有误的bug
        */
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.capture_viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;
        lastResult = null;
        /*
            The camera preview function must be aided by SurfaceView, so it needs to be initialized at the beginning
           摄像头预览功能必须借助SurfaceView，因此也需要在一开始对其进行初始化
        */

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view); // 预览
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            /*
            The activity was paused but not stopped, so the surfaceview still
             exists. Therefore surfaceCreated() won't be called,
             and init the camera here.

             Activity只是暂停状态不是停止状态，所以surfaceview依然存在
             所以不需要再次调用surfaceCreated，直接初始化相机就可以
            */

            initCamera(surfaceHolder);

        } else {
            // Prevent sdk8 device initialization exception
            // 防止sdk8的设备初始化预览异常
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            // Install the callback and wait for surfaceCreated() to init the camera.
            //调用回调方法，等待surfaceCreated初始化相机
            surfaceHolder.addCallback(this);
        }

        // Loading the sound configuration, in fact, will also be called in the constructor of the BeemManager, that is, it will be called once in onCreate
        // 加载声音配置，其实在BeemManager的构造器中也会调用该方法，即在onCreate的时候会调用一次
        beepManager.updatePrefs();

        //start AmbientLightManager
        // 启动闪光灯调节器
        ambientLightManager.start(cameraManager);
        // resume InactivityTimer
        // 恢复活动监控器
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();

        //close camera
        // 关闭摄像头
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((source == IntentSource.NONE) && lastResult != null) { // 重新进行扫描
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                // 处理这些事件，以免启动camera应用程序
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.zoomIn();
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.zoomOut();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    // describe:Processing of returned results
    /**
     * 对于返回结果的处理
     * @param requestCode
     * 请求码
     * @param resultCode
     * 返回码
     * @param intent
     * Intent对象
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            final ProgressDialog progressDialog;
            switch (requestCode) {
                case REQUEST_CODE:
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoPath = cursor.getString(columnIndex);
                    cursor.close();


                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage(getString(R.string.axeac_scaning));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Bitmap img = BitmapUtils.getCompressedBitmap(photoPath);

                            BitmapDecoder decoder = new BitmapDecoder(
                                    CaptureActivity.this);
                            Result result = decoder.getRawResult(img);

                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = ResultParser.parseResult(result).toString();
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                mHandler.sendMessage(m);
                            }

                            progressDialog.dismiss();

                        }
                    }).start();
                    break;
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        hasSurface = false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    // A valid barcode has been found, so give an indication of success and show
    // the results.
    /**
     * 一个有效的条形码已经找到，给出成功提示并展示结果
     * @param rawResult
     * The contents of the barcode.
     * 条码内容
     * @param scaleFactor
     * amount by which thumbnail was scaled
     * 缩放比例
     * @param barcode
     * A greyscale bitmap of the camera data which was decoded.
     * 解码位图
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        // reclocking
        // 重新计时
        inactivityTimer.onActivity();

        lastResult = rawResult;

        // The picture shows the scanning frame
        // 把图片画到扫描框
        viewfinderView.drawResultBitmap(barcode);

        beepManager.playBeepSoundAndVibrate();

        dealResult(ResultParser.parseResult(rawResult).toString());

    }

    /**
     * 超时后重新开始预览界面
     * @param delayMS
     * 超时时间
     * */
    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(Config.restart_preview, delayMS);
        }
        resetStatusView();
    }

    /**
     * 返回ViewfinderView对象
     * @return
     * ViewfinderView对象
     * */
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    /**
     * 返回Handler对象
     * @return
     * Handler对象
     * */
    public Handler getHandler() {
        return handler;
    }

    /**
     * 返回CameraManger对象
     * @return
     * CameraManger对象
     * */
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    /**
     * 重置状态视图
     * */
    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    /**
     * 绘制ViewfinderView视图
     * */
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 初始化相机
     * */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            // 创建handler启动预览，同时可以抛出RuntimeException
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe.toString());
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            /*
               Barcode Scanner has seen crashes in the wild of this variety:
              java.lang.RuntimeException: Fail to connect to camera service

              条码扫描崩溃
              java.lang.RuntimeException: 连接相机服务失败
            */
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    // describe:send message to CaptureActivityHanlder,and display scanner picture
    /**
     * 向CaptureActivityHandler中发送消息，并展示扫描到的图像
     * @param bitmap
     * Bitmap对象
     * @param result
     * Result对象
     */
    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        // 位图将被使用
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler,
                        Config.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    // describe:display BUGMessage dialog
    /**
     * 展示错误消息对话框
     * */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.axeac_msg_camera_framework_bug));
        builder.setPositiveButton(R.string.axeac_button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    private void dealResult(String result) {
        Handler handler = CodeScan.handlerMap.get(CodeScan.curPosition);
        if (handler != null) {
            Message msg = new Message();
            msg.what = CodeScan.CODESCAN_WITH_DATA;
            msg.obj = result;
            handler.sendMessage(msg);
        }
        Intent data = new Intent();
        data.putExtra("result", result);
        setResult(RESULT_OK, data);
        finish();
    }


}
