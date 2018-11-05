package com.axeac.android.sdk.scanner;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * describe:Simple listener used to exit the app in a few cases.
 * <br>简单的监听器用于在一些情况下退出应用程序。
 * @author axeac
 * @version 1.0.0
 */
public final class FinishListener implements DialogInterface.OnClickListener,
        DialogInterface.OnCancelListener {

    private final Activity activityToFinish;

    public FinishListener(Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        run();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        run();
    }

    private void run() {
        activityToFinish.finish();
    }

}
