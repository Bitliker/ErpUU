package com.gxut.bitliker.usoftchinauu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitliker on 2017/8/28.
 */

public class FormDetailList {

    private int id;
    private String caller;
    private String status;
    private List<ItemData> itemDatas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaller() {
        return caller;
    }

    public String getStatus() {
        return status;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemData> getItemDatas() {
        return itemDatas == null ? itemDatas=new ArrayList<ItemData>() : itemDatas;
    }


    public void addItemData(String key, String values, String valuesKey) {
        getItemDatas().add(new ItemData(key, values, valuesKey));
    }

    public class ItemData {
        private String key;
        private String values;
        private String valuesKey;

        public ItemData(String key, String values, String valuesKey) {
            this.key = key;
            this.values = values;
            this.valuesKey = valuesKey;
        }

        public String getKey() {
            return key == null ? "" : key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValues() {
            return values == null ? "" : values;
        }

        public void setValues(String values) {
            this.values = values;
        }

        public String getValuesKey() {
            return valuesKey == null ? "" : valuesKey;
        }

        public void setValuesKey(String valuesKey) {
            this.valuesKey = valuesKey;
        }
    }
}
