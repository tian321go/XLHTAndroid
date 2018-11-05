package com.axeac.android.sdk.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.tools.StringUtil;
import com.axeac.android.sdk.ui.base.LabelComponent;
import com.axeac.android.sdk.utils.CommonUtil;
/**
 * describe:GPS positioning controls
 * GPS定位控件
 * @author axeac
 * @version 1.0.0
 * */
public class GPS extends LabelComponent {

    /**
     * 位置更新之间的最短时间间隔
     * */
    private static final int MINTIME = 2 * 1000;

    /**
     * 正在定位文本
     * */
    private String LOC_LOADING;
    /**
     * 定位未知地理文本
     * */
    private String NONE_CITY_INFO;

    private RelativeLayout valLayout;
    private EditText textField;

    /**
     * 定位信息文本
     * */
    private String text = "";
    /**
     * 经度
     * */
    private volatile double x = -1;
    /**
     * 纬度
     * */
    private volatile double y = -1;
    /**
     * 精度范围
     * <br>默认值为1500米
     * */
    private int position = 1500;
    /**
     * 未获取到经纬度，即Form提交时，是否忽略。默认为true，即可忽略
     * */
    private boolean ignore = true;

    /**
     * 定位是否可见
     * */
    private boolean visiable = true;

    /**
     * 定位位置数据
     * */
    private volatile String location;

    public GPS(Activity ctx) {
        super(ctx);
        this.returnable = true;
        valLayout = (RelativeLayout) LayoutInflater.from(ctx).inflate(
                R.layout.axeac_label_text, null);
        textField = (EditText) valLayout.findViewById(R.id.label_text_single);
        this.view = valLayout;
        LOC_LOADING = ctx.getString(R.string.axeac_gps_loading);
        NONE_CITY_INFO = ctx.getString(R.string.axeac_gps_unknow);
        location = NONE_CITY_INFO;
    }

    /**
     * 获取定位位置数据
     * @return
     * 定位位置数据
     * */
    public String getLocation() {
        return location;
    }

    /**
     * 设置定位信息文本
     * @param text
     * 定位信息文本
     * */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 设置经度
     * @param x
     * 经度
     * */
    public void setX(String x) {
        this.x = Float.parseFloat(x);
    }

    /**
     * 设置纬度
     * @param y
     * 纬度
     * */
    public void setY(String y) {
        this.y = Float.parseFloat(y);
    }

    /**
     * 设置定位位置数据
     * @param position
     * 定位位置数据
     * */
    public void setPosition(String position) {
        this.position = Integer.parseInt(position);
    }

    /**
     * 设置未获取到经纬度，即Form提交时，是否忽略。
     * @param ignore
     * 可选值 true|false
     * */
    public void setIgnore(String ignore) {
        this.ignore = Boolean.parseBoolean(ignore);
    }

    /**
     * 设置定位是否可见
     * @param visiable
     * 可选值 true|false
     * */
    public void setVisiable(String visiable) {
        this.visiable = Boolean.parseBoolean(visiable);
    }

    /**
     * 打开手机GPS设置界面
     * */
    private void openGPSSettings() {
        LocationManager locMgr = (LocationManager) ctx
                .getSystemService(Context.LOCATION_SERVICE);
        if (!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(ctx, R.string.axeac_map_gps_disable, Toast.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                ctx.startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                intent.setAction(Settings.ACTION_SETTINGS);
                try {
                    ctx.startActivity(intent);
                } catch (Throwable e) {
                    Toast.makeText(ctx, R.string.axeac_map_gps_exp,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 定位
     * */
    public void obtainLocation() {
        openGPSSettings();
        LocationManager gpsLocMgr;
        LocationManager networkLocMgr;
        try {
            gpsLocMgr = (LocationManager) ctx
                    .getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            gpsLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINTIME, position, locationListener);
            networkLocMgr = (LocationManager) ctx
                    .getSystemService(Context.LOCATION_SERVICE);
            networkLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            networkLocMgr.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MINTIME, position,
                    locationListener);
        } catch (Exception e) {
            gpsLocMgr = (LocationManager) ctx
                    .getSystemService(Context.LOCATION_SERVICE);
            gpsLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINTIME, position, locationListener);
            networkLocMgr = (LocationManager) ctx
                    .getSystemService(Context.LOCATION_SERVICE);
            networkLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            networkLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MINTIME, position, locationListener);
        } finally {
            textField.setText(LOC_LOADING);
        }
    }

    /**
     * 重新定位
     * @param location
     * 定位地址
     * */
    private void updateToNewLocation(Location location) {
        if (location != null) {
            Message msg = new Message();
            msg.what = 1001;
            msg.obj = location;
            mHandler.sendMessage(msg);
        } else {
            textField.setText(text);
        }
    }

    /**
     * 定位监听
     * */
    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1001:
                    obtainLoctionByLatLng((Location) msg.obj);
                    break;
                case 1002:
                    textField.setText(msg.obj.toString());
                    break;
                case 1003:
                    Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 根据经纬度获得定位
     * */
    private void obtainLoctionByLatLng(final Location loc) {
        new Thread() {
            public void run() {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();

                try {
                    String url = "http://ditu.google.cn/maps/api/geocode/json?latlng="
                            + lat + "," + lng + "&sensor=true";
                    HttpResponse response = new DefaultHttpClient()
                            .execute(new HttpPost(url));
                    HttpEntity entity = response.getEntity();
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity.getContent()));
                    String responseData = "";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        responseData += line;
                    }
                    location = analysisJSON(responseData, lat, lng);
                } catch (Throwable e) {
                    Message msg = new Message();
                    msg.what = 1003;
                    msg.obj = ctx.getString(R.string.axeac_gps_exp_used);
                    mHandler.sendMessage(msg);
                    return;
                }
                DecimalFormat format = new DecimalFormat("0.000000");
                double latitude = Double.parseDouble(format.format(lat));
                double longitude = Double.parseDouble(format.format(lng));
                x = latitude;
                y = longitude;
                Message msg = new Message();
                msg.what = 1002;
                msg.obj = location;
                mHandler.sendMessage(msg);
            }

            ;
        }.start();
    }

    /**
     * 返回定位文本信息
     * @param responseData
     * 请求返回的数据
     * @param lat
     * 纬度
     * @param lng
     * 经度
     * */
    private String analysisJSON(String responseData, double lat, double lng) {
        String location = NONE_CITY_INFO;
        try {
            JSONObject dataJSON = new JSONObject(responseData);
            Log.i("dataJSON","======"+dataJSON.toString());
            String status = dataJSON.getString("status");
            if (status.equals("ZERO_RESULTS")) {
                return location;
            }
            JSONArray results = dataJSON.getJSONArray("results");
            JSONObject tempComponent = results.optJSONObject(0);
            JSONArray address = tempComponent
                    .getJSONArray("address_components");
            String city = "", route = "", streetNumber = "";
            for (int i = 0; i < address.length(); i++) {
                JSONObject tempObject = address.getJSONObject(i);
                String types = tempObject.getString("types");
                String[] typesArr = types.split(",");
                if (typesArr[0].equals("[\"locality\"")) {
                    city = tempObject.getString("long_name");
                }
                if (typesArr[0].equals("[\"route\"]")) {
                    route = tempObject.getString("long_name");
                }
                if (typesArr[0].equals("[\"street_number\"]")) {
                    streetNumber = tempObject.getString("long_name");
                }
            }
            location = lat + "," + lng + "[" + city + route + streetNumber + "]";
        } catch (Throwable e) {
            Message msg = new Message();
            msg.what = 1003;
            msg.obj = ctx.getString(R.string.axeac_gps_exp_jiexi);
            mHandler.sendMessage(msg);
        }
        return location;
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        if (!readOnly) {
            obtainLocation();
        }
        try {
            String familyName = null;
            int style = Typeface.NORMAL;
            if (this.font != null && !"".equals(this.font)) {
                if (this.font.indexOf(";") != -1) {
                    String[] strs = this.font.split(";");
                    for (String str : strs) {
                        if (str.startsWith("size")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            String s = str.substring(index + 1).trim();
                            textField.setTextSize(Float.parseFloat(s.replace(
                                    "px", "").trim()));
                        } else if (str.startsWith("family")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            familyName = str.substring(index + 1).trim();
                        } else if (str.startsWith("style")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            String s = str.substring(index + 1).trim();
                            if ("bold".equals(s)) {
                                style = Typeface.BOLD;
                            } else if ("italic".equals(s)) {
                                style = Typeface.ITALIC;
                            } else {
                                if (s.indexOf(",") != -1) {
                                    if ("bold".equals(s.split(",")[0])
                                            && "italic".equals(s.split(",")[1])) {
                                        style = Typeface.BOLD_ITALIC;
                                    }
                                    if ("bold".equals(s.split(",")[1])
                                            && "italic".equals(s.split(",")[0])) {
                                        style = Typeface.BOLD_ITALIC;
                                    }
                                }
                            }
                        } else if (str.startsWith("color")) {
                            int index = str.indexOf(":");
                            if (index == -1)
                                continue;
                            String s = str.substring(index + 1).trim();
                            if (CommonUtil.validRGBColor(s)) {
                                int r = Integer.parseInt(s.substring(0, 3));
                                int g = Integer.parseInt(s.substring(3, 6));
                                int b = Integer.parseInt(s.substring(6, 9));
                                textField.setTextColor(Color.rgb(r, g, b));
                            }
                        }
                    }
                }
            }
            if (familyName == null || "".equals(familyName)) {
                textField.setTypeface(Typeface.defaultFromStyle(style));
            } else {
                textField.setTypeface(Typeface.create(familyName, style));
            }
            textField.setEnabled(false);
            int r = Integer.parseInt(bgColor.substring(0, 3));
            int g = Integer.parseInt(bgColor.substring(3, 6));
            int b = Integer.parseInt(bgColor.substring(6, 9));
            textField.setBackgroundColor(Color.rgb(r, g, b));
            if (text.equals("")) {
                text = x + "," + y + " [" + NONE_CITY_INFO + "]";
            }
            textField.setText(text);
        } catch (Throwable e) {
            String clsName = this.getClass().getName();
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
            String info = ctx.getString(R.string.axeac_toast_exp_create);
            info = StringUtil.replace(info, "@@TT@@", clsName);
            Toast.makeText(ctx, info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View getView() {
        if (visiable) {
            return super.getView();
        } else {
            return null;
        }
    }

    /**
     * 获得经度
     * @return
     * 经度
     * */
    public double getX() {
        return x;
    }

    /**
     * 获得纬度
     * @return
     * 纬度
     * */
    public double getY() {
        return y;
    }

    /**
     * 返回定位信息
     * */
    @Override
    public String getValue() {
        if (this.x == -1) {
            return null;
        }
        return this.location;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void starting() {

    }

    @Override
    public void end() {

    }
}