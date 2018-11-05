package com.axeac.android.sdk.utils.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.axeac.android.sdk.R;
import com.axeac.android.sdk.ui.Map;

import java.util.ArrayList;
import java.util.List;

public class RouteOverlay {
	protected List<Marker> stationMarkers = new ArrayList<Marker>();
	protected List<Polyline> allPolyLines = new ArrayList<Polyline>();
	protected Marker startMarker;
	protected Marker endMarker;
	protected LatLng startPoint;
	protected LatLng endPoint;
	protected AMap mAMap;
	private Context mContext;
	private Bitmap startBit, endBit, busBit, walkBit, driveBit;
	protected boolean nodeIconVisible = true;

	public RouteOverlay(Context context) {
		mContext = context;
	}

	/**
	 * describe:Remove all Marker from BusRouteOverlay.
	 * 描述：去掉BusRouteOverlay上所有的Marker。
	 * @since V2.1.0
	 */
	public void removeFromMap() {
		if (startMarker != null) {
			startMarker.remove();

		}
		if (endMarker != null) {
			endMarker.remove();
		}
		for (Marker marker : stationMarkers) {
			marker.remove();
		}
		for (Polyline line : allPolyLines) {
			line.remove();
		}
		destroyBit();
	}

	private void destroyBit() {
		if (startBit != null) {
			startBit.recycle();
			startBit = null;
		}
		if (endBit != null) {
			endBit.recycle();
			endBit = null;
		}
		if (busBit != null) {
			busBit.recycle();
			busBit = null;
		}
		if (walkBit != null) {
			walkBit.recycle();
			walkBit = null;
		}
		if (driveBit != null) {
			driveBit.recycle();
			driveBit = null;
		}
	}

	/**
	 * describe:Set the icon to the passing marker and return the picture of the replacement icon.
	 * 			If you do not use the default picture, you need to override this method.
	 *
	 * 描述：给经过点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
	 *
	 * @return
	 * the Marker picture of replace
	 * 更换的Marker图片。
	 *
	 * @since V2.1.0
	 */
	protected BitmapDescriptor getThroughBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.axeac_amap_through);
	}

	/**
	 * describe:Set the icon to the start Marker and return the image of the replacement icon.
	 * 			If you do not use the default picture, you need to override this method.
	 *
	 * 描述：给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
	 *
	 * @return
	 * the Marker picture of replace
	 * 更换的Marker图片。
	 *
	 * @since V2.1.0
	 */
	protected BitmapDescriptor getStartBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.axeac_amap_start);
	}
	/**
	 * describe:Set the icon to the end marker and return the picture of the replacement icon.
	 * 			If you do not use the default picture, you need to override this method.
	 *
	 * 描述：给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
	 *
	 * @return
	 *  the Marker picture of replace
	 * 更换的Marker图片。
	 *
	 * @since V2.1.0
	 */
	protected BitmapDescriptor getEndBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.axeac_amap_end);
	}


	protected BitmapDescriptor getDriveBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.axeac_amap_car);
	}

	protected void addStartAndEndMarker(int pos) {
		if (pos==0&& Map.desList.size()<3){
			startMarker = mAMap.addMarker((new MarkerOptions())
					.position(startPoint).icon(getStartBitmapDescriptor())
					.title(Map.desList.get(pos)));
			endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
					.icon(getEndBitmapDescriptor())
					.title(Map.desList.get(pos + 1)));
		}else if (pos==0&& Map.desList.size()>2) {
			startMarker = mAMap.addMarker((new MarkerOptions())
					.position(startPoint).icon(getStartBitmapDescriptor())
//				.title("\u8D77\u70B9"));
					.title(Map.desList.get(pos)));
			endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
					.icon(getThroughBitmapDescriptor())
					.title(Map.desList.get(pos + 1)));
//				.title("\u7EC8\u70B9"));
		}else if (Map.desList.size()>2&&pos== Map.desList.size()-2){
			startMarker = mAMap.addMarker((new MarkerOptions())
					.position(startPoint).icon(getThroughBitmapDescriptor())
					.title(Map.desList.get(pos)));
			endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
					.icon(getEndBitmapDescriptor())
					.title(Map.desList.get(pos + 1)));
		}else{
			startMarker = mAMap.addMarker((new MarkerOptions())
					.position(startPoint).icon(getThroughBitmapDescriptor())
					.title(Map.desList.get(pos)));
			endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
					.icon(getThroughBitmapDescriptor())
					.title(Map.desList.get(pos + 1)));
		}
	}
	/**
	 * describe:Move the lens to the current perspective.
	 * 描述：移动镜头到当前的视角。
	 * @since V2.1.0
	 */
	public void zoomToSpan() {
		if (startPoint != null) {
			if (mAMap == null)
				return;
			try {
				LatLngBounds bounds = getLatLngBounds();
				mAMap.animateCamera(CameraUpdateFactory
						.newLatLngBounds(bounds, 100));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	protected LatLngBounds getLatLngBounds() {
		LatLngBounds.Builder b = LatLngBounds.builder();
		b.include(new LatLng(startPoint.latitude, startPoint.longitude));
		b.include(new LatLng(endPoint.latitude, endPoint.longitude));
		return b.build();
	}
	/**
	 * describe:The road section icon controls the display interface.
	 *
	 * 描述：路段节点图标控制显示接口。
	 *
	 * @param visible
	 * True to display the node icon, false is not displayed.
	 * true为显示节点图标，false为不显示。
	 *
	 * @since V2.3.1
	 */
	public void setNodeIconVisibility(boolean visible) {
		try {
			nodeIconVisible = visible;
			if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
				for (int i = 0; i < this.stationMarkers.size(); i++) {
					this.stationMarkers.get(i).setVisible(visible);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	protected void addStationMarker(MarkerOptions options) {
		if(options == null) {
			return;
		}
		Marker marker = mAMap.addMarker(options);
		if(marker != null) {
			stationMarkers.add(marker);
		}
		
	}

	protected void addPolyLine(PolylineOptions options) {
		if(options == null) {
			return;
		}
		Polyline polyline = mAMap.addPolyline(options);
		if(polyline != null) {
			allPolyLines.add(polyline);
		}
	}
	
	protected float getRouteWidth() {
		return 18f;
	}

	protected int getWalkColor() {
		return Color.parseColor("#6db74d");
	}

	/**
	 * describe:Customize route colors.
	 * 描述：自定义路线颜色。
	 *
	 * return
	 * the route colors of customize
	 * 自定义路线颜色。
	 *
	 * @since V2.2.1
	 */
	protected int getBusColor() {
		return Color.parseColor("#537edc");
	}

	protected int getDriveColor() {
		return Color.parseColor("#537edc");
	}

}
