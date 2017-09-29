package com.gxut.bitliker.usoftchinauu.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gxut.bitliker.baseutil.util.JSONUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.model.Locale;

import java.text.DecimalFormat;

/**
 * Created by Bitliker on 2017/6/23.
 */

public class BDMapUtil {
    public static float distance(LatLng poi1, LatLng poi2) {
        if (poi1 == null || poi2 == null) return -1f;
        double distance = Math.abs(DistanceUtil.getDistance(poi1, poi2));
        DecimalFormat df = new DecimalFormat(".##");
        try {
            return Float.valueOf(df.format(distance));
        } catch (ClassCastException e) {
            return -1f;
        } catch (Exception e) {
            return -1f;
        }
    }

    public static float workDistance(LatLng myLocale, LatLng companyPoi) {
        if (myLocale == null || companyPoi == null) return -1f;
        return distance(myLocale, new LatLng(companyPoi.longitude, companyPoi.latitude));
    }

    public static float workDistance(LatLng companyPoi) {
        Locale myLocation = LocationHelper.api().getLocation();
        if (myLocation == null || companyPoi == null) return -1f;
        return workDistance(myLocation.getLatLng(), companyPoi);
    }

    public static void showMapPount(MapView mapView, LatLng point) {
        showMapPount(mapView, point, false);
    }

    public static void showMapPount(MapView mapView, LatLng point, boolean isClear) {
        try {
            if (point == null) return;
            // 构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_point);
            // 构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            if (isClear) mapView.getMap().clear();
            // 在地图上添加Marker，并显示
            mapView.getMap().addOverlay(option);
            mapView.showZoomControls(false);
            MapStatus mapStatus = new MapStatus.Builder().zoom(mapView.getMap().getMaxZoomLevel() - 3).target(point).build();
            MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mapView.getMap().animateMapStatus(u);//设置为中心显示
        } catch (Exception e) {

        }
    }


    public static PoiInfo json2PoiInfo(String message) {
        if (Utils.isEmpty(message) || !JSONUtil.validatorJson(message)) return null;
        JSONObject object = JSON.parseObject(message);
        PoiInfo info = new PoiInfo();
        info.address = JSONUtil.getText(object, "address");
        info.phoneNum = JSONUtil.getText(object, "phoneNum");
        info.city = JSONUtil.getText(object, "city");
        info.name = JSONUtil.getText(object, "name");
        info.uid = JSONUtil.getText(object, "uid");
        info.isPano = JSONUtil.getBoolean(object, "isPano");
        info.hasCaterDetails = JSONUtil.getBoolean(object, "hasCaterDetails");
        double longitude = JSONUtil.getDouble(object, "longitude");
        double latitude = JSONUtil.getDouble(object, "latitude");
        if (latitude > 0 && longitude > 0) {
            info.location = new LatLng(latitude, longitude);
        }
        return info;
    }
}
