package com.axeac.android.sdk.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;

import com.axeac.android.sdk.R;
import com.axeac.android.sdk.customview.MapContainer;
import com.axeac.android.sdk.dialog.CustomDialog;
import com.axeac.android.sdk.ui.base.Component;
import com.axeac.android.sdk.utils.AMapUtil;
import com.axeac.android.sdk.utils.SensorEventHelper;
import com.axeac.android.sdk.utils.ToastUtil;
import com.axeac.android.sdk.utils.overlay.DrivingRouteOverLay;
import com.axeac.android.sdk.utils.overlay.RideRouteOverlay;

/**
 * describe:Map controls
 * 地图控件
 * @author axeac
 * @version 1.0.0
 */
public class Map extends Component {

    private RelativeLayout valLayout;
    /**
     * MapView对象
     * */
    private MapView mMapView;
    /**
     * AMap对象
     * */
    private AMap aMap;
    /**
     * MarkerOptions对象
     * */
    private MarkerOptions markerOptions;
    /**
     * UiSettings对象
     * */
    private UiSettings uiSettings;
    /**
     * MyLocationStyle对象
     * */
    private MyLocationStyle myLocationStyle;
    /**
     * AMapLocationClient对象
     * */
    private AMapLocationClient mlocationClient;
    /**
     * AMapLocationClient对象
     * */
    private AMapLocationClientOption mLocationOption;
    /**
     * OnLocationChangedListener对象
     * */
    private LocationSource.OnLocationChangedListener mListener;

    /**
     * 存储坐标点描述文本的list集合
     * */
    public static List<String> desList;

    /**
     * 存储从服务器读取的坐标点的list集合
     * */
    private List<String[]> posList = new ArrayList<>();
    /**
     * 存储Marker坐标点的list集合
     * */
    private List<Marker> pointList = new ArrayList<>();
    /**
     * 存储搜索后坐标点Marker的list集合
     * */
    private List<Marker> resultList = new ArrayList<>();

    // Default to driving
    /**
     * 规划路线模式
     * <br>默认值为1 代表驾车模式
     * */
    private int type = 1;

    /**
     * 是否显示路线轨迹
     * 默认显示
     * */
    private String line = "true";
    /**
     * GeocodeSearch对象
     * */
    private GeocodeSearch geocoderSearch;
    /**
     * Marker对象
     * */
    private Marker geoMarker;
    /**
     * 搜索地址
     * */
    private String addressName;

    /**
     * 线段颜色
     * */
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);

    /**
     * 填充颜色
     * */
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    /**
     * Circle对象
     * */
    private Circle mCircle;
    /**
     * 定位是否存在标志
     * */
    private boolean mFirstFix = false;
    /**
     * Marker对象
     * */
    private Marker mLocMarker;
    /**
     * SensorEventHelper对象
     * */
    private SensorEventHelper mSensorHelper;
    /**
     * 坐标点名称，常量字段
     * */
    public static final String LOCATION_MARKER_FLAG = "mylocation";

    /**
     * 对话框ProgressDialog对象
     * */
    private ProgressDialog progDialog = null;

    /**
     * 行走路线规划结果
     * */
    private WalkRouteResult mWalkRouteResult;
    /**
     * 骑行路线规划结果
     * */
    private RideRouteResult mRideRouteResult;
    /**
     * 公交路线规划结果
     * */
    private BusRouteResult mBusRouteResult;
    /**
     * 驾车路线规划结果
     * */
    private DriveRouteResult mDriveRouteResult;
    /**
     * RouteSearch对象
     * */
    private RouteSearch mRouteSearch;
    /**
     * 公交路线类型
     * */
    private final int ROUTE_TYPE_BUS = 1;
    /**
     * 驾车路线类型
     * */
    private final int ROUTE_TYPE_DRIVE = 2;
    /**
     * 行走路线类型
     * */
    private final int ROUTE_TYPE_WALK = 3;
    /**
     * 骑行路线类型
     * */
    private final int ROUTE_TYPE_RIDE = 4;
    /**
     * 公交路线规划查询城市
     * */
    private String mCurrentCityName = "北京";
    /**
     * 存储LatLonPoint对象的list集合
     * */
    private List<LatLonPoint> mPointList = new ArrayList<>();
    /**
     * MapPointOverlay对象
     * */
    private MapPointOverlay mapPointOverlay;
    /***/
    private ScrollView scrollView;
    /**
     * MapContainer对象
     * */
    private MapContainer mapContainer;


    public Map(Activity ctx) {
        super(ctx);
        desList = new ArrayList<>();
        this.returnable = true;
        valLayout = (RelativeLayout) LayoutInflater.from(this.ctx).inflate(R.layout.axeac_map_view, null);
        mMapView = (MapView) valLayout.findViewById(R.id.map_view);
        scrollView = (ScrollView)ctx.findViewById(R.id.comp_layout);
        mapContainer = (MapContainer)valLayout.findViewById(R.id.map_container);
        mMapView.onCreate(new Bundle());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // screen height
        //屏幕高度
        int displayheight = displayMetrics.heightPixels;
        // screen width
        //屏幕宽度
        int displaywidth = displayMetrics.widthPixels;
        mMapView.setLayoutParams(new RelativeLayout.LayoutParams(displaywidth,displayheight));
        mapContainer.setScrollView(scrollView);
        aMap = mMapView.getMap();
        uiSettings = aMap.getUiSettings();
        mSensorHelper = new SensorEventHelper(ctx);
        mSensorHelper.registerSensorListener();

    }

    /**
     * 设置规划路线类型
     * @param type
     * 可选值 1、2  1代表驾车路线，2代表骑行路线
     * */
    public void setType(int type){
        this.type = type;
    }

    /**
     * 设置是否显示路线轨迹
     * @param line
     * true为显示，false为不显示
     * */
    public void setLine(String line){
        this.line = line;
    }

    /**
     * 添加地点
     * @param pos
     * 地点数据
     * */
    public void addPosition(String pos) {
        String[] s = pos.split(",");
        if (s.length == 3) {
            posList.add(s);
        }
    }

    /**
     * 执行方法
     * */
    @Override
    public void execute() {
        getStartandendPoint();
        if (!this.visiable) return;
        if (this.width == "-1" && this.height == -1) {
            valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        } else {
            if (this.width == "-1") {
                valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, this.height, 1));
            } else if (this.height == -1) {
                if (this.width.endsWith("%")) {
                    int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
                    valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
                } else {
                    valLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            } else {
                if (this.width.endsWith("%")) {
                    int viewWeight = 100 - (int) Float.parseFloat(this.width.substring(0, this.width.indexOf("%")));
                    valLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, viewWeight));
                } else {
                    valLayout.setLayoutParams(new LinearLayout.LayoutParams(Integer.parseInt(this.width), this.height));
                }
            }
        }
        if (posList.size()!=-1) {
            if (mPointList.size()==1){
                if (posList.size()!=-1){
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Double.valueOf(posList.get(0)[0]),Double.valueOf(posList.get(0)[1])),18,0,30));
                    aMap.moveCamera(cameraUpdate);
                }
            }
            else{
                if (line.equals("true")) {
                    for (int x = 0; x < mPointList.size() - 1; x++) {
                        if (type == 1) {
                            searchRouteResult(mPointList.get(x), mPointList.get(x + 1), ROUTE_TYPE_DRIVE, x);
                        } else if (type == 2) {
                            searchRouteResult(mPointList.get(x), mPointList.get(x + 1), ROUTE_TYPE_RIDE, x);
                        } else {
                            searchRouteResult(mPointList.get(x), mPointList.get(x + 1), ROUTE_TYPE_RIDE, x);
                        }
                    }
                }else{
                    for (int x=0;x<mPointList.size();x++) {
                        markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .position(new LatLng(mPointList.get(x).getLatitude(), mPointList.get(x).getLongitude()))
                                .draggable(true)
                                .title(desList.get(x));
                        Marker marker = new Marker(markerOptions);
                        marker.setTitle(desList.get(x));
                        pointList.add(marker);
                        aMap.addMarker(markerOptions);

                        try {
                            LatLngBounds.Builder b = LatLngBounds.builder();
                            b.include(new LatLng(mPointList.get(0).getLatitude(),mPointList.get(0).getLongitude()));
                            b.include(new LatLng(mPointList.get(mPointList.size()-1).getLatitude(),mPointList.get(mPointList.size()-1).getLongitude()));
                            LatLngBounds bounds = b.build();
                            aMap.animateCamera(CameraUpdateFactory
                                    .newLatLngBounds(bounds, 100));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }



                    }
                }
            }
        }

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        obtainLocation();
        mapPointOverlay = new MapPointOverlay(pointList.size() - 1);
    }

    // Get the starting point and the end point
    /**
     * 获得起始点和终点
     * */
    public void getStartandendPoint(){
        if (posList.size()!=-1){
            for (int x = 0; x <posList.size(); x++) {
                Double w = Double.parseDouble(posList.get(x)[0]);
                Double j = Double.parseDouble(posList.get(x)[1]);
                String des = posList.get(x)[2];
                desList.add(des);
                mPointList.add(new LatLonPoint(w, j));
            }
        }

    }

    // Customizable Rotatable Positioning
    /**
     * 自定义可旋转定位
     * */
    private void obtainLocation() {

        // Initialize the locating blue dot style class
        //初始化定位蓝点样式类
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                // Set the icon for the small blue dot
                // 设置小蓝点的图标
                .fromResource(R.drawable.axeac_location_marker));
        // Set the border color of the circle
        // 设置圆形的边框颜色
        myLocationStyle.strokeColor(Color.BLACK);
        // Set the fill color of the circle
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
        // Set the border thickness of the circle
        // 设置圆形的边框粗细
        myLocationStyle.strokeWidth(1.0f);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
                if (mlocationClient == null) {
                    mlocationClient = new AMapLocationClient(ctx);
                    mLocationOption = new AMapLocationClientOption();
                    // Set the positioning listener
                    //设置定位监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {
                            if (mListener != null && aMapLocation != null) {
                                if (aMapLocation != null
                                        && aMapLocation.getErrorCode() == 0) {
                                    LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                                    if (!mFirstFix) {
                                        mFirstFix = true;
                                        // Add positioning accuracy circle
                                        //添加定位精度圆
                                        addCircle(location, aMapLocation.getAccuracy());
                                        // Add a targeting icon
                                        //添加定位图标
                                        addMarker(location,R.drawable.axeac_navi_map_gps_locked);
                                        // positioning icon rotation
                                        // 定位图标旋转
                                        mSensorHelper.setCurrentMarker(mLocMarker);
                                    } else {
                                        mCircle.setCenter(location);
                                        mCircle.setRadius(aMapLocation.getAccuracy());
                                        mLocMarker.setPosition(location);
                                    }
                                    if (posList.size()==0)
                                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                                } else {
                                    String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                                    Log.e("AmapErr",errText);
                                }
                            }
                        }

                    });
                    // Set to high-precision positioning mode
                    //设置为高精度定位模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    mLocationOption.setInterval(2000);
                    // Whether it is positioned once
                    //是否为一次定位
                    mLocationOption.setOnceLocation(false);
                    // Set the positioning parameters
                    //设置定位参数
                    mlocationClient.setLocationOption(mLocationOption);
                    /*
                        This method initiates a location request every fixed time. in order to reduce
                        power consumption or network traffic consumption,note that the appropriate
                        positioning time interval (minimum interval support is 2000ms) is set and
                        the stopLocation () method is called at the appropriate time to cancel the
                        positioning request.
             After the positioning is complete, call the onDestroy () method at the
                        appropriate lifecycle.
             In the case of a single positioning, the positioning of the success,
                        whether successful or not, No need to call stopLocation () method to remove
                        the request, positioning sdk will removed it.

                       此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                       注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                       在定位结束后，在合适的生命周期调用onDestroy()方法
                       在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                     */

                    mlocationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {

            }
        });
        // Set whether the default positioning button is displayed
        // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Set to true to display the positioning layer and can trigger the positioning, false that hide the positioning layer and can not trigger positioning, the default is false.
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false。
        aMap.setMyLocationEnabled(true);
    }

    // Customize the blue circle
    /**
     * 自定义定位蓝色范围圈
     * @param latlng
     * 定位点
     * @param radius
     * 蓝色范围圈半径
     * */
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    // add marker
    /**
     * 添加标记
     * @param latlng
     * 坐标点
     * @param id
     * 图片资源id
     * */
    private void addMarker(LatLng latlng,int id) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(ctx.getResources(),
                id);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    /**
     * 查询位置方法
     * */
    private void doSearchQuery() {

        geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        geocoderSearch = new GeocodeSearch(ctx);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                dissmissProgressDialog();
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                            && geocodeResult.getGeocodeAddressList().size() > 0) {
                        GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                AMapUtil.convertToLatLng(address.getLatLonPoint()), 17));
                        geoMarker.setPosition(AMapUtil.convertToLatLng(address
                                .getLatLonPoint()));
                        resultList.add(geoMarker);
                        addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                                + address.getFormatAddress();
                        ToastUtil.show(ctx, addressName);
                    } else {
                        ToastUtil.show(ctx, R.string.no_result);
                    }
                } else {
                    ToastUtil.showerror(ctx, i);
                }
            }
        });
    }

    // According to the name get the latitude and longitude address
    /**
     * 根据名称获得经纬度地址
     * @param name
     * 搜索的名称
     * */
    public void getLatlon(final String name) {
        if (geoMarker!=null){
            geoMarker.remove();
        }
        showProgressDialog();
        doSearchQuery();
        // The first parameter is address, the second parameter is query the city, Chinese or Chinese Quanpin, citycode, adcode,
        // 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        GeocodeQuery query = new GeocodeQuery(name, "010");
        // Set up a geocoding request
        // 设置同步地理编码请求
        geocoderSearch.getFromLocationNameAsyn(query);
    }

    // Plan the path to the map
    /**
     * 规划路径至地图
     * @param startPoint
     * 起始点坐标
     * @param endPoint
     * 终点坐标
     * @param type
     * 路线模式
     * @param pos
     * 标记点，用于取出list集合中正确位置描述
     * */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint,int type,final int pos) {
        setfromandtoMarker(startPoint,endPoint);
        mRouteSearch = new RouteSearch(ctx);
        searchRouteResult(type ,RouteSearch.WalkDefault,startPoint,endPoint);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                dissmissProgressDialog();
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (busRouteResult != null && busRouteResult.getPaths() != null) {
                        if (busRouteResult.getPaths().size() > 0) {
                            mBusRouteResult = busRouteResult;
                        } else if (busRouteResult != null && busRouteResult.getPaths() == null) {
                            ToastUtil.show(ctx, R.string.no_result);
                        }
                    } else {
                        ToastUtil.show(ctx, R.string.no_result);
                    }
                } else {
                    ToastUtil.showerror(ctx, i);
                }
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                dissmissProgressDialog();
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                        if (driveRouteResult.getPaths().size() > 0) {
                            mDriveRouteResult = driveRouteResult;
                            final DrivePath drivePath = mDriveRouteResult.getPaths()
                                    .get(0);
                            DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(
                                    ctx, aMap, drivePath,
                                    mDriveRouteResult.getStartPos(),
                                    mDriveRouteResult.getTargetPos(), null);
                            drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                            drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                            drivingRouteOverlay.removeFromMap();
                            drivingRouteOverlay.addToMap(pos);
                            drivingRouteOverlay.zoomToSpan();
                        } else if (driveRouteResult != null && driveRouteResult.getPaths() == null) {
                            ToastUtil.show(ctx, R.string.no_result);
                        }

                    } else {
                        ToastUtil.show(ctx, R.string.no_result);
                    }
                } else {
                    ToastUtil.showerror(ctx, i);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
                dissmissProgressDialog();
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                        if (walkRouteResult.getPaths().size() > 0) {
                            mWalkRouteResult = walkRouteResult;
                            final WalkPath walkPath = mWalkRouteResult.getPaths()
                                    .get(0);
                            WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                                    ctx, aMap, walkPath,
                                    mWalkRouteResult.getStartPos(),
                                    mWalkRouteResult.getTargetPos());
                            walkRouteOverlay.removeFromMap();
                            walkRouteOverlay.addToMap();
                            walkRouteOverlay.zoomToSpan();
                            int dis = (int) walkPath.getDistance();
                            int dur = (int) walkPath.getDuration();
                            String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";


                        } else if (walkRouteResult != null && walkRouteResult.getPaths() == null) {
                            ToastUtil.show(ctx, R.string.no_result);
                        }
                    } else {
                        ToastUtil.show(ctx, R.string.no_result);
                    }
                } else {
                    ToastUtil.showerror(ctx, i);
                }
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
                dissmissProgressDialog();
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (rideRouteResult != null && rideRouteResult.getPaths() != null) {
                        if (rideRouteResult.getPaths().size() > 0) {
                            mRideRouteResult = rideRouteResult;
                            final RidePath ridePath = mRideRouteResult.getPaths()
                                    .get(0);
                            RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                                    ctx, aMap, ridePath,
                                    mRideRouteResult.getStartPos(),
                                    mRideRouteResult.getTargetPos());
                            rideRouteOverlay.removeFromMap();
                            rideRouteOverlay.addToMap(pos);
                            rideRouteOverlay.zoomToSpan();
                        } else if (rideRouteResult != null && rideRouteResult.getPaths() == null) {
                            ToastUtil.show(ctx, R.string.no_result);
                        }
                    } else {
                        ToastUtil.show(ctx, R.string.no_result);
                    }
                } else {
                    ToastUtil.showerror(ctx, i);
                }
            }
        });
    }

    // plan route
    /**
     * 路径规划
     * @param routeType
     * 路线类型
     * @param mode
     * 查询模式
     * @param startPoint
     * 起始点
     * @param endPoint
     * 终点
     * */
    public void searchRouteResult(int routeType, int mode,LatLonPoint startPoint, LatLonPoint endPoint) {
        if (startPoint == null) {
            ToastUtil.show(ctx, "定位中，稍后再试...");
            return;
        }
        if (endPoint == null) {
            ToastUtil.show(ctx, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        // Walk
        // 步行
        if (routeType == ROUTE_TYPE_WALK) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            //  Asynchronous path planning Walking mode query
            // 异步路径规划步行模式查询
            mRouteSearch.calculateWalkRouteAsyn(query);
        }
        // Ride
        // 骑行
        if (routeType == ROUTE_TYPE_RIDE) {
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, mode);
            // Asynchronous path planning ride mode query
            // 异步路径规划骑行模式查询
            mRouteSearch.calculateRideRouteAsyn(query);
        }
        // Bus
        // 公交
        if (routeType == ROUTE_TYPE_BUS) {
            // The first parameter indicates the start and end of the path plan, the second parameter indicates the bus query mode, the third parameter indicates the bus area code, the fourth parameter indicates whether the night bus is calculated, 0 means no calculation
            // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    mCurrentCityName, 0);
            // Asynchronous path planning Bus mode query
            // 异步路径规划公交模式查询
            mRouteSearch.calculateBusRouteAsyn(query);
        }
        // Drive
        // 驾车
        if (routeType == ROUTE_TYPE_DRIVE) {
            // The first parameter represents the start and end of the path plan, the second parameter represents the driving mode, the third parameter indicates the passing point, the fourth parameter indicates the avoidance area, and the fifth parameter indicates the way
            // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");
            // Asynchronous path planning drive mode query
            // 异步路径规划驾车模式查询
            mRouteSearch.calculateDriveRouteAsyn(query);
        }
    }

    // Show progressDialog
    /**
     * 显示进度框
     * */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(ctx);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    // Hide progressDialog
    /**
     * 隐藏进度框
     * */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    // Set the start and end markers
    //设置起点和终点标记
    /**
     * 设置起点和终点标记
     * @param startPoint
     * 起始点
     * @param endPoint
     * 终点
     * */
    private void setfromandtoMarker(LatLonPoint startPoint, LatLonPoint endPoint) {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(startPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.axeac_start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(endPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.axeac_end)));
    }

    /**
     * 地图标记的监听事件
     * */
    public class MapPointOverlay implements AMap.OnMarkerClickListener {

        private ImageView pointImg;
        private int index;

        public MapPointOverlay(int index) {
            this.index = index;
            onTap();
        }

        public void onTap() {
            aMap.setOnMarkerClickListener(this);
            if (pointImg != null) {

            }
            if (mapPointOverlay != null) {

            }
            addMarkersToMap();

        }

        // add marker to the map
        //在地图上添加marker
        private void addMarkersToMap() {
            if (posList.size()==1){
                markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(new LatLng(mPointList.get(0).getLatitude(),mPointList.get(0).getLongitude()))
                        .draggable(true)
                        .title(desList.get(0));
                Marker marker = new Marker(markerOptions);
                marker.setTitle(desList.get(0));
                pointList.add(marker);
                aMap.addMarker(markerOptions);
            }
        }

        // Marker click event
        //标记点点击事件
        @Override
        public boolean onMarkerClick(final Marker marker) {
            if (aMap != null) {
                jumpPoint(marker);
                CustomDialog.Builder builder = new CustomDialog.Builder(ctx);
                builder.setTitle(R.string.axeac_msg_describe);
                if (marker.getSnippet()!=null){
                    builder.setMessage(marker.getTitle()+"\n"+marker.getSnippet());
                }else{
                    builder.setMessage(marker.getTitle());
                }

                builder.setPositiveButton(R.string.axeac_msg_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final CustomDialog dialog = builder.create();
                dialog.show();
            }
            return true;
        }

        /**
         * describe:Click on marker to bounce
         * 描述：marker点击时跳动一下
         */
        public void jumpPoint(final Marker marker) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = aMap.getProjection();
            final LatLng markerLatlng = marker.getPosition();
            Point markerPoint = proj.toScreenLocation(markerLatlng);
            markerPoint.offset(0, -100);
            final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * markerLatlng.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * markerLatlng.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));
                    if (t < 1.0) {
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }
    }

    @Override
    public View getView() {
        return valLayout;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public void repaint() {

    }

    @Override
    public void starting() {

    }

    /**
     * 本类结束后的操作
     * */
    @Override
    public void end() {
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mMapView.onPause();
        deactivate();
        mFirstFix = false;
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    /**
     * 关闭AMapLocationClient对象
     * */
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}