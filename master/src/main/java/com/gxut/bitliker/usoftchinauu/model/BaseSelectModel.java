package com.gxut.bitliker.usoftchinauu.model;

import com.alibaba.fastjson.JSON;
import com.gxut.bitliker.baseutil.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/7/4.
 */

public class BaseSelectModel<T> {
    private boolean select;
    private float sort;
    private String name;
    private String sub;
    private String tag;
    private T data;


    public BaseSelectModel(boolean select, String name, String sub, String tag) {
        this.select = select;
        this.name = name;
        this.sub = sub;
        this.tag = tag;
    }

    public BaseSelectModel(String name, String sub, String tag) {
        this.name = name;
        this.sub = sub;
        this.tag = tag;
    }

    public BaseSelectModel(String name, String sub) {
        this.name = name;
        this.sub = sub;
    }

    public float getSort() {
        return sort;
    }

    public void setSort(float sort) {
        this.sort = sort;
    }

    public BaseSelectModel(String name) {
        this.name = name;
    }

    public BaseSelectModel() {
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("select", select);
        map.put("sort", sort);
        map.put("name", name);
        map.put("sub", sub);
        if (data != null)
            map.put("data", JSON.toJSONString(data));
        return JSONUtil.mapToJson(map);
    }
}
