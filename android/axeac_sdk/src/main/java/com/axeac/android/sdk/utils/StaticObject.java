package com.axeac.android.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.axeac.android.sdk.ui.base.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public class StaticObject {

    private static final String SYS_N_NAME = "SystemNoti";
    private static final int SYS_N_ID = 100;
    public static final String NOTIFYCATION_ACTION = "cn.axeacsoft.khmap5.notifycation";
    public static final String BUTTON_ACTION = "cn.axeacsoft.khmap5.buttonsend";
    public static final String INDEX_BUTTON = "cn.axeacsoft.khmap5.indexbutton";
    public static final String CLICK_ACTION = "cn.axeacsoft.khmap5.clicksend";
    public static final String MENU_CLICK_ACTION = "cn.axeacsoft.khmap5.menuclicksend";
    public static final String Fresh_CLICK_ACTION = "cn.axeacsoft.khmap5.freshclicksend";
    public static final String BTN_NO_ACTION = "BTN_NO_ACTION";
    public static final String BTN_OFF_ACTION = "BTN_OFF_ACTION";
    public static final String CHANGE_GRID_OR_LIST_ACTION = "cn.axeacsoft.khmap5.CHANGE_GRID_OR_LIST_ACTION";
    public static final String ACTION_ADD_NAV_MAP = "cn.axeacsoft.khmap5.ACTION_ADD_NAV_MAP";
    public static final String HTTPSERVER = "HttpServer";
    public static final String COMMONSERVER = "CommonServer";
    public static final String CUR_USERNAME = "CUR_USERNAME";
    public static final String USERNAME = "USERNAME";
    public static final String LOGINTIME = "LOGINTIME";
    public static final String PASSWORD = "PASSWORD";
    public static final String ISSAVEPWD = "ISSAVEPWD";
    public static final String SERVERDESC = "SERVERDESC";
    public static final String SERVERURL = "SERVERURL";
    public static final String SERVERURL_IP = "SERVERURL_IP";
    public static final String SERVERURL_SERVERNAME = "SERVERURL_SERVERNAME";
    public static final String SERVERURL_HTTP_PORT = "SERVERURL_HTTP_PORT";
    public static final String SERVERURL_VPN_PORT = "SERVERURL_VPN_PORT";
    public static final String SERVERURL_VPN_IP = "SERVERURL_VPN_IP";
    public static final String SERVERURL_IS_HTTPS = "SERVERURL_IS_HTTPS";
    public static final String SERVER_IS_DEMO = "SERVER_IS_DEMO";
    public static final String CURCERTID = "CURCERTID";
    public static final String COMMONCERTID = "axeacsoftkhmap5openuserscertid";
    public static final String NONECERTID = "NONE0000000000000000000000000000";

    public static final String MENUTYPE = "MENUTYPE";

    public static final String SYSTEMSETUPS_THIRDVERIFY = "SYSTEMSETUPS_THIRDVERIFY";
    public static final String SYSTEMSETUPS_USECERT = "SYSTEMSETUPS_USECERT";
    public static final String SYSTEMSETUPS_CHECKNEWVERSION = "SYSTEMSETUPS_CHECKNEWVERSION"; 
    public static final String SYSTEMSETUPS_MSGTIMES = "SYSTEMSETUPS_MSGTIMES";

    public static final String CURUSERSDATA = "CURUSERSDATA";

    public static final String CURRX = "CURRX";
    public static final String CURTX = "CURTX";
    public static final String ALLRXDATA = "ALLRXDATA";
    public static final String ALLTXDATA = "ALLTXDATA";

    public static final String MEIP_RETURN_NONE = "MEIP_RETURN_NONE";
    public static final String MEIP_RETURN_FORM = "MEIP_RETURN_FORM";

    public static final String LOADDEFAULTINFO = "LOADDEFAULTINFO";
    public static final String SYSTEMLANGUAGE = "SYSTEMLANGUAGE";

    public static final String TOKEN = "TOKEN";

    public static SharedPreferences read;
    public static SharedPreferences wirte;

    public static int deviceWidth;
    public static int deviceHeight;
    public static double screenSize;
    public static Bitmap img;

    public static Map<String, Component> ComponentMap = new LinkedHashMap<String, Component>();
    public static Map<String, Component> ReturnComponentMap = new LinkedHashMap<String, Component>();
    public static boolean loginFlag = false;
    public static boolean isGrid = false;
    public static boolean isGridSet = true;
    public static boolean ismenuclick = true;


    public static void initStaticObject(Context ctx) {
        StaticObject.read = ctx.getSharedPreferences(FileUtils.CONFIG_FILENAME, Context.MODE_PRIVATE);
        StaticObject.wirte = ctx.getSharedPreferences(FileUtils.CONFIG_FILENAME, Context.MODE_PRIVATE);
    }


    public static long[] readCurRXTX() {
        long[] curRXTX = new long[2];
        curRXTX[0] = StaticObject.read.getLong(CURRX, 0);
        curRXTX[1] = StaticObject.read.getLong(CURTX, 0);
        return curRXTX;
    }

    public static long[] readAllRXTX() {
        long[] allRXTX = new long[2];
        allRXTX[0] = StaticObject.read.getLong(ALLRXDATA, 0);
        allRXTX[1] = StaticObject.read.getLong(ALLTXDATA, 0);
        return allRXTX;
    }

    public static void clearCurRXTX() {
        StaticObject.wirte.edit().remove(CURRX).commit();
        StaticObject.wirte.edit().remove(CURTX).commit();
    }

    public static void clearAllRXTX() {
        StaticObject.wirte.edit().remove(ALLRXDATA).commit();
        StaticObject.wirte.edit().remove(ALLTXDATA).commit();
    }

    public static void updateRXTX(int rxLength, int txLength) {
        System.out.println("CurRXLength : " + rxLength);
        System.out.println("CurTXLength : " + txLength);
        long[] curRXTX = StaticObject.readCurRXTX();
        long[] allRXTX = StaticObject.readAllRXTX();
        curRXTX[0] += rxLength;
        curRXTX[1] += txLength;
        allRXTX[0] += rxLength;
        allRXTX[1] += txLength;
        StaticObject.wirte.edit().putLong(CURRX, curRXTX[0]).commit();
        StaticObject.wirte.edit().putLong(CURTX, curRXTX[1]).commit();
        StaticObject.wirte.edit().putLong(ALLRXDATA, allRXTX[0]).commit();
        StaticObject.wirte.edit().putLong(ALLTXDATA, allRXTX[1]).commit();
    }

    public static void updateRX(int rxLength) {
        System.out.println("CurRXLength : " + rxLength);
        long curRX = StaticObject.read.getLong(CURRX, 0);
        long allRX = StaticObject.read.getLong(ALLRXDATA, 0);
        curRX += rxLength * 2;
        allRX += rxLength * 2;
        StaticObject.wirte.edit().putLong(CURRX, curRX).commit();
        StaticObject.wirte.edit().putLong(ALLRXDATA, allRX).commit();
    }

    public static void updateTX(int txLength) {
        System.out.println("CurTXLength : " + txLength);
        long curTX = StaticObject.read.getLong(CURTX, 0);
        long allTX = StaticObject.read.getLong(ALLTXDATA, 0);
        curTX += txLength * 2;
        allTX += txLength * 2;
        StaticObject.wirte.edit().putLong(CURTX, curTX).commit();
        StaticObject.wirte.edit().putLong(ALLTXDATA, allTX).commit();
    }


    public static String getImageUrl(String icon) {
        if (icon == null) {
            return null;
        }
        icon = icon.trim();
        if (icon.equals("")) {
            return null;
        }
        String iconl = icon.toLowerCase();
        if (icon.length() > 7
                && (iconl.startsWith("http://") || (iconl.startsWith("https://")) || iconl.startsWith("ftp://"))) {
            // save picture
            // 保存图片
            return icon;
        } else if (iconl.startsWith("base64:")) {
            return icon;
        } else if (iconl.startsWith("res-img:")) {
            String url = StaticObject.read.getString(StaticObject.SERVERURL, "");
            String url1 = url.substring(0, url.lastIndexOf("/")) + "/ResourceServer?id=" + icon.substring(8);
            Log.d("URL", url1);
            return url1;
        } else return icon;
    }
}