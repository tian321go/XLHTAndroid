package com.axeac.android.sdk.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 普通工具类
 * @author axeac
 * @version 1.0.0
 * */
public class CommonUtil {

    /**
     * 判断IP地址是否正确
     * @param ip
     * IP地址
     * */
    public static boolean isIP(String ip) {
        Pattern pattern = Pattern
                .compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /**
     * 判断网络是否连接
     * @param ctx
     * Context对象
     * */
    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }


    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * 判断是否为数字类型
     * @param str
     * 字符串
     * */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 将字符串转换为float类型并返回true
     * @param str
     * 字符串
     * */
    public static boolean isFloat(String str) {
        boolean isFloat = true;
        try {
            Float.parseFloat(str);
        } catch (Exception e) {
            isFloat = false;
        }
        return isFloat;
    }

    /**
     * 是否是电话号码
     * @param str
     * 字符串
     * */
    public static boolean isTelNumber(String str) {
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断密码格式是否正确
     * @param pwd
     * 密码字符串
     * */
    public static boolean isPassWord(String pwd) {
        Pattern pattern = Pattern
                .compile("[0-9a-zA-Z]{6,}");
        return pattern.matcher(pwd).matches();
    }

    public static boolean getBoolean(String str) {
        if (str == null || str.trim().equals(""))
            return false;
        return str.trim().equals("1")
                || str.trim().toLowerCase().equals("true")
                || str.toLowerCase().equals("T");
    }

    /**
     * 根据颜色值或字符串获得颜色
     * @param color
     * 颜色值
     * */
    public static int getColor(String color) {
        if (color == null) {
            return Color.BLACK;
        }
        color = color.trim();
        if (color.length() == 0 || color.equals("0") || color.length() != 9)
            return Color.BLACK;
        try {
            int red = Integer.parseInt(color.substring(0, 3));
            int green = Integer.parseInt(color.substring(3, 6));
            int blue = Integer.parseInt(color.substring(6, 9));
            return Color.rgb(red, green, blue);
        } catch (Throwable e) {
            return Color.BLACK;
        }
    }

    /**
     * 判断字符串是否为颜色值
     * @param s
     * 字符串
     * */
    public static boolean validRGBColor(String s) {
        s = s.trim();
        if (s == null || s.trim().equals(""))
            return false;
        if (s.length() != 9) {
            return false;
        }
        int r = Integer.parseInt(s.substring(0, 3));
        int g = Integer.parseInt(s.substring(3, 6));
        int b = Integer.parseInt(s.substring(6, 9));
        if (r > 255 || g > 255 || b > 255) {
            return false;
        }
        return true;
    }

    public static Integer[] sortDesc(Integer[] pos) {
        int temp;
        int i;
        int j = 0;
        for (i = 1; i < pos.length; i++)
            for (j = 0; j < pos.length; j++) {
                if (pos[i] > pos[j]) {
                    temp = pos[i];
                    pos[i] = pos[j];
                    pos[j] = temp;
                }
            }
        return pos;
    }

    /**
     * Integer类型数组从小到大排序
     * */
    public static Integer[] sortAsc(Integer[] pos) {
        int temp;
        int i;
        int j = 0;
        for (i = 1; i < pos.length; i++)
            for (j = 0; j < pos.length; j++) {
                if (pos[i] < pos[j]) {
                    temp = pos[i];
                    pos[i] = pos[j];
                    pos[j] = temp;
                }
            }
        return pos;
    }

    public static int obtainMaxData(int maxData, int count) {
        int arg = count;
        if (maxData <= 100)
            arg = count;
        else if (maxData <= 1000)
            arg = count * 10;
        else if (maxData <= 10000)
            arg = count * 100;
        else if (maxData <= 100000)
            arg = count * 1000;
        else
            arg = count * 10000;
        return maxData % arg != 0 ? (maxData / arg + 1) * arg : maxData;
    }

    /**
     * 根据传入字符串判定是否弹吐司
     * @param msg
     * 字符串
     * */
    public static boolean isResponseNoToast(String msg) {
        if (null == msg || "".equals(msg) || "空".equals(msg)
                || "null".equals(msg) || "OK".equals(msg) || "操作成功".equals(msg)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * describe:Measure the view, and finally getMeasuredWidth () to get the width and height.
     *
     * 测量这个view，最后通过getMeasuredWidth()获取宽度和高度.
     *
     * @param v
     * The view to be measured
     * 要测量的view
     *
     * @return
     * Measured the view after
     * 测量过的view
     */
    public static void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }
}