package com.axeac.android.sdk.scanner.common;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * describe:Compatible with low version of the child thread to open the task
 * <br>兼容低版本的子线程开启任务
 * @author axeac
 * @version 1.0.0
 */
public class Runnable {

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public static void execAsync(AsyncTask task) {
		if (Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else {
			task.execute();
		}

	}

}
