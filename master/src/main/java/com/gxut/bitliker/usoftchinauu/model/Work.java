package com.gxut.bitliker.usoftchinauu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/6/23.
 */

public class Work implements Parcelable {
    private int id;

    private String workStart;//上班开始时间
    private String workTime;//上班时间
    private String workEnd;//上班结束时间
    private String workSignin;//上班打卡

    private String offStart;//下班开始时间
    private String offTime;//下班时间
    private String offSignin;//下班签到时间
    private String offEnd;//下班结束时间


    public Work() {
    }

    public void init(String workStart, String workTime, String offTime, String offEnd) {
        this.workStart = workStart;
        this.workTime = workTime;
        this.offTime = offTime;
        this.offEnd = offEnd;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public String getWorkStart() {
        return workStart;
    }

    public void setWorkStart(String workStart) {
        this.workStart = workStart;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getWorkEnd() {
        return workEnd;
    }

    public void setWorkEnd(String workEnd) {
        this.workEnd = workEnd;
    }

    public String getWorkSignin() {
        return workSignin;
    }

    public void setWorkSignin(String workSignin) {
        this.workSignin = workSignin;
    }

    public String getOffStart() {
        return offStart;
    }

    public void setOffStart(String offStart) {
        this.offStart = offStart;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }

    public String getOffSignin() {
        return offSignin;
    }

    public void setOffSignin(String offSignin) {
        this.offSignin = offSignin;
    }

    public String getOffEnd() {
        return offEnd;
    }

    public void setOffEnd(String offEnd) {
        this.offEnd = offEnd;
    }

    public boolean isEmpty() {
        return Utils.isEmpty(workStart) || Utils.isEmpty(workTime) || Utils.isEmpty(workEnd) ||
                Utils.isEmpty(offStart) || Utils.isEmpty(offTime) || Utils.isEmpty(offEnd);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.workStart);
        dest.writeString(this.workTime);
        dest.writeString(this.workEnd);
        dest.writeString(this.workSignin);
        dest.writeString(this.offStart);
        dest.writeString(this.offTime);
        dest.writeString(this.offSignin);
        dest.writeString(this.offEnd);
    }

    protected Work(Parcel in) {
        this.id = in.readInt();
        this.workStart = in.readString();
        this.workTime = in.readString();
        this.workEnd = in.readString();
        this.workSignin = in.readString();
        this.offStart = in.readString();
        this.offTime = in.readString();
        this.offSignin = in.readString();
        this.offEnd = in.readString();
    }

    public static final Creator<Work> CREATOR = new Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel source) {
            return new Work(source);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };


    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id",id);
        map.put("workStart",workStart);
        map.put("workTime",workTime);
        map.put("workEnd",workEnd);
        map.put("workSignin",workSignin);
        map.put("offStart",offStart);
        map.put("offTime",offTime);
        map.put("offSignin",offSignin);
        map.put("offEnd",offEnd);
        return JSONUtil.mapToJson(map);
    }

}
