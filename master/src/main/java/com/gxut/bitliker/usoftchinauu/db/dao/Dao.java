package com.gxut.bitliker.usoftchinauu.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.db.UserDBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitliker on 2017/7/6.
 */

public abstract class Dao<T> {


    public boolean insert(String where, String[][] whereArgss, List<T> datas) {
        if (Utils.isEmpty(datas)) return false;
        long i = 0;
        try {
            SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
            db.beginTransaction();
            for (int j = 0; j < datas.size(); j++) {
                T t = datas.get(j);
                i = db.insert(getTable(), null, getValues(t));
                if (i < 0 && !Utils.isEmpty(where) && !Utils.isEmpty(whereArgss)) {
                    i = db.update(getTable(), getValues(t), where, whereArgss[j]);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } finally {
            UserDBManager.getInstance().closeDatabase();
            return i > 0;
        }
    }

    public boolean insert(String where, String[] whereArgs, T t) {
        if (Utils.isEmpty(t)) return false;
        long i = 0;
        try {
            SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
            i = db.insert(getTable(), null, getValues(t));
            if (i < 0) {
                i = db.update(getTable(), getValues(t), where, whereArgs);

            }
        } finally {
            UserDBManager.getInstance().closeDatabase();
            return i > 0;
        }
    }

    public boolean delete(String where, String[] whereArgs) {
        if (Utils.isEmpty(where) || Utils.isEmpty(whereArgs)) return false;
        long i = 0;
        try {
            SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
            i = db.delete(getTable(), where, whereArgs);
        } finally {
            UserDBManager.getInstance().closeDatabase();
            return i > 0;
        }
    }


    public boolean update(T t, String where, String[] whereArgs) {
        if (Utils.isEmpty(t)) return false;
        long i = 0;
        try {
            SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
            i = db.update(getTable(), getValues(t), where, whereArgs);
        } finally {
            UserDBManager.getInstance().closeDatabase();
            return i > 0;
        }
    }

    public List<T> query(String where, String[] whereArgs) {
        return query(null, where, whereArgs, null, null, null);
    }

    public List<T> query(String[] columns, String where, String[] whereArgs) {
        return query(columns, where, whereArgs, null, null, null);
    }

    public List<T> query(String[] columns, String where, String[] whereArgs, String orderBy) {
        return query(columns, where, whereArgs, null, null, orderBy);
    }

    public List<T> query(String[] columns, String where, String[] whereArgs, String groupBy, String having, String orderBy) {
        List<T> datas = new ArrayList<T>();
        try {
            SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
            Cursor c = db.query(getTable(), columns, where, whereArgs, groupBy, having, orderBy);
            while (c.moveToNext()) {
                T t = getData(c);
                if (!Utils.isEmpty(t))
                    datas.add(t);
            }
        } finally {
            UserDBManager.getInstance().closeDatabase();
            return datas;
        }
    }

    protected boolean clear(){
        SQLiteDatabase db = UserDBManager.getInstance().openDatabase();
        db.delete(getTable(),null,null);
        UserDBManager.getInstance().closeDatabase();
        return true;
    }

    protected abstract String getTable();

    protected abstract ContentValues getValues(T t) throws Exception;

    protected abstract T getData(Cursor c) throws Exception;

}
