package com.axeac.android.client.utils.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.InputStream;

import com.axeac.android.sdk.utils.FileUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * 客户端升级工具类
 * @author axeac
 * @version 2.3.0.0001
 * */
public class KHUpgradeUtils {
    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";


    private KHUpgradeUtils() {
    }

    // describe:listen download progress
    /**
     * 监听下载进度
     */
    public static Observable<KHDownloadProgressEvent> getDownloadProgressEventObservable() {
        return RxUtil.getDownloadEventObservable();
    }

    // describe:Whether apk file has been downloaded, if it has been downloaded directly installed
    /**
     * apk 文件是否已经下载过，如果已经下载过就直接安装
     *
     * @param version
     * New apk file version
     * <br>新apk文件版本号
     * @return
     * true为已下载
     */
    public static boolean isApkFileDownloaded(Context context, String version) {
        File apkFile = FileUtils.getApkFile(context, version);
        if (apkFile.exists()) {
            installApk(context, apkFile);
            return true;
        }
        return false;
    }

    private static Call<ResponseBody> requsetCall;

    public static Call<ResponseBody> getRequsetCall() {
        return requsetCall;
    }

    // describe:Download the new apk
    /**
     * 下载新版 apk 文件
     * @param url
     * apk file url
     * <br>apk 文件路径
     * @param version
     * New apk file version
     * <br>新 apk 文件版本号
     */
    public static Observable<File> downloadApkFile(final Context context, final String url, final String version) {
        return Observable.defer(new Func0<Observable<InputStream>>() {
            @Override
            public Observable<InputStream> call() {
                try {
                    requsetCall = Engine.getInstance().getDownloadApi().downloadFile(url);
                    return Observable.just(requsetCall.execute().body().byteStream());
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).map(new Func1<InputStream, File>() {
            @Override
            public File call(InputStream inputStream) {
                return FileUtils.saveApk(context, inputStream, version);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // describe:Install apk file
    /**
     * 安装 apk 文件
     * @param context
     * Context对象
     * @param apkFile
     * apk文件
     */
    public static void installApk(Context context, File apkFile) {

        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(FileProvider.getUriForFile(context, getFileProviderAuthority(context), apkFile), MIME_TYPE_APK);
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), MIME_TYPE_APK);
        }

        if (context.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            context.startActivity(installApkIntent);
        }
    }

    // describe:Remove the old apk file that was downloaded before upgrading
    /**
     * 删除之前升级时下载的老的 apk 文件
     */
    public static void deleteOldApk(Context context) {
        File apkDir = FileUtils.getApkFileDir(context);
        if (apkDir == null || apkDir.listFiles() == null || apkDir.listFiles().length == 0) {
            return;
        }

        // delete files
        // 删除文件
        FileUtils.deleteFile(apkDir);
    }

    // describe:get auth of FileProvider
    /**
     * 获取FileProvider的auth
     * @param context
     * Context对象
     * @return
     * provider.authority
     */
    public static String getFileProviderAuthority(Context context) {
        try {
            for (ProviderInfo provider : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS).providers) {
                if (FileProvider.class.getName().equals(provider.name) && provider.authority.endsWith(".kh_update.file_provider")) {
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return null;
    }
}
