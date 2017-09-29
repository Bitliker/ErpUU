package com.gxut.bitliker.usoftchinauu.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gxut.bitliker.baseutil.util.Utils;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class UserDBHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String USERDB_NAME = "user";
    private static UserDBHelper instance;

    public static UserDBHelper getInstance() {
        if (instance == null) {
            synchronized (UserDBHelper.class) {
                if (instance == null)
                    instance = new UserDBHelper();
            }
        }
        return instance;
    }

    private UserDBHelper() {
        super(Utils.getContext(), USERDB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDBTable.USER_TABLE_CONTENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserDBTable.USER_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
