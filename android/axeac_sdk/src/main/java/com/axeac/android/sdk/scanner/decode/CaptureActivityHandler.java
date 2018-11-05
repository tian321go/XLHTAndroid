package com.axeac.android.sdk.scanner.decode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.util.Collection;
import java.util.Map;

import com.axeac.android.sdk.activity.CaptureActivity;
import com.axeac.android.sdk.scanner.camera.CameraManager;
import com.axeac.android.sdk.scanner.common.Config;
import com.axeac.android.sdk.scanner.view.ViewfinderResultPointCallback;

/**
 * describe:This class handles all the messaging which comprises the state machine for capture.
 * <br>该类处理包含状态机制捕获的所有消息。
 * @author axeac
 * @version 1.0.0
 */
public final class CaptureActivityHandler extends Handler {

	private static final String TAG = CaptureActivityHandler.class
			.getSimpleName();

	private final CaptureActivity activity;

	// describe:Really responsible for scanning the core task of the thread
	/**
	 * 真正负责扫描任务的核心线程
	 */
	private final DecodeThread decodeThread;

	private State state;

	private final CameraManager cameraManager;

	// describe:The state of the current scan
	/**
	 * 当前扫描的状态
	 */
	private enum State {
		/**
		 * describe：preview
		 * 描述：预览
		 */
		PREVIEW,
		/**
		 * describe:the scan was successful
		 * 描述：扫描成功
		 */
		SUCCESS,
		/**
		 * describe：end the scan
		 * 描述：结束扫描
		 */
		DONE
	}

	public CaptureActivityHandler(CaptureActivity activity,
								  Collection<BarcodeFormat> decodeFormats,
								  Map<DecodeHintType, ?> baseHints, String characterSet,
								  CameraManager cameraManager) {
		this.activity = activity;

		// start scan thread
		// 启动扫描线程
		decodeThread = new DecodeThread(activity, decodeFormats, baseHints,
				characterSet, new ViewfinderResultPointCallback(
				activity.getViewfinderView()));
		decodeThread.start();

		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		// 开始自己的捕捉预览和解码
		this.cameraManager = cameraManager;

		// Open the camera preview interface
		// 开启相机预览界面
		cameraManager.startPreview();

		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			// Ready for the next scan
			// 准备进行下一次扫描
			case Config.restart_preview:
				Log.d(TAG, "Got restart preview message");
				restartPreviewAndDecode();
				break;
			case Config.decode_succeeded:
				Log.d(TAG, "Got decode succeeded message");
				state = State.SUCCESS;
				Bundle bundle = message.getData();
				Bitmap barcode = null;
				float scaleFactor = 1.0f;
				if (bundle != null) {
					byte[] compressedBitmap = bundle
							.getByteArray(DecodeThread.BARCODE_BITMAP);
					if (compressedBitmap != null) {
						barcode = BitmapFactory.decodeByteArray(
								compressedBitmap, 0, compressedBitmap.length,
								null);
						barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
					}
					scaleFactor = bundle
							.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
				}
				activity.handleDecode((Result) message.obj, barcode,
						scaleFactor);
				break;
			case Config.decode_failed:
				// We're decoding as fast as possible, so when one decode fails,start another.\
				// 我们尽可能快的进行解码，所以当一个解码失败时，启动另一个解码
				state = State.PREVIEW;
				cameraManager.requestPreviewFrame(decodeThread.getHandler(),
						Config.decode);
				break;
			case Config.return_scan_result:
				Log.d(TAG, "Got return scan result message");
				activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
				activity.finish();
				break;
			case Config.launch_product_query:
				Log.d(TAG, "Got product query message");
				String url = (String) message.obj;

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.setData(Uri.parse(url));

				/**
				 * describe:This code is zxing project group Want to use chrome to open the browser and browser url
				 * 描述：这段代码是zxing项目组想要用chrome打开浏览器浏览url
				 */
				ResolveInfo resolveInfo = activity.getPackageManager()
						.resolveActivity(intent,
								PackageManager.MATCH_DEFAULT_ONLY);
				String browserPackageName = null;
				if (resolveInfo != null && resolveInfo.activityInfo != null) {
					browserPackageName = resolveInfo.activityInfo.packageName;
					Log.d(TAG, "Using browser in package " + browserPackageName);
				}

				// Needed for default Android browser / Chrome only apparently
				//只有默认android浏览器或者chrome浏览器才可以
				if ("com.android.browser".equals(browserPackageName)
						|| "com.android.chrome".equals(browserPackageName)) {
					intent.setPackage(browserPackageName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							browserPackageName);
				}

				try {
					activity.startActivity(intent);
				}
				catch (ActivityNotFoundException ignored) {
					Log.w(TAG, "Can't find anything to handle VIEW of URI "
							+ url);
				}
				break;
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), Config.quit);
		quit.sendToTarget();

		try {
			// Wait at most half a second; should be enough time, and onPause() will timeout quickly
			//等待最多半秒钟; 应该有足够的时间，onPause（）会很快超时
			decodeThread.join(500L);
		}
		catch (InterruptedException e) {

		}

		// Be absolutely sure we don't send any queued up messages
		// 一定确保不要发送任何队列消息
		removeMessages(Config.decode_succeeded);
		removeMessages(Config.decode_failed);
	}

	// describe:After completing a scan, you only need to call this method again
	/**
	 * 完成一次扫描后，只需要再调用此方法即可
	 */
	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;

			// Send a decode message to the decodeThread bound handler (DecodeHandler)
			// 向decodeThread绑定的handler（DecodeHandler)发送解码消息
			cameraManager.requestPreviewFrame(decodeThread.getHandler(),
					Config.decode);
			activity.drawViewfinder();
		}
	}

}
