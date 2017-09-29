package com.gxut.bitliker.usoftchinauu.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class UserDBManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static UserDBManager instance;
    private static UserDBHelper mDatabaseHelper;
    private static SQLiteDatabase mDatabase;

    private  static synchronized void initializeInstance(UserDBHelper helper) {
        if (instance == null) {
            instance = new UserDBManager();
            mDatabaseHelper = helper;
        }
    }
   
    public static synchronized UserDBManager getInstance() {
        if (instance == null) {
            initializeInstance(UserDBHelper.getInstance());
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1 ) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
                mDatabase.close();
        }
    }
}