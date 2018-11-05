package com.axeac.android.sdk.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Ftp工具类
 * @author axeac
 * @version 1.0.0
 * */
public class FtpUtils {
    private static final String TAG = "MainActivity";

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    private FTPClient ftpClient;
    public FtpUtils() {
        this.ftpClient = new FTPClient();
    }

    /**
     * 上传图片到ftp
     * @param hostName
     * ftp地址
     * @param serverPort
     * 端口号
     * @param userName
     * 用户名
     * @param password
     * 密码
     * @param singleFile
     * File对象
     * @param remotePath
     * ftp路径
     * @param listener
     * 上传监听
     * */
    public void uploadBitmap(String hostName,String serverPort,String userName,String password,File singleFile, String remotePath, UploadProgressListener listener) throws IOException {
        this.uploadBeforeOperate(hostName,serverPort,userName,password,remotePath, listener);
        boolean flag;
        flag = uploadingSingle(singleFile, listener);
        if (flag) {
            listener.onUploadProgress(FTP_UPLOAD_SUCCESS, 0,
                    singleFile);
        } else {
            listener.onUploadProgress(FTP_UPLOAD_FAIL, 0,
                    singleFile);
        }
        this.uploadAfterOperate(listener);
    }

    /**
     * 判断是否为单文件上传
     * @param localFile
     * File对象
     * @param listener
     * 上传监听
     * */
    private boolean uploadingSingle(File localFile,
                                    UploadProgressListener listener) throws IOException {
        boolean flag = true;

        BufferedInputStream buffIn = new BufferedInputStream(
                new FileInputStream(localFile));
        ProgressInputStream progressInput = new ProgressInputStream(buffIn,
                listener, localFile);
        flag = ftpClient.storeFile(localFile.getName(), progressInput);
        buffIn.close();

        return flag;
    }

    /**
     * 上传前对FTPClient配置
     * @param hostName
     * ftp地址
     * @param serverPort
     * 端口号
     * @param userName
     * 用户名
     * @param password
     * 密码
     * @param remotePath
     * ftp路径
     * @param listener
     * 上传监听
     * */
    private void uploadBeforeOperate(String hostName,String serverPort,String userName,String password,String remotePath,
                                     UploadProgressListener listener) throws IOException {
        try {
            this.openConnect(hostName,serverPort,userName,password);
            listener.onUploadProgress(FTP_CONNECT_SUCCESSS, 0,
                    null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onUploadProgress(FTP_CONNECT_FAIL, 0, null);
            return;
        }

        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        ftpClient.makeDirectory(remotePath);
        ftpClient.changeWorkingDirectory(remotePath);
    }

    /**
     * 上传后关闭资源
     * @param listener
     * 上传监听
     * */
    private void uploadAfterOperate(UploadProgressListener listener)
            throws IOException {
        this.closeConnect();
        listener.onUploadProgress(FTP_DISCONNECT_SUCCESS, 0, null);
    }

    /**
     * 从ftp下载图片到本地
     * @param hostName
     * ftp地址
     * @param serverPort
     * 端口号
     * @param userName
     * 用户名
     * @param password
     * 密码
     * @param serverPath
     * ftp路径
     * @param localPath
     * 本地路径
     * @param fileName
     * 文件名称
     * @param listener
     * 下载监听
     * */
    public void downloadBitmap(String hostName,String serverPort,String userName,String password,String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
            throws Exception {
        try {
            this.openConnect(hostName,serverPort,userName,password);
            listener.onDownLoadProgress(FTP_CONNECT_SUCCESSS, 0, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(FTP_CONNECT_FAIL, 0, null);
            return;
        }

        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            listener.onDownLoadProgress(FTP_FILE_NOTEXISTS, 0, null);
            return;
        }

        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }

        localPath = localPath + fileName;
        long serverSize = files[0].getSize();
        File localFile = new File(localPath);
        long localSize = 0;
        if (localFile.exists()) {
            localFile.delete();
        }
        OutputStream out = new FileOutputStream(localFile, true);
        ftpClient.setRestartOffset(localSize);
        InputStream input = ftpClient.retrieveFileStream(serverPath);
        byte[] b = new byte[256];
        int length = 0;
        while ((length = input.read(b)) != -1) {
            out.write(b, 0, length);
        }
        out.flush();
        out.close();
        input.close();

        if (ftpClient.completePendingCommand()) {
            listener.onDownLoadProgress(FTP_DOWN_SUCCESS, 0, new File(localPath));
        } else {
            listener.onDownLoadProgress(FTP_DOWN_FAIL, 0, null);
        }

        this.closeConnect();
        listener.onDownLoadProgress(FTP_DISCONNECT_SUCCESS, 0, null);

        return;
    }

    // Open the FTP connection and configure FTP
    /**
     *  开启FTP连接并配置FTP
     *  @param hostName
     *  ftp地址
     *  @param serverPort
     *  端口号
     *  @param userName
     *  用户名
     *  @param password
     *  密码
     * */

    public void openConnect(String hostName,String serverPort,String userName,String password) throws IOException {

        ftpClient.setControlEncoding("UTF-8");
        int reply;
        ftpClient.connect(hostName, Integer.parseInt(serverPort));

        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        ftpClient.login(userName, password);
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(256);
            ftpClient
                    .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }

    // Close the FTP link
    /**
     * 关闭FTP链接
     * */

    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    /**
     * 上传监听接口
     * */
    public interface UploadProgressListener {
        public void onUploadProgress(String currentStep, long uploadSize, File file);
    }

    /**
     * 下载监听接口
     * */
    public interface DownLoadProgressListener {
        public void onDownLoadProgress(String currentStep, long downProcess, File file);
    }

}
