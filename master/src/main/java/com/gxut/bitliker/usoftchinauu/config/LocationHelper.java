package com.gxut.bitliker.usoftchinauu.config;

import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.gxut.bitliker.baseutil.util.LogUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.model.Locale;

/**
 * Created by Bitliker on 2017/6/22.
 */

public class LocationHelper {
    private LocationClient mLocationClient;
    private Locale location;
    private static LocationHelper api;

    public static LocationHelper api() {
        LocationHelper inst = api;
        if (inst == null) {
            synchronized (LocationHelper.class) {
                inst = api;
                if (inst == null) {
                    inst = new LocationHelper();
                    api = inst;
                }
            }
        }
        return inst;
    }

    public Locale getLocation() {
        return location;
    }

    public void requestLocation() {
        if (mLocationClient != null)
            mLocationClient.requestLocation();
    }

    private LocationHelper() {
        mLocationClient = new LocationClient(Utils.getContext().getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        mLocationClient.setLocOption(initLocation());
        mLocationClient.start();
    }

    private LocationClientOption initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
//        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(false);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        return option;
    }

    private BDLocationListener mLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            handlerLocation(location);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };


    private void handlerLocation(BDLocation bdLocation) {
        if (bdLocation == null) return;
        int code = bdLocation.getLocType();
        if (code == BDLocation.TypeGpsLocation ||
                code == BDLocation.TypeNetWorkLocation ||
                code == BDLocation.TypeOffLineLocation) {
            if (location == null)
                this.location = new Locale();
            this.location.setCode(code);
            this.location.setCity(bdLocation.getCity());
            this.location.setAddress(bdLocation.getAddrStr());
            this.location.setName(bdLocation.getLocationDescribe());
            this.location.setLatLng(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
            LogUtil.i(this.location.toString());
            BroadcastManager.sendLocalBroadcast(new Intent(BroadcastManager.LOCATION_CHANGE)
                    .putExtra(BroadcastManager.MODEL_KEY, this.location));
        }
    }
}
