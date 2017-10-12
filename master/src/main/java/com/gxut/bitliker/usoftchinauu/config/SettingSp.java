package com.gxut.bitliker.usoftchinauu.config;

import com.gxut.code.baseutil.util.SpUtil;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class SettingSp extends SpUtil {
    private static SettingSp api;


    public static SettingSp api() {
        if (api == null) {
            synchronized (SettingSp.class) {
                if (api == null) api = new SettingSp();
            }
        }
        return api;
    }

    @Override
    protected String getName() {
        return "setting";
    }

    public static final String VERSION_CODE = "versionCode";//版本号
    public static final String ACCOUNT = "account";//账号
    public static final String PASSWORD = "password";//密码
    public static final String MASTER = "master";//当前选择账套
    public static final String AUTO_TASK = "autoTask";//当前选择账套
}
