package com.gxut.bitliker.usoftchinauu.db;

/**
 * Created by Bitliker on 2017/6/20.
 */

public class UserDBTable {
    public static String USER_TABLE = "user";
    public static String USER_TABLE_CONTENT = "CREATE TABLE " + USER_TABLE + "("
            + "id integer UNIQUE,"//id  唯一不可重复
            + "account  varchar(20),"//员工编号
            + "password  varchar(20),"//账套
            + "emName  varchar(20),"//日期  yyyy-MM-dd
            + "emCode  varchar(10),"//日期  yyyy-MM-dd
            + "company  varchar(20),"//日期  yyyy-MM-dd
            + "masterName  varchar(20),"//日期  yyyy-MM-dd
            + "master  varchar(20),"//日期  yyyy-MM-dd
            + "baseUrl  varchar(50),"//日期  yyyy-MM-dd
            + "sessionId  varchar(50)"//日期  yyyy-MM-dd
            + ")";

}