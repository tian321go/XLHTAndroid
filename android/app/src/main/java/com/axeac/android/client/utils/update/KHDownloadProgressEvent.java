package com.axeac.android.client.utils.update;

/**
 * describe:Download  progress event object
 * <br>下载进度事件对象
 * @author axeac
 * @version 2.3.0.0001
 */
public class KHDownloadProgressEvent {
    // describe:The total file size
    /**
     * 文件总大小
     */
    private long mTotal;
    // describe：Current download progress
    /**
     * 当前下载进度
     */
    private long mProgress;

    public KHDownloadProgressEvent(long total, long progress) {
        mTotal = total;
        mProgress = progress;
    }

    // describe:Get the total file size
    /**
     * 获取文件总大小
     *
     * @return
     * 文件总大小
     */
    public long getTotal() {
        return mTotal;
    }

    /**
     * 返回下载进度
     * @return
     * 下载进度
     */
    public long getProgress() {
        return mProgress;
    }

    // describe:Whether or not that it not yet  Download completed
    /**
     * 是否还没有下载完成
     *
     * @return
     * 是否下载完毕
     */
    public boolean isNotDownloadFinished() {
        return mTotal != mProgress;
    }
}
