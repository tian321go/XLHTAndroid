package com.axeac.android.sdk.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 获取手机信息
 * @author axeac
 * @version 1.0.0
 * */
public class DeviceMessage {

	private static Map<String, String> params = new HashMap<String, String>();

	private static String jhsp;

	public static void init(Context ctx) {
		if (jhsp == null)
			jhsp = toString(ctx);
	}


	public static String getJHSP() {
		String param = "";
		for (String key : params.keySet())
			param += key + "=" + params.get(key) + "\r\n";
		return jhsp + param;
	}

	// Client ID

	/**
	 * 客户端ID
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceId(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return "";
		}
		return mTelephonyMgr.getDeviceId() == null ? "" : mTelephonyMgr.getDeviceId();
	}

	// Mobile phone/Tablet/PC

	/**
	 * 手机、平板、PC
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceClass(Context ctx) {
		boolean isTablet = false;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				Configuration con = ctx.getResources().getConfiguration();
				Method mIsLayoutSizeAtLeast = con.getClass().getMethod("isLayoutSizeAtLeast", int.class);
				Boolean flag = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
				isTablet = flag;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (isTablet) {
			return "Tablet";
		}
		return "Phone";
	}

	// phone number

	/**
	 * 手机号
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDevicePhone(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return "";
		}
		return mTelephonyMgr.getLine1Number() == null ? "" : mTelephonyMgr.getLine1Number();
	}

	// Device SIM card number

	/**
	 * 设备SIM卡号
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceIMSI(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return "";
		}
		return mTelephonyMgr.getSimSerialNumber() == null ? "" : mTelephonyMgr.getSimSerialNumber();
	}

	// Device terminal number

	/**
	 * 设备终端号
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceIMEI(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return "";
		}
		return mTelephonyMgr.getDeviceId() == null ? "" : mTelephonyMgr.getDeviceId();
	}

	// Device Mac address
	/**
	 * 设备MAC地址
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceMac(Context ctx) {
		WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = mWifiManager.getConnectionInfo();
		return info.getMacAddress() == null ? "" : info.getMacAddress();
	}

	// Whether to support the phone function
	/**
	 * 是否支持手机功能
	 * @param ctx
	 * Context对象
	 * */
	private static boolean obtainDeviceSimSupport(Context ctx) {
		return false;
	}

	// Whether the SIM card is normally inserted
	/**
	 * SIM卡是否正常插入
	 * */
	private static boolean obtainDeviceSimReadly(Context ctx) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getSimState() == TelephonyManager.SIM_STATE_READY;
	}

	// Operating system version
	/**
	 * 操作系统版本
	 * */
	private static String obtainDeviceOsver() {
		return android.os.Build.VERSION.RELEASE;
	}

	// Build manufacturers
	/**
	 * 生产厂家
	 * */
	private static String obtainDeviceBuilder() {
		return /*android.os.Build.HARDWARE*/"China";
	}

	// model
	/**
	 * 型号
	 * */
	private static String obtainDeviceModel() {
		return android.os.Build.MODEL;
	}

	// Time zone
	/**
	 * 时区
	 * */
	private static String obtainDeviceTimezone() {
		String timezone = "";
		int tz = TimeZone.getDefault().getRawOffset() / 1000;
		if (tz >= 0)
			timezone += '+';
		else
			timezone += '-';
		tz = Math.abs(tz);
		timezone += tz / 36000;
		timezone += tz / 3600 % 10;
		timezone += tz % 3600 / 600;
		timezone += tz % 3600 / 60 % 10;
		return timezone;
	}

	// Time zone deviation
	/**
	 * 时区偏差
	 * */
	private static int obtainDeviceOffset() {
		return TimeZone.getDefault().getRawOffset() / 1000;
	}

	// screen size
	/**
	 * 屏幕尺寸
	 * @param ctx
	 * Context对象
	 * */
	private static double obtainDeviceScreenSize(Context ctx) {
		DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		int densityDpi = metric.densityDpi;
		double diagonalPixels = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		double screenSize = diagonalPixels / densityDpi;
		DecimalFormat format = new DecimalFormat("0.0");
		screenSize = Double.parseDouble(format.format(screenSize));
		StaticObject.screenSize = screenSize;
		return screenSize;
	}

	// screen width
	/**
	 * 屏幕宽度
	 * @param ctx
	 * Context对象
	 * */
	public static int obtainDeviceScreenWidth(Context ctx) {
		DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
		int deviceWidth = metric.widthPixels;
		StaticObject.deviceWidth = deviceWidth;
		return deviceWidth;
	}

	// screen height
	/**
	 * 屏幕高度
	 * @param ctx
	 * Context对象
	 * */
	private static int obtainDeviceScreenHeight(Context ctx) {
		DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
		int deviceHeight = metric.heightPixels;
		StaticObject.deviceHeight = deviceHeight;
		return deviceHeight;
	}

	// Screen quality, HD, N, SD
	/**
	 * 屏幕质量，HD、N、SD
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceScreenQulity(Context ctx) {
		String screenQulity = "N";
		DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
		int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		if (densityDpi == 120) {
			screenQulity = "SD";
		} else if (densityDpi == 160) {
			screenQulity = "N";
		} else if (densityDpi == 240) {
			screenQulity = "HD";
		} else {
			screenQulity = "N";
		}
		return screenQulity;
	}

	// longitude
	/**
	 * 经度
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceGPSX(Context ctx) {
		return "0";
	}

	// latitude
	/**
	 * 纬度
	 * @param ctx
	 * Context对象
	 * */
	private static String obtainDeviceGPSY(Context ctx) {
		return "0";
	}

	// Apply the version number
	/**
	 * 应用版本号
	 * @param ctx
	 * Context对象
	 * */
	public static String obtainAppVersion(Context ctx) {
		String version = "";
		try {
			version = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 返回手机信息字符串
	 * @param ctx
	 * Context对象
	 * */
	public static String toString(Context ctx) {
		return jhsp = "DEVICE_ID=" + obtainDeviceId(ctx) + "\r\n"
				+ "DEVICE_TYPE=MA\r\n"
				+ "DEVICE_PHONE=" + obtainDevicePhone(ctx) + "\r\n"
				+ "DEVICE_IMSI=" + obtainDeviceIMSI(ctx) + "\r\n"
				+ "DEVICE_IMEI=" + obtainDeviceIMEI(ctx) + "\r\n"
				+ "DEVICE_MAC=" + obtainDeviceMac(ctx) + "\r\n"
				+ "DEVICE_OS=Android\r\n"
				+ "DEVICE_CLASS=" + obtainDeviceClass(ctx) + "\r\n"
				+ "DEVICE_SIM_SUPPORT=" + obtainDeviceSimSupport(ctx) + "\r\n"
				+ "DEVICE_SIM_READLY=" + obtainDeviceSimReadly(ctx) + "\r\n"
				+ "DEVICE_OSVER=" + obtainDeviceOsver() + "\r\n"
				+ "DEVICE_BUILDER=" + obtainDeviceBuilder() + "\r\n"
				+ "DEVICE_MODEL=" + obtainDeviceModel() + "\r\n"
				+ "DEVICE_TIMEZONE=GMT" + obtainDeviceTimezone() + "\r\n"
				+ "DEVICE_OFFSET=" + obtainDeviceOffset() + "\r\n"
				+ "DEVICE_SCREENSIZE=" + obtainDeviceScreenSize(ctx) + "\r\n"
				+ "DEVICE_SCREENWIDTH=" + obtainDeviceScreenWidth(ctx) + "\r\n"
				+ "DEVICE_SCREENHEIGHT=" + obtainDeviceScreenHeight(ctx) + "\r\n"
				+ "DEVICE_SCREENQULITY=" + obtainDeviceScreenQulity(ctx) + "\r\n"
				+ "DEVICE_GPS_X=" + obtainDeviceGPSX(ctx) + "\r\n"
				+ "DEVICE_GPS_Y=" + obtainDeviceGPSY(ctx) + "\r\n"
				+ "DEVICE_GPS_ENABLE=TRUE\r\n"
				+ "DEVICE_ADDR=CHINA\r\n"
				+ "DEVICE_LANGUAGE=CN\r\n"
				+ "DEVICE_APP_VERSION=" + obtainAppVersion(ctx) + "\r\n";
	}

	public static Map<String,String> getParams() {
		return params;
	}
}