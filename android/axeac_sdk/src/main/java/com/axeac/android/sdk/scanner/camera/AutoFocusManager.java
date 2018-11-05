package com.axeac.android.sdk.scanner.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import com.axeac.android.sdk.scanner.common.Config;

/**
 * describe:Because of the focusing is not disposable to complete the task (handshaken),
 *          <br>The system provides only the Camera.autoFocus () method of focusing,Therefore,
 *          <br>you need a thread to call Camera.autoFocus () until the user is satisfied with pressing the shutter
 * <br>由于对焦不是一次性完成的任务（手抖），而系统提供的对焦仅有Camera.autoFocus()方法，
 * <br>因此需要一个线程来不断调用Camera.autoFocus()直到用户满意按下快门为止
 * @author axeac
 * @version 1.0.0
 */
final class AutoFocusManager implements Camera.AutoFocusCallback {

	private static final String TAG = AutoFocusManager.class.getSimpleName();

	private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
	private static final Collection<String> FOCUS_MODES_CALLING_AF;
	static {
		FOCUS_MODES_CALLING_AF = new ArrayList<String>(2);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
		FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
	}

	private boolean active;
	private final boolean useAutoFocus;
	private final Camera camera;
	private AsyncTask<?, ?, ?> outstandingTask;

	AutoFocusManager(Context context, Camera camera) {
		this.camera = camera;
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String currentFocusMode = camera.getParameters().getFocusMode();
		useAutoFocus = sharedPrefs.getBoolean(Config.KEY_AUTO_FOCUS, true)
				&& FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
		start();
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera theCamera) {
		if (active) {
			outstandingTask = new AutoFocusTask();
			com.axeac.android.sdk.scanner.common.Runnable.execAsync(outstandingTask);
		}
	}

	synchronized void start() {
		if (useAutoFocus) {
			active = true;
			try {
				camera.autoFocus(this);
			}
			catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+;
				// continue?
				//听说在android 4.0.x+会报告异常
				//是否继续
				Log.w(TAG, "Unexpected exception while focusing", re);
			}
		}
	}

	synchronized void stop() {
		if (useAutoFocus) {
			try {
				camera.cancelAutoFocus();
			}
			catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+;
				// continue?
				//听说在android 4.0.x+会报告异常
				//是否继续
				Log.w(TAG, "Unexpected exception while cancelling focusing", re);
			}
		}
		if (outstandingTask != null) {
			outstandingTask.cancel(true);
			outstandingTask = null;
		}
		active = false;
	}

	private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... voids) {
			try {
				Thread.sleep(AUTO_FOCUS_INTERVAL_MS);
			}
			catch (InterruptedException e) {
				// continue
			}
			synchronized (AutoFocusManager.this) {
				if (active) {
					start();
				}
			}
			return null;
		}
	}

}
