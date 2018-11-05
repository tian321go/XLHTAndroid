package com.axeac.android.sdk.scanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

/**
 * describe:The active monitor  omnidistance the active state of the scan, which is the same as
 *          <br>that of the CaptureActivity life cycle
 * <br>该活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同
 * @author axeac
 * @version 1.0.0
 */
public class InactivityTimer {

    private static final String TAG = InactivityTimer.class.getSimpleName();

    // describe:If the scanner is not used in 5min, the automatic finish out activity
    /**
     * 如果在5min内扫描器没有被使用过，则自动finish掉activity
     */
    private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

    // describe:In this app, this activity is CaptureActivity
    /**
     * 在本app中，此activity即为CaptureActivity
     */
    private final Activity activity;
    // describe:Accept the system radio: whether the phone is connected to power
    /**
     * 接受系统广播：手机是否连通电源
     */
    private final BroadcastReceiver powerStatusReceiver;
    private boolean registered;
    private AsyncTask<?, ?, ?> inactivityTask;

    public InactivityTimer(Activity activity) {
        this.activity = activity;
        powerStatusReceiver = new PowerStatusReceiver();
        registered = false;
        onActivity();
    }

    // describe:First terminate the previous monitoring tasks, and then start a new monitoring task
    /**
     * 首先终止之前的监控任务，然后新起一个监控任务
     */
    public synchronized void onActivity() {
        cancel();
        inactivityTask = new InactivityAsyncTask();
        com.axeac.android.sdk.scanner.common.Runnable.execAsync(inactivityTask);
    }

    public synchronized void onPause() {
        cancel();
        if (registered) {
            activity.unregisterReceiver(powerStatusReceiver);
            registered = false;
        } else {
            Log.w(TAG, "PowerStatusReceiver was never registered?");
        }
    }

    public synchronized void onResume() {
        if (registered) {
            Log.w(TAG, "PowerStatusReceiver was already registered?");
        } else {
            activity.registerReceiver(powerStatusReceiver, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));
            registered = true;
        }
        onActivity();
    }

    // describe:Cancel the monitoring task
    /**
     * 取消监控任务
     */
    private synchronized void cancel() {
        AsyncTask<?, ?, ?> task = inactivityTask;
        if (task != null) {
            task.cancel(true);
            inactivityTask = null;
        }
    }

    public void shutdown() {
        cancel();
    }

    // describe:Monitor whether the system is connected to the power supply. If the power is
    //          connected, stop monitoring tasks, or restart the monitoring task
    /**
     * 监听是否连通电源的系统广播。如果连通电源，则停止监控任务，否则重启监控任务
     */
    private final class PowerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 0 indicates that we're on battery
                // 0表示正在使用电池
                boolean onBatteryNow = intent.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED, -1) <= 0;
                if (onBatteryNow) {
                    InactivityTimer.this.onActivity();
                } else {
                    InactivityTimer.this.cancel();
                }
            }
        }
    }

    // describe:The task is simple, that is, after the INACTIVITY_DELAY_MS time to close the activity
    /**
     * 该任务很简单，就是在INACTIVITY_DELAY_MS时间后终结activity
     */
    private final class InactivityAsyncTask extends
            AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try {
                Thread.sleep(INACTIVITY_DELAY_MS);
                Log.i(TAG, "Finishing activity due to inactivity");
                activity.finish();
            } catch (InterruptedException e) {
            }
            return null;
        }
    }

}
