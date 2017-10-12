package com.gxut.bitliker.usoftchinauu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class User implements Parcelable {
    private int id;//id
    private String account;//账号,手机号码
    private String password;//密码(加密后)
    private String emName; //人员名称
    private String emCode;//人员编号
    private String company;//公司名称
    private String master;//账套
    private String masterName;//账套名称
    private String baseUrl;//网址前缀
    private String sessionId;//sessionId

    public User() {

    }

    public User(User user) {
        this.id = user.id;
        this.account = user.account;
        this.password = user.password;
        this.emName = user.emName;
        this.emCode = user.emCode;
        this.company = user.company;
        this.master = user.master;
        this.masterName = user.masterName;
        this.baseUrl = user.baseUrl;
        this.sessionId = user.sessionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return Utils.isEmpty(account) ? "" : account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return Utils.isEmpty(password) ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmName() {
        return Utils.isEmpty(emName) ? "" : emName;
    }

    public void setEmName(String emName) {
        this.emName = emName;
    }

    public String getEmCode() {
        return Utils.isEmpty(emCode) ? "" : emCode;
    }

    public void setEmCode(String emCode) {
        this.emCode = emCode;
    }

    public String getCompany() {
        return Utils.isEmpty(company) ? "" : company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterName() {
        return Utils.isEmpty(masterName) ? "" : masterName;
    }

    public String getMaster() {
        return Utils.isEmpty(master) ? "" : master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getBaseUrl() {
        return Utils.isEmpty(baseUrl) ? "" : baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSessionId() {
        return Utils.isEmpty(sessionId) ? "" : sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", id);
        userMap.put("account", account);
        userMap.put("password", password);
        userMap.put("emName", emName);
        userMap.put("emCode", emCode);
        userMap.put("company", company);
        userMap.put("masterName", masterName);
        userMap.put("master", master);
        userMap.put("baseUrl", baseUrl);
        userMap.put("sessionId", sessionId);
        return JSONUtil.mapToJson(userMap);
    }

    public boolean isEmpty() {
        return (Utils.isEmpty(account)
                || Utils.isEmpty(password)
                || Utils.isEmpty(company) ||
                Utils.isEmpty(master) ||
                Utils.isEmpty(emCode));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.account);
        dest.writeString(this.password);
        dest.writeString(this.emName);
        dest.writeString(this.emCode);
        dest.writeString(this.company);
        dest.writeString(this.master);
        dest.writeString(this.masterName);
        dest.writeString(this.baseUrl);
        dest.writeString(this.sessionId);
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.account = in.readString();
        this.password = in.readString();
        this.emName = in.readString();
        this.emCode = in.readString();
        this.company = in.readString();
        this.master = in.readString();
        this.masterName = in.readString();
        this.baseUrl = in.readString();
        this.sessionId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
