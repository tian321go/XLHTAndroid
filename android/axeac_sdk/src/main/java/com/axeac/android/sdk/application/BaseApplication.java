package com.axeac.android.sdk.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/12/4.
 */

public class BaseApplication extends Application {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_BACK_TO_FRONT = 1;
    public static final int STATE_FRONT_TO_BACK = 2;
    public static int sAppState = STATE_NORMAL;
    private int mVisibleActivityCount = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        initActivityLife();
    }

    private void initActivityLife(){
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                // 每有一个activity可见都会走该方法，mVisibleActivityCount会增1。
                mVisibleActivityCount++;
                if (mVisibleActivityCount == 1) {
                    // 从后台进入前台
                    sAppState = STATE_BACK_TO_FRONT;
                } else {
                    // 否则是正常状态
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //每有一个acitivity不可见都会走该方法，让mVisibleActivityCount减1。
                mVisibleActivityCount--;
                if (mVisibleActivityCount == 0) {
                    // 从前台进入后台
                    sAppState = STATE_FRONT_TO_BACK;
                } else {
                    // 否则是正常状态
                    sAppState = STATE_NORMAL;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
