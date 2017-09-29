package com.gxut.bitliker.usoftchinauu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.baseutil.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/8/25.
 */

public class Daily implements Parcelable {

    private int id;
    private String emName;//姓名
    private String depart;//部门
    private String joname;//职位
    private String date;//日期
    private String comment;//总结
    private String plan;//计划
    private String experience;//心得
    private String status;//状态
    private String webText;//web显示数据

    public Daily(JSONObject object) {
        id = JSONUtil.getInt(object, "WD_ID");
        emName = JSONUtil.getText(object, "WD_EMP");
        depart = JSONUtil.getText(object, "WD_DEPART");
        joname = JSONUtil.getText(object, "WD_JONAME");
        date = JSONUtil.getText(object, "WD_DATE");
        comment = JSONUtil.getText(object, "WD_COMMENT");
        plan = JSONUtil.getText(object, "WD_PLAN");
        experience = JSONUtil.getText(object, "WD_EXPERIENCE");
        status = JSONUtil.getText(object, "WD_STATUS");
        webText = JSONUtil.getText(object, "WD_UNFINISHEDTASK");
    }

    @Override
    public String toString() {
        Map<String, Object> string = new HashMap<>();
        string.put("id", id);
        string.put("emName", emName);
        string.put("depart", depart);
        string.put("joname", joname);
        string.put("date", date);
        string.put("experience", experience);
        string.put("plan", plan);
        string.put("comment", comment);
        string.put("status", status);
        string.put("webText", webText);
        return super.toString();
    }

    public boolean isEmpty() {
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmName() {
        return emName;
    }

    public void setEmName(String emName) {
        this.emName = emName;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getJoname() {
        return joname;
    }

    public void setJoname(String joname) {
        this.joname = joname;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getWebText() {
        return webText;
    }

    public void setWebText(String webText) {
        this.webText = webText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.emName);
        dest.writeString(this.depart);
        dest.writeString(this.joname);
        dest.writeString(this.date);
        dest.writeString(this.experience);
        dest.writeString(this.plan);
        dest.writeString(this.comment);
        dest.writeString(this.status);
        dest.writeString(this.webText);
    }

    public Daily() {
    }

    protected Daily(Parcel in) {
        this.id = in.readInt();
        this.emName = in.readString();
        this.depart = in.readString();
        this.joname = in.readString();
        this.date = in.readString();
        this.experience = in.readString();
        this.plan = in.readString();
        this.comment = in.readString();
        this.status = in.readString();
        this.webText = in.readString();
    }

    public static final Parcelable.Creator<Daily> CREATOR = new Parcelable.Creator<Daily>() {
        @Override
        public Daily createFromParcel(Parcel source) {
            return new Daily(source);
        }

        @Override
        public Daily[] newArray(int size) {
            return new Daily[size];
        }
    };
}
