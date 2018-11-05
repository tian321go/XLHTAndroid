package com.axeac.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.axeac.android.sdk.activity.MainActivity1;
import com.axeac.android.sdk.fragment.MainFragment1;
import com.axeac.android.sdk.fragment.MainFragment2;
import com.axeac.android.sdk.jhsp.JHSPResponse;
import com.axeac.android.sdk.retrofit.DataRetrofit;
import com.axeac.android.sdk.retrofit.OnRequestCallBack;
import com.axeac.android.sdk.retrofit.UIHelper;
import com.axeac.android.sdk.tools.Property;
import com.axeac.android.sdk.utils.CommonUtil;
import com.axeac.android.sdk.utils.DeviceMessage;
import com.axeac.android.sdk.utils.NetInfo;
import com.axeac.android.sdk.utils.OrmHelper;
import com.axeac.android.sdk.utils.StaticObject;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class KhinfSDK {

    private static KhinfSDK khinfSDK;

    public static KhinfSDK getInstance() {
        return khinfSDK;
    }

    /**
     * 初始化
     * */
    public static void init(Context context) {
        khinfSDK = new KhinfSDK();
        StaticObject.initStaticObject(context);
        // Initialize device information
        //初始化设备信息
        DeviceMessage.init(context.getApplicationContext());
        if(StaticObject.read.getBoolean(StaticObject.SERVER_IS_DEMO,true)) {
            if (!StaticObject.read.getBoolean(StaticObject.LOADDEFAULTINFO, false)) {
                getDefaultInfo(context);
            } else {
                if (StaticObject.read.getString(StaticObject.SYSTEMLANGUAGE, "").equals("zh") && !Locale.getDefault().getLanguage().equals("zh")) {

                    deleteIp("http://10.96.6.12:8087/HttpServer",context);
                    getDefaultInfo(context);
                }else  if (!StaticObject.read.getString(StaticObject.SYSTEMLANGUAGE, "").equals("zh") && Locale.getDefault().getLanguage().equals("zh")){

                    deleteIp("http://10.96.6.12:8087/HttpServer",context);
                    getDefaultInfo(context);
                }
            }
        }
        // Force the version update feature to be enabled
        //强制打开版本更新功能
        StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, true).commit();
    }


    /**
     * 设置网络地址
     * @param ip
     * server address
     * <br>服务器地址
     * @param port
     * port number
     * <br>端口号
     * @param serverName
     * server name
     * <br>服务名称
     * @param isHttps
     * Whether it is https
     * <br>是否https
     */
    public static void setUrl(String ip, String port, String serverName, boolean isHttps) {
        String url;
        if ("".equals(port)) {
            if (isHttps) {
                url = "https://" + ip + "/" + serverName;
            } else {
                url = "http://" + ip + "/" + serverName;
            }
        } else {
            if (isHttps) {
                url = "https://" + ip + ":" + port + "/" + serverName;
            } else {
                url = "http://" + ip + ":" + port + "/" + serverName;
            }
        }
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL, url).commit();
        StaticObject.wirte.edit().putBoolean(StaticObject.SERVERURL_IS_HTTPS, isHttps).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_IP, ip).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_HTTP_PORT, port).commit();
        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_SERVERNAME, serverName).commit();
        DataRetrofit.isNeedRefresh = true;
    }

    /**
     * 返回Property对象
     * @return
     * Property对象
     * */
    public Property getProperty() {
        if (property == null) {
            property = new Property();
        }
        return property;
    }

    /**
     * 设置Property对象
     * @param property
     * Property对象
     * */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Property对象
     * */
    private Property property;

    // describe:Get the custom home page fragment
    /**
     * 获取自定义首页fragment
     * @param property
     * Property对象
     * @return
     * MainFragment1对象
     */
    public MainFragment1 getCustomMainFrament(Property property) {
        setProperty(property);
        return new MainFragment1();
    }

    // describe：Get the custom home page fragment class
    /**
     * 获取自定义首页fragment class
     * @param property
     * Property对象
     * @return
     * MainFragment1类
     */
    public Class<?> getCustomMainFramentClass(Property property) {
        setProperty(property);
        return MainFragment1.class;
    }

    // describe:Get the navigation fragment
    /**
     * 获取导航fragment
     *
     * @param property
     * Property对象
     * @return
     * MainFragment2对象
     */
    public MainFragment2 getNavFrament(Property property) {
        setProperty(property);
        return new MainFragment2();
    }

    // describe:Get the navigation fragment class
    /**
     * 获取导航fragment class
     * @param property
     * Property对象
     * @return
     * MainFragment2类
     */
    public Class<?> getNavFramentClass(Property property) {
        setProperty(property);
        return MainFragment2.class;
    }

    // describe:Read init.properties default server information
    /**
     * 读取init.properties默认服务器信息
     * @param context
     * Context对象
     */
    public static void getDefaultInfo(Context context) {

        Property defaultinfo = new Property();
        try {
            defaultinfo.load(context.getResources().getAssets().open("init.properties"));
        } catch (IOException e) {
            return;
        }
        String user = defaultinfo.getProperty("username", "");
        String pwd = defaultinfo.getProperty("password", "");
        boolean userthirdvalidate = defaultinfo.getBoolean("userthirdvalidate");
        boolean usecert = defaultinfo.getBoolean("usecert");
        boolean checknewversion = defaultinfo.getBoolean("checknewversion");
        boolean backgroundmsg = defaultinfo.getBoolean("backgroundmsg");
        StaticObject.wirte.edit().putLong(StaticObject.SYSTEMSETUPS_MSGTIMES,600000);
        if (userthirdvalidate) {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, true).commit();
        } else {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_THIRDVERIFY, false).commit();
        }
        if (usecert) {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_USECERT, true).commit();
        } else {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_USECERT, false).commit();
        }
        if (checknewversion) {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, true).commit();
        } else {
            StaticObject.wirte.edit().putBoolean(StaticObject.SYSTEMSETUPS_CHECKNEWVERSION, false).commit();
        }
        if (StaticObject.read.getBoolean(StaticObject.SERVER_IS_DEMO,true)){
            StaticObject.wirte.edit().putBoolean(StaticObject.SERVER_IS_DEMO, true).commit();
        }else{
            StaticObject.wirte.edit().putBoolean(StaticObject.SERVER_IS_DEMO, false).commit();
        }
        if (Locale.getDefault().getLanguage().equals("zh")){
            StaticObject.wirte.edit().putString(StaticObject.SYSTEMLANGUAGE,"zh").commit();
        }else{
            StaticObject.wirte.edit().putString(StaticObject.SYSTEMLANGUAGE,"en").commit();
        }
        if(user!=null&&!"".equals(user))
            StaticObject.wirte.edit().putString(StaticObject.USERNAME, user).commit();
        StaticObject.wirte.edit().putBoolean(StaticObject.ISSAVEPWD, true).commit();


        if (!"".equals(defaultinfo.getProperty("serverlist", ""))) {

            for (String servername : defaultinfo.getProperty("serverlist").split(",")) {
                String httpip = defaultinfo.getProperty(servername + ".httpip", "");
                String httpport = defaultinfo.getProperty(servername + ".httpport", "");
                String httpname = defaultinfo.getProperty(servername + ".httpname", "");
                String httpdesc = defaultinfo.getProperty(servername + ".httpdesc", "");
                String vpnip = defaultinfo.getProperty(servername + ".vpnip", "");
                String vpnport = defaultinfo.getProperty(servername + ".vpnport", "");
                String isHttps = defaultinfo.getProperty(servername + ".isHttps", "false");

//                if(!Locale.getDefault().getLanguage().equals("zh")){
//                    if (!httpdesc.equals("Demo")){
//                        continue;
//                    }
//                }else{
//                    if (!httpdesc.equals("演示系统")){
//                        continue;
//                    }
//                }
                if (!"".equals(httpip) && !"".equals(httpport) && !"".equals(httpdesc)) {
                    NetInfo netinfo = new NetInfo();
                    boolean ifHttps = isHttps.equals("true");
                    String httpurl = (ifHttps ? "https://" : "http://") + httpip + ":" + httpport + "/" + StaticObject.HTTPSERVER;
                    netinfo.serverdesc = httpdesc;
                    netinfo.serverurl = httpurl;
                    netinfo.servername = httpname;
                    netinfo.serverip = httpip;
                    netinfo.httpport = httpport;
                    netinfo.vpnip = vpnip;
                    netinfo.vpnport = vpnport;
                    netinfo.serverishttps = isHttps;
                    OrmHelper.getLiteOrm(context).save(netinfo);
                    String defaultServerDesc = StaticObject.read.getString(StaticObject.SERVERDESC, "");
                    String defaultServerUrl = StaticObject.read.getString(StaticObject.SERVERURL, "");
                    if ("".equals(defaultServerDesc) || "".equals(defaultServerUrl)) {
                        StaticObject.wirte.edit().putString(StaticObject.SERVERDESC, httpdesc).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL, httpurl).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_IP, httpip).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_SERVERNAME, httpname).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_HTTP_PORT, httpport).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_IP, vpnip).commit();
                        StaticObject.wirte.edit().putString(StaticObject.SERVERURL_VPN_PORT, vpnport).commit();
                        StaticObject.wirte.edit().putBoolean(StaticObject.SERVERURL_IS_HTTPS, isHttps.equals("true")).commit();
                    }
                }
            }
        }
        StaticObject.wirte.edit().putBoolean(StaticObject.LOADDEFAULTINFO, true).commit();
    }

    /**
     * 删除数据表中服务器地址
     * */
    public static void deleteIp(String serverUrl, Context context){
        List<NetInfo> urllist = OrmHelper.getLiteOrm(context).query(new QueryBuilder<>(NetInfo.class)
                .where("serverurl = ? ", new Object[]{serverUrl}));
        if (urllist != null && urllist.size() > 0) {
            for (NetInfo info : urllist) {
                OrmHelper.getLiteOrm(context).delete(info);
            }
        }
    }
    // describe:Test for sdk
    /**
     * 测试Sdk用
     * @param context
     * Context对象
     * @param username
     * user's name
     * <br>用户名
     *
     * @param password
     * password
     * <br>密码
     */
    public void login(final Context context, final String username, String password) {
        UIHelper.init(username, password);
        UIHelper.sendRequest((Activity) context, username, password, "MEIP_LOGIN=MEIP_LOGIN", new OnRequestCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onSuccesed(JHSPResponse response) {
                if (response.getCode() == 0) {
                    if (!CommonUtil.isResponseNoToast(response.getMessage())) {
                        Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (DeviceMessage.getParams().get("MEIP_CURRENT_USER") == null || DeviceMessage.getParams().get("MEIP_CURRENT_USER").equals("")) {
                        DeviceMessage.getParams().put("MEIP_CURRENT_USER", "demo");
                    }
                    Intent intent = new Intent(context, MainActivity1.class);
                    intent.putExtra("parms", response.getData());
                    StaticObject.loginFlag = true;
                    StaticObject.wirte.edit().putString(StaticObject.CUR_USERNAME, username).commit();

                    context.startActivity(intent);

                } else if (response.getCode() == 502) {

                } else {
                    StaticObject.loginFlag = false;
                    DeviceMessage.getParams().clear();
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onfailed(Throwable e) {
            }
        });

    }


}
