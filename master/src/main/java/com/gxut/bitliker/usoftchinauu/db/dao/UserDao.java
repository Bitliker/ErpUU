package com.gxut.bitliker.usoftchinauu.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;
import com.gxut.bitliker.usoftchinauu.db.UserDBTable;
import com.gxut.bitliker.usoftchinauu.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class UserDao extends Dao<User> {
    private static UserDao api;

    public static UserDao api() {
        UserDao inst = api;
        if (inst == null) {
            synchronized (UserDao.class) {
                inst = api;
                if (inst == null) {
                    inst = new UserDao();
                    api = inst;
                }
            }
        }
        return inst;
    }



    public void deleteLoginUser(User user) {
        if (user == null) return;
        String where = "account=? and password=?";
        String[] whereArg = {user.getAccount(), user.getPassword()};
        delete(where, whereArg);
    }

    public boolean saveUser(List<User> users) {
        String where = "id=?";
        String[][] whereArgs = new String[users.size()][];
        for (int i = 0; i < users.size(); i++) {
            whereArgs[i] = new String[]{String.valueOf(users.get(i).getId())};
        }
        return insert(where, whereArgs, users);
    }

    public boolean updataUser(User user) {
        String where = "id=?";
        String[] whereArg = {String.valueOf(user.getId())};
        return update(user, where, whereArg);
    }

    public User getLoginUser() {
        User user = null;
        String select = "account=? and password=? and master=?";
        String account = SettingSp.api().getString(SettingSp.ACCOUNT);
        String password = SettingSp.api().getString(SettingSp.PASSWORD);
        String master = SettingSp.api().getString(SettingSp.MASTER);
        if (Utils.isEmpty(account) || Utils.isEmpty(password) || Utils.isEmpty(master))
            return user;
        String[] selectArg = {account, password, master};
        List<User> users = query(select, selectArg);
        if (!Utils.isEmpty(users)) {
            user = users.get(0);
        }
        return user;
    }


    public List<User> queryCompany() {
        List<User> users = new ArrayList<>();
        String account = SettingSp.api().getString(SettingSp.ACCOUNT);
        String password = SettingSp.api().getString(SettingSp.PASSWORD);
        if (!Utils.isEmpty(account) && !Utils.isEmpty(password)) {
            String select = "account=? and password=? ";
            String[] selectArg = {account, password};
            users = query(select, selectArg);
        }
        return users;
    }


    @Override
    protected String getTable() {
        return UserDBTable.USER_TABLE;
    }

    protected ContentValues getValues(User user) throws Exception {
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("account", user.getAccount());
        values.put("password", user.getPassword());
        values.put("emName", user.getEmName());
        values.put("emCode", user.getEmCode());
        values.put("company", user.getCompany());
        values.put("masterName", user.getMasterName());
        values.put("master", user.getMaster());
        values.put("baseUrl", user.getBaseUrl());
        values.put("sessionId", user.getSessionId());
        return values;
    }

    @Override
    protected User getData(Cursor c) throws Exception {
        User user = new User();
        user.setId(c.getInt(c.getColumnIndex("id")));
        user.setAccount(c.getString(c.getColumnIndex("account")));
        user.setPassword(c.getString(c.getColumnIndex("password")));
        user.setEmName(c.getString(c.getColumnIndex("emName")));
        user.setEmCode(c.getString(c.getColumnIndex("emCode")));
        user.setCompany(c.getString(c.getColumnIndex("company")));
        user.setMasterName(c.getString(c.getColumnIndex("masterName")));
        user.setMaster(c.getString(c.getColumnIndex("master")));
        user.setBaseUrl(c.getString(c.getColumnIndex("baseUrl")));
        user.setSessionId(c.getString(c.getColumnIndex("sessionId")));
        return user;
    }

}
