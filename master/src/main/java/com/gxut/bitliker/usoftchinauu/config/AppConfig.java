package com.gxut.bitliker.usoftchinauu.config;

import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.gxut.bitliker.baseutil.config.BaseApplication;
import com.gxut.bitliker.httpclient.HttpClient;
import com.gxut.bitliker.httpclient.request.OkHttpRequest;
import com.gxut.bitliker.usoftchinauu.db.dao.UserDao;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.network.interceptor.CommonHttpBoyInterceptor;
import com.gxut.bitliker.usoftchinauu.network.interceptor.LogInterceptor;


/**
 * Created by Bitliker on 2017/6/20.
 */

public class AppConfig extends BaseApplication {
    private static AppConfig api;

    public static AppConfig api() {
        return api;
    }

    private User loginUser;

    public User getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
        if (loginUser != null) {
            UrlHelper.api().setBaseUrl(loginUser.getBaseUrl());
            BroadcastManager.sendLocalBroadcast(
                    new Intent(BroadcastManager.USER_CHANGE)
                            .putExtra(BroadcastManager.MODEL_KEY, loginUser));
        }
    }

    @Override
    public void initConfig() throws Exception {
        api = this;
        setLoginUser(UserDao.api().getLoginUser());
        initLocation();
        initNetWork();
    }

    private void initNetWork() {
        HttpClient.init(new OkHttpRequest.Builder()
                .addInterceptors(new CommonHttpBoyInterceptor())
                .addInterceptors(new LogInterceptor())
                .builder());
    }

    private void initLocation() {
        SDKInitializer.initialize(this);
        LocationHelper.api();
    }
}
