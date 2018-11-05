package com.axeac.android.sdk.retrofit;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.UUID;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.jhsp.Certificate;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.tools.DateFunction;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.tools.RSA;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DeviceMessage;
import com.axeac.android.sdk.utils.FileUtils;
import com.axeac.android.sdk.utils.StaticObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * 数据请求类
 * @author axeac
 * @version 1.0.0
 */
public class UIHelper {

    /**
     * 登录账号
     * */
    public static String loginName;
    /**
     * 登录密码
     * */
    public static String loginPwd;

    /**
     * 初始化账号密码
     * @param mLoginName
     * 账号
     * @param mLoginPwd
     * 密码
     * */
    public static void init(String mLoginName, String mLoginPwd) {
        loginName = mLoginName.trim();
        loginPwd = mLoginPwd.trim();
    }

    public static void send(Activity activity, String data, OnRequestCallBack requestCallBack) {
        sendRequest(activity, loginName, loginPwd, data, requestCallBack);
    }

    // describe:request data
    /**
     * 请求数据
     * @param activity
     * Activity对象
     * @param user
     * 账号
     * @param pwd
     * 密码
     * @param data
     * 请求字段
     * @param requestCallBack
     * OnRequestCallBack对象
     * */
    public static void sendRequest(final Activity activity, String user, String pwd, String data, final OnRequestCallBack requestCallBack) {

        if (!CommonUtil.isNetworkConnected(activity)) {
            Toast.makeText(activity, R.string.axeac_response_notinternet, Toast.LENGTH_SHORT).show();
            return;
        }

        String jhsp = DeviceMessage.getJHSP();
        String uuid = UUID.randomUUID().toString();
        jhsp += "MEIP_USERID=" + user + "\r\n";
        jhsp += "MEIP_CURRENT_USER=" + user + "\r\n";
        boolean thirdverifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
        if (thirdverifyFlag) {
            jhsp += "MEIP_PASSWORD=" + pwd + "\r\n";
        } else {
            jhsp += "MEIP_PASSWORD=" + StringUtil.encodeMD5(pwd + user) + "\r\n";
        }
        jhsp += "MEIP_UUID=" + uuid + "\r\n";
        String date = DateFunction.format(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss.SSS");
        jhsp += "MEIP_SENDDATE=" + date + "\r\n";
        String token = StaticObject.read.getString(StaticObject.TOKEN,"");
        jhsp += "DEVICE_XINGE_TOKEN=" + token + "\r\n";
        jhsp += data;
        if (FileUtils.isStoreLogToSD && FileUtils.checkSDCard()) {
            File requestFolder = new File(FileUtils.getSDCardPath() + FileUtils.KHPATH + "/RequestFolder");
            if (!requestFolder.exists()) {
                requestFolder.mkdirs();
            }
            FileUtils.writeFile("START\r\n" + jhsp + "\r\nEND", requestFolder.getPath() + "/" + System.currentTimeMillis() + ".txt");
        }
        Certificate cert = Certificate.getCertficateForData(activity);
        byte[] byte1 = new byte[46];

        // JHSP head
        // JHSP 头
        byte1[0] = 'J';
        byte1[1] = 'H';
        byte1[2] = 'S';
        byte1[3] = 'P';
        // JHSP version
        // JHSP 版本
        byte1[4] = '2';
        byte1[5] = '0';
        if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, true))
            copyString(byte1, 6, 32, (cert.getCertId()));
        else
            copyString(byte1, 6, 32, (StaticObject.NONECERTID));
        // Parameter length
        // 参数长度
        int length = jhsp.length();
        String hex = Integer.toHexString(length);
        byte[] h = new byte[8];
        for (int i = 0; i < (8 - hex.length()); i++) {
            h[i] = '0';
        }
        copyString(h, 8 - hex.length(), hex.length(), hex);
        copyString(byte1, 38, h.length, new String(h));
        String s1 = StringUtil.toUTF(jhsp);
        final String re = new String(byte1) + s1;

        Log.e("request", re.replaceAll("\r", ""));

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), re);
        String [] serverName = StaticObject.read.getString(StaticObject.SERVERURL_SERVERNAME, "").split("/");
        int len = serverName.length;
        int host = Integer.parseInt(StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, ""));
        if(host>65535){
            Toast.makeText(activity, R.string.axeac_host_big, Toast.LENGTH_SHORT).show();
            return ;
        }
        Subscription s = DataRetrofit.getService(activity).request(serverName[len-1], requestBody)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallBack.onStart();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        requestCallBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleError(activity, e);
                        requestCallBack.onfailed(e);
                    }

                    @Override
                    public void onNext(Response result) {
                        try {
                            byte[] b = ((ResponseBody) result.body()).bytes();
                            Log.d("request_Result", new String(b).replaceAll("\r", ""));
                            StaticObject.updateRX(b.length);
                            requestCallBack.onSuccesed(print(activity, b));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    /**
     * 请求数据
     * @param activity
     * Activity对象
     * @param user
     * 账号
     * @param pwd
     * 密码
     * @param data
     * 请求字段
     * @param requestCallBack
     * OnRequestCallBack对象
     * */
    public static void sendRequestCom(final Activity activity, String user, String pwd, String data,
                                      final OnRequestCallBack requestCallBack) {
        if (!CommonUtil.isNetworkConnected(activity)) {
            Toast.makeText(activity, R.string.axeac_response_notinternet, Toast.LENGTH_SHORT).show();
            return;
        }
        String jhsp = DeviceMessage.getJHSP();
        String uuid = UUID.randomUUID().toString();
        jhsp += "MEIP_USERID=" + user + "\r\n";
        jhsp += "MEIP_CURRENT_USER=" + user + "\r\n";
        boolean thirdverifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
        if (thirdverifyFlag) {
            jhsp += "MEIP_PASSWORD=" + pwd + "\r\n";
        } else {
            jhsp += "MEIP_PASSWORD=" + StringUtil.encodeMD5(pwd + user) + "\r\n";
        }
        jhsp += "MEIP_UUID=" + uuid + "\r\n";
        String date = DateFunction.format(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss.SSS");
        jhsp += "MEIP_SENDDATE=" + date + "\r\n";
        jhsp += data;
        if (FileUtils.isStoreLogToSD && FileUtils.checkSDCard()) {
            File requestFolder = new File(FileUtils.getSDCardPath() + FileUtils.KHPATH + "/RequestFolder");
            if (!requestFolder.exists()) {
                requestFolder.mkdirs();
            }
            FileUtils.writeFile("START\r\n" + jhsp + "\r\nEND", requestFolder.getPath() + "/" + System.currentTimeMillis() + ".txt");
        }
        Certificate cert = Certificate.getCertficateForData(activity);
        byte[] byte1 = new byte[46];
        // JHSP head
        // JHSP 头
        byte1[0] = 'J';
        byte1[1] = 'H';
        byte1[2] = 'S';
        byte1[3] = 'P';
        // JHSP version
        // JHSP 版本
        byte1[4] = '2';
        byte1[5] = '0';
        if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, true))
            copyString(byte1, 6, 32, (cert.getCertId()));
        else
            copyString(byte1, 6, 32, (StaticObject.NONECERTID));
        // Parameter length
        // 参数长度
        int length = jhsp.length();
        String hex = Integer.toHexString(length);
        byte[] h = new byte[8];
        for (int i = 0; i < (8 - hex.length()); i++) {
            h[i] = '0';
        }
        copyString(h, 8 - hex.length(), hex.length(), hex);
        copyString(byte1, 38, h.length, new String(h));
        String s1 = StringUtil.toUTF(jhsp);
        final String re = new String(byte1) + s1;

        Log.e("request_com", re.replaceAll("\r", ""));

        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), re);
        Subscription s = DataRetrofit.getService(activity).requestCom(requestBody)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallBack.onStart();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        requestCallBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {

                        requestCallBack.onfailed(e);
                    }

                    @Override
                    public void onNext(Response result) {
                        try {
                            byte[] b = ((ResponseBody) result.body()).bytes();
                            Log.d("request_com_Result", new String(b).replaceAll("\r", ""));
                            StaticObject.updateRX(b.length);
                            requestCallBack.onSuccesed(print(activity, b));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    // describe:polling
    /**
     * 轮询
     * @param activity
     * Activity对象
     * @param requestCallBack
     * OnRequestCallBack对象
     */
    public static void sendRequestService(final Context activity,
                                          final OnRequestCallBack requestCallBack) {
        if (!CommonUtil.isNetworkConnected(activity)) {
            if (Looper.myLooper()==null)
            Looper.prepare();
            Toast.makeText(activity, R.string.axeac_response_notinternet, Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        String user = StaticObject.read.getString(StaticObject.USERNAME, "");
        String pwd = StaticObject.read.getString(StaticObject.PASSWORD, "");
        String jhsp = DeviceMessage.getJHSP();
        jhsp += "MEIP_CURRENT_USER=" + user + "\r\n";
        jhsp += "MEIP_USERID=" + user + "\r\n";
        boolean thirdverifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
        if (thirdverifyFlag) {
            jhsp += "MEIP_PASSWORD=" + pwd + "\r\n";
        } else {
            jhsp += "MEIP_PASSWORD=" + StringUtil.encodeMD5(pwd + user) + "\r\n";
        }
        String date = DateFunction.format(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss.SSS");
        jhsp += "MEIP_SENDDATE=" + date + "\r\n";
        jhsp += "MEIP_PAGE=task_count" + "\r\n";
        Certificate cert = Certificate.getCertficateForData(activity);
        byte[] byte1 = new byte[46];
        // JHSP head
        // JHSP 头
        byte1[0] = 'J';
        byte1[1] = 'H';
        byte1[2] = 'S';
        byte1[3] = 'P';
        // JHSP version
        // JHSP 版本
        byte1[4] = '2';
        byte1[5] = '0';
        if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, true))
            copyString(byte1, 6, 32, (cert.getCertId()));
        else
            copyString(byte1, 6, 32, (StaticObject.NONECERTID));
        // Parameter length
        // 参数长度
        int length = jhsp.length();
        String hex = Integer.toHexString(length);
        byte[] h = new byte[8];
        for (int i = 0; i < (8 - hex.length()); i++) {
            h[i] = '0';
        }
        copyString(h, 8 - hex.length(), hex.length(), hex);
        copyString(byte1, 38, h.length, new String(h));
        String s1 = StringUtil.toUTF(jhsp);
        final String re = new String(byte1) + s1;

        Log.e("request_service", re.replaceAll("\r", ""));

        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), re);
        String [] serverName = StaticObject.read.getString(StaticObject.SERVERURL_SERVERNAME, "").split("/");
        int len = serverName.length;
        Subscription s = DataRetrofit.getService(activity).request(serverName[len-1], requestBody)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallBack.onStart();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        requestCallBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestCallBack.onfailed(e);
                    }

                    @Override
                    public void onNext(Response result) {
                        try {
                            byte[] b = ((ResponseBody) result.body()).bytes();
                            Log.d("request_service_Result", new String(b).replaceAll("\r", ""));
                            StaticObject.updateRX(b.length);
                            requestCallBack.onSuccesed(print(activity, b));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    /**
     * msg轮询
     * @param activity
     * Activity对象
     * @param requestCallBack
     * OnRequestCallBack对象
     */
    public static void sendRequestMsgService(final Context activity,
                                          final OnRequestCallBack requestCallBack) {
        if (!CommonUtil.isNetworkConnected(activity)) {
            if (Looper.myLooper()==null)
            Looper.prepare();
            Toast.makeText(activity, R.string.axeac_response_notinternet, Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        String user = StaticObject.read.getString(StaticObject.USERNAME, "");
        String pwd = StaticObject.read.getString(StaticObject.PASSWORD, "");
        String jhsp = DeviceMessage.getJHSP();
        jhsp += "MEIP_CURRENT_USER=" + user + "\r\n";
        jhsp += "MEIP_USERID=" + user + "\r\n";
        boolean thirdverifyFlag = StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false);
        if (thirdverifyFlag) {
            jhsp += "MEIP_PASSWORD=" + pwd + "\r\n";
        } else {
            jhsp += "MEIP_PASSWORD=" + StringUtil.encodeMD5(pwd + user) + "\r\n";
        }
        String date = DateFunction.format(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss.SSS");
        jhsp += "MEIP_SENDDATE=" + date + "\r\n";
        jhsp += "MEIP_PAGE=msg_count" + "\r\n";
        Certificate cert = Certificate.getCertficateForData(activity);
        byte[] byte1 = new byte[46];
        // JHSP head
        // JHSP 头
        byte1[0] = 'J';
        byte1[1] = 'H';
        byte1[2] = 'S';
        byte1[3] = 'P';
        // JHSP version
        // JHSP 版本
        byte1[4] = '2';
        byte1[5] = '0';
        if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, true))
            copyString(byte1, 6, 32, (cert.getCertId()));
        else
            copyString(byte1, 6, 32, (StaticObject.NONECERTID));
        // Parameter length
        // 参数长度
        int length = jhsp.length();
        String hex = Integer.toHexString(length);
        byte[] h = new byte[8];
        for (int i = 0; i < (8 - hex.length()); i++) {
            h[i] = '0';
        }
        copyString(h, 8 - hex.length(), hex.length(), hex);
        copyString(byte1, 38, h.length, new String(h));
        String s1 = StringUtil.toUTF(jhsp);
        final String re = new String(byte1) + s1;

        Log.e("request_service", re.replaceAll("\r", ""));

        final RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), re);
        String [] serverName = StaticObject.read.getString(StaticObject.SERVERURL_SERVERNAME, "").split("/");
        int len = serverName.length;
        Subscription s = DataRetrofit.getService(activity).request(serverName[len-1], requestBody)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallBack.onStart();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {
                        requestCallBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        requestCallBack.onfailed(e);
                    }

                    @Override
                    public void onNext(Response result) {
                        try {
                            byte[] b = ((ResponseBody) result.body()).bytes();
                            Log.d("request_service_Result", new String(b).replaceAll("\r", ""));
                            StaticObject.updateRX(b.length);
                            requestCallBack.onSuccesed(print(activity, b));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }
    /**
     * 复制字符串
     * @param data
     * 要改变的byte数组
     * @param start
     * 起始指针位置
     * @param len
     * 复制长度
     * @param s
     * 要复制的字符串
     * */
    private static void copyString(byte[] data, int start, int len, String s) {
        int l = len;
        if (s.length() < len)
            l = s.length();
        byte[] d = s.getBytes();
        for (int i = 0; i < l; i++) {
            data[i + start] = d[i];
        }
        for (int i = s.length(); i < len; i++) {
            data[i + start] = ' ';
        }
    }

    /**
     * 打印服务器回传值
     * @param ctx
     * Context对象
     * @param data
     * 服务器回传值byte数组
     * */
    private static JHSPResponse print(Context ctx, byte[] data) {
        JHSPResponse response = new JHSPResponse();
        if (data[0] != 'J' || data[1] != 'H' || data[2] != 'S' || data[3] != 'P') {
            System.out.println("错误的协议头部信息!");
        }
        if (data[4] != '2' || data[5] != '0') {
            System.out.println("不支持的JHSP版本!");
        }
        String length = new String(CommonUtil.copyOfRange(data, 38, 46));
        int paramLength = Integer.parseInt(length, 16);
        byte[] content = new byte[0];
        if (StaticObject.read.getBoolean(StaticObject.SYSTEMSETUPS_USECERT, true)) {
            Certificate cert = Certificate.getCertficateForData(ctx);
            long times = System.currentTimeMillis();
            content = RSA.decode(CommonUtil.copyOfRange(data, 46, data.length),
                    cert.getPublicKey(), cert.getModulus()).getBytes();
            Log.w("TIMES", "RSA解密 ：" + (System.currentTimeMillis() - times) + "毫秒");
        } else
            content = CommonUtil.copyOfRange(data, 46, data.length);
        String newParam = null;
        try {
            newParam = StringUtil.toGB2312(new String(content, "gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (paramLength > newParam.length()) {
            Property p = new Property(newParam);
            response.setCode(p.getInt(JHSPResponse.MEIP_CODE, 0));
            response.setMessage(p.getProperty(JHSPResponse.MEIP_MESSAGE, ""));
            response.setSequence(p.getProperty(JHSPResponse.MEIP_SEQUENCE, ""));
            response.setTimes(p.getInt(JHSPResponse.MEIP_TIMES, 0));
            response.setUsername(p.getProperty(JHSPResponse.MEIP_USERNAME, ""));
            response.setData("");
            response.setReturnType(p.getProperty(JHSPResponse.MEIP_RETURN_TYPE, ""));
            response.setResourceVer(p.getInt(JHSPResponse.MEIP_RESOURCE_VER, 0));
            response.setForward(p.getProperty(JHSPResponse.MEIP_FORWARD, ""));
        } else {
            Property p = new Property(newParam.substring(0, paramLength));
            response.setCode(p.getInt(JHSPResponse.MEIP_CODE, 0));
            response.setMessage(p.getProperty(JHSPResponse.MEIP_MESSAGE, ""));
            response.setSequence(p.getProperty(JHSPResponse.MEIP_SEQUENCE, ""));
            response.setTimes(p.getInt(JHSPResponse.MEIP_TIMES, 0));
            response.setUsername(p.getProperty(JHSPResponse.MEIP_USERNAME, ""));
            response.setData(newParam.substring(paramLength, newParam.length()));
            response.setReturnType(p.getProperty(JHSPResponse.MEIP_RETURN_TYPE, ""));
            response.setResourceVer(p.getInt(JHSPResponse.MEIP_RESOURCE_VER, 0));
            response.setForward(p.getProperty(JHSPResponse.MEIP_FORWARD, ""));
        }
        return response;
    }

    /**
     * 提示错误信息
     * @param activity
     * Context对象
     * @param e
     * Throwable对象
     * */
    private static void handleError(Context activity, Throwable e) {
        e.printStackTrace();

        if (e instanceof ConnectException) {
            Toast.makeText(activity, R.string.axeac_response_connect_failure, Toast.LENGTH_SHORT).show();
        } else if (e instanceof SocketTimeoutException) {
            Toast.makeText(activity, R.string.axeac_response_connect_timeout, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, R.string.axeac_response_dealwith_failure, Toast.LENGTH_SHORT).show();
        }

    }

}
