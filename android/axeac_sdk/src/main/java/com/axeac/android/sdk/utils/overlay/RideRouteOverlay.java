package com.axeac.android.sdk.utils.overlay;

import android.content.Context;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideStep;
import com.axeac.android.sdk.utils.AMapUtil;

import java.util.List;

/**
 * describe:Ride the route layer class. In the GaodeMap API, you can use this type to create a ride
 * 			route layer if you want to show a walking route plan. If you do not meet the needs, you
 * 			can also create a custom ride route layer.
 *
 * 骑行路线图层类。在高德地图API里，如果要显示步行路线规划，可以用此类来创建骑行路线图层。
 * 		如不满足需求，也可以自己创建自定义的骑行路线图层。
 *
 * @since V3.5.0
 */
public class RideRouteOverlay extends RouteOverlay {

	private PolylineOptions mPolylineOptions;
	
	private BitmapDescriptor walkStationDescriptor= null;

	private RidePath ridePath;
	/**
	 * describe:Use this constructor to create a ride route layer.
	 *
	 * 描述：通过此构造函数创建骑行路线图层。
	 *
	 * @param context
	 * Current activity
	 * 当前activity。
	 *
	 * @param amap
	 * Map object
	 * 地图对象。
	 *
	 * @param path
	 * A plan for riding a route. See the <strong> <a href = "../../../../../../ search in the path query package (com.amap.api.services.route) of the search service module /com/amap/api/services/route/WalkStep.html "title =" class in com.amap.api.services.route "> WalkStep </a> </ strong>.
	 * 骑行路线规划的一个方案。详见搜索服务模块的路径查询包（com.amap.api.services.route）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/route/WalkStep.html" title="com.amap.api.services.route中的类">WalkStep</a></strong>。
	 *
	 * @param start
	 * starting point. See the class <strong> <a href = "../../../../../../ Search in the core package (com.amap.api.services.core) of the search service module /com/amap/api/services/core/LatLonPoint.html "title =" class in com.amap.api.services.core "> LatLonPoint </a> </ strong>.
	 * 起点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类<strong><a href="../../../../../../Search/com/amap/api/services/core/LatLonPoint.html" title="com.amap.api.services.core中的类">LatLonPoint</a></strong>。
	 *
	 * @param end
	 * end point. See the class <strong> <a href = "../../../../../../ Search in the core package (com.amap.api.services.core) of the search service module /com/amap/api/services/core/LatLonPoint.html "title =" class in com.amap.api.services.core "> LatLonPoint </a> </ strong>.
	 * 终点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类<strong><a href="../../../../../../Search/com/amap/api/services/core/LatLonPoint.html" title="com.amap.api.services.core中的类">LatLonPoint</a></strong>。
	 *
	 * @since V3.5.0
	 */
	public RideRouteOverlay(Context context, AMap amap, RidePath path,
                            LatLonPoint start, LatLonPoint end) {
		super(context);
		this.mAMap = amap;
		this.ridePath = path;
		startPoint = AMapUtil.convertToLatLng(start);
		endPoint = AMapUtil.convertToLatLng(end);
	}
	/**
	 * describe:Add a ride route to the map.
	 * 描述：添加骑行路线到地图中。
	 * @since V3.5.0
	 */
	public void addToMap(int pos) {
		
		initPolylineOptions();
		try {
			List<RideStep> ridePaths = ridePath.getSteps();
			mPolylineOptions.add(startPoint);
			for (int i = 0; i < ridePaths.size(); i++) {
				RideStep rideStep = ridePaths.get(i);
				LatLng latLng = AMapUtil.convertToLatLng(rideStep
						.getPolyline().get(0));


//				addRideStationMarkers(rideStep, latLng);
				addRidePolyLines(rideStep);
			}
			mPolylineOptions.add(endPoint);
			addStartAndEndMarker(pos);
			
			showPolyline();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param rideStep
	 */
	private void addRidePolyLines(RideStep rideStep) {
		mPolylineOptions.addAll(AMapUtil.convertArrList(rideStep.getPolyline()));
	}
	/**
	 * @param rideStep
	 * @param position
	 */
	private void addRideStationMarkers(RideStep rideStep, LatLng position) {
		addStationMarker(new MarkerOptions()
				.position(position)
				.title("\u65B9\u5411:" + rideStep.getAction()
						+ "\n\u9053\u8DEF:" + rideStep.getRoad())
				.snippet(rideStep.getInstruction()).visible(nodeIconVisible)
				.anchor(0.5f, 0.5f).icon(walkStationDescriptor));
	}
	
	 /**
	  * describe:Initialize line segment properties
     * 描述：初始化线段属性
     */
    private void initPolylineOptions() {
    	
//    	if(walkStationDescriptor == null) {
//    		walkStationDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.amap_man);
//    	}
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }
	 private void showPolyline() {
	        addPolyLine(mPolylineOptions);
	    }
}
