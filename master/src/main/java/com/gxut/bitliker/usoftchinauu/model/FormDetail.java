package com.gxut.bitliker.usoftchinauu.model;

import com.gxut.bitliker.baseutil.util.JSONUtil;
import com.gxut.bitliker.baseutil.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bitliker on 2017/8/26.
 */

public class FormDetail {
    private int groupId;
    private int id;
    private String type;//类型
    private String caption;//字幕
    private String valuesKey;//值对应的字段名称
    private Combostore combostore;//值
    private List<Combostore> combostores;


    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("id", id);
        map.put("type", type);
        map.put("caption", caption);
        map.put("valuesKey", valuesKey);
        map.put("combostore", getValues());
        return JSONUtil.mapToJson(map);
    }

    public FormDetail(boolean hasGroup) {
        if (hasGroup) {
            caption = "明细表";
            setGroupId(1);
        } else {
            caption = "主表";
            setGroupId(0);
        }
        type = "TAG";
    }

    public FormDetail(int detailItem) {
        caption = "明细表" + (detailItem == 0 ? "" : detailItem);
        setGroupId(1);
        type = "TAG";
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getValuesKey() {
        return valuesKey;
    }

    public void setValuesKey(String valuesKey) {
        this.valuesKey = valuesKey;
    }

    private Combostore getCombostore() {
        return combostore == null ? combostore = new Combostore("", "") : combostore;
    }

    public void setCombostore(Combostore combostore) {
        this.combostore = combostore;
    }


    public List<Combostore> getCombostores() {
        return combostores == null ? combostores = new ArrayList<>() : combostores;
    }

    public void addCombostore(Combostore combostore) {
        getCombostores().add(combostore);
    }

    public boolean isSelect() {
        if (type == null) return false;
        return Utils.equalsOne(type, "C", "B", "YN", "D", "DT");
    }

    public boolean isSelectDate() {
        if (type == null) return false;
        return Utils.equalsOne(type, "D", "DT");
    }

    public boolean isNumber() {
        if (type == null) return false;
        return Utils.equalsOne(type, "N", "floatcolumn8", "SN");
    }

    public boolean isTAG() {
        if (type == null) return false;
        return Utils.equalsOne(type, "TAG");
    }

    public void setValues(String values) {
        getCombostore().values = values;
        getCombostore().display = values;
    }

    public void setValues(String values, String display) {
        getCombostore().values = values;
        getCombostore().display = display;
    }

    public String getValues() {
        return Utils.isEmpty(getCombostore().values) ? (Utils.isEmpty(getCombostore().display) ? "" : getCombostore().display) : getCombostore().values;
    }

    public String getPutValues() {
        return Utils.isEmpty(getCombostore().values) ? (Utils.isEmpty(getCombostore().display) ? "" : getCombostore().display) : getCombostore().values;
    }


    public static class Combostore {
        private String values;//显示值
        private String display;//实际值

        public Combostore(String values, String display) {
            this.values = values;
            this.display = display;
        }

        public String getValues() {
            return values;
        }

        public String getDisplay() {
            return display;
        }
    }

}
