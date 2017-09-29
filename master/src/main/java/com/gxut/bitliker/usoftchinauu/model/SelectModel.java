package com.gxut.bitliker.usoftchinauu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gxut.bitliker.baseutil.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/6/30.
 */

public class SelectModel implements Parcelable {
    private boolean select;
    private float sort;
    private String name;
    private String sub;
    private String tag;
    private String json;//JSON.pase   ==>  Object==>json

    public SelectModel() {

    }

    public SelectModel(boolean select) {
        this.select = select;
    }

    public SelectModel(boolean select, String... keys) {
        this.select = select;
        setData(keys);
    }

    public SelectModel(String... keys) {
        setData(keys);
    }

    private void setData(String... keys) {
        if (keys != null && keys.length > 0) {
            for (int i = 0; i < keys.length; i++) {
                if (i == 0) name = keys[i];
                if (i == 1) sub = keys[i];
                if (i == 2) tag = keys[i];
                if (i == 3) json = keys[i];
            }
        }
    }


    public SelectModel setSelect(boolean select) {
        this.select = select;
        return this;
    }
    public SelectModel setName(String name) {
        this.name = name;
        return this;
    }
    public SelectModel setSub(String sub) {
        this.sub = sub;
        return this;
    }
    public SelectModel setJson(String json) {
        this.json = json;
        return this;
    }
    public SelectModel setTag(String tag) {
        this.tag = tag;
        return this;
    }
    public SelectModel setSort(float sort) {
        this.sort = sort;
        return this;
    }

    public boolean isSelect() {
        return select;
    }

    public String getName() {
        return name == null ? "" : name;
    }


    public String getSub() {
        return sub == null ? "" : sub;
    }


    public String getJson() {
        return json == null ? "" : json;
    }


    public String getTag() {
        return tag == null ? "" : tag;
    }


    public float getSort() {
        return sort;
    }



    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("select", select);
        map.put("sort", sort);
        map.put("name", name);
        map.put("sub", sub);
        map.put("tag", tag);
        map.put("json", json);
        return JSONUtil.mapToJson(map);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.sort);
        dest.writeString(this.name);
        dest.writeString(this.sub);
        dest.writeString(this.tag);
        dest.writeString(this.json);
    }

    protected SelectModel(Parcel in) {
        this.select = in.readByte() != 0;
        this.sort = in.readFloat();
        this.name = in.readString();
        this.sub = in.readString();
        this.tag = in.readString();
        this.json = in.readString();
    }

    public static final Creator<SelectModel> CREATOR = new Creator<SelectModel>() {
        @Override
        public SelectModel createFromParcel(Parcel source) {
            return new SelectModel(source);
        }

        @Override
        public SelectModel[] newArray(int size) {
            return new SelectModel[size];
        }
    };
}
