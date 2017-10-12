package com.gxut.bitliker.usoftchinauu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.gxut.code.network.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/6/22.
 */

public class Locale implements Parcelable {
    private int code;
    private LatLng latLng;
    private String city;
    private String name;
    private String address;
    private int signinRange;//打卡范围
    private int workRange;//办公范围

    public Locale(int code, LatLng latLng, String name, String address) {
        this.code = code;
        this.latLng = latLng;
        this.name = name;
        this.address = address;
    }

    public Locale() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address==null?"":address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSigninRange() {
        return signinRange == 0 ? signinRange = 200 : signinRange;
    }

    public void setSigninRange(int signinRange) {
        this.signinRange = signinRange;
    }

    public int getWorkRange() {
        return workRange;
    }

    public void setWorkRange(int workRange) {
        this.workRange = workRange;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("name", name);
        map.put("city", city);
        map.put("address", address);
        map.put("latitude", latLng.latitude);
        map.put("longitude", latLng.longitude);
        return JSONUtil.mapToJson(map);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.city);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeInt(this.signinRange);
        dest.writeInt(this.workRange);
    }

    protected Locale(Parcel in) {
        this.code = in.readInt();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.city = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.signinRange = in.readInt();
        this.workRange = in.readInt();
    }

    public static final Creator<Locale> CREATOR = new Creator<Locale>() {
        @Override
        public Locale createFromParcel(Parcel source) {
            return new Locale(source);
        }

        @Override
        public Locale[] newArray(int size) {
            return new Locale[size];
        }
    };
}
