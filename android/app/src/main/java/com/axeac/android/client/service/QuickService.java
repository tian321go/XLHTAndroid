package com.axeac.android.client.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.util.Log;

import com.axeac.android.sdk.utils.StaticObject;

import java.util.List;

@TargetApi(Build.VERSION_CODES.N)
public class QuickService extends TileService {

    public static final String TAG = QuickService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onDestroy");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(TAG, "onStartListening");
        Tile tile = getQsTile();
        if (tile != null) {
//            if (isServiceRunning(this, TaskCountTaskService.class.getName())&&isServiceRunning(this, MsgCountTaskService.class.getName())) {
//                tile.setState(Tile.STATE_ACTIVE);
//                tile.setLabel("WorkCenter");
//            } else {
//                tile.setState(Tile.STATE_INACTIVE);
//                tile.setLabel("WorkCenter");
//            }

//            if (tile.getState() == Tile.STATE_ACTIVE) {
//                tile.setLabel("WorkCenter");
//            } else {
//                tile.setLabel("Running");
//            }
            tile.updateTile();
        }
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d(TAG, "onStopListening");
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(TAG, "onTileAdded");
        Tile tile = getQsTile();
        if (tile != null) {
            Log.d(TAG, "getQsTile");
        }
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(TAG, "onTileRemoved");
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.d(TAG, "onClick");

        Tile tile = getQsTile();
        if (tile != null) {
            if (tile.getState() == Tile.STATE_ACTIVE) {
//                tile.setLabel("WorkCenter");
//                tile.setState(Tile.STATE_INACTIVE);
//                Intent iService = new Intent(QuickService.this, TaskCountTaskService.class);
//                stopService(iService);
//                Intent msgService = new Intent(QuickService.this, MsgCountTaskService.class);
//                stopService(msgService);
            } else {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    PollingUtils.startPollingService(QuickService.this, StaticObject.read.getLong(StaticObject.SYSTEMSETUPS_MSGTIMES,10)*60000, TaskCountTaskService.class, TaskCountTaskService.ACTION);
//                    PollingUtils.startPollingService(QuickService.this, StaticObject.read.getLong(StaticObject.SYSTEMSETUPS_MSGTIMES,10)*60000, MsgCountTaskService.class, MsgCountTaskService.ACTION);
//                }else{
//                    PollingUtils.startPollingService(QuickService.this, (int) StaticObject.read.getLong(StaticObject.SYSTEMSETUPS_MSGTIMES,10)*60000, TaskCountTaskService.class, TaskCountTaskService.ACTION);
//                    PollingUtils.startPollingService(QuickService.this, (int) (StaticObject.read.getLong(StaticObject.SYSTEMSETUPS_MSGTIMES,10)*60000), MsgCountTaskService.class, MsgCountTaskService.ACTION);
//                }
//                tile.setLabel("WorkCenter");
                tile.setState(Tile.STATE_ACTIVE);
            }
            tile.updateTile();
        }
    }


    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;

        if (TextUtils.isEmpty(serviceName)) {
            return isRunning;
        }

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServiceInfos) {
            if (service.service.getClassName().equalsIgnoreCase(serviceName)) {
                if (service.uid == context.getApplicationInfo().uid) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }
}
