package com.gxut.bitliker.usoftchinauu.util;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;

/**
 * Created by Bitliker on 2017/8/7.
 */

public class LocalBroadcastHelper {

    public static void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(AppConfig.api()).sendBroadcast(intent);
    }

    public static void registerBroadcast(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(AppConfig.api()).registerReceiver(receiver, filter);
    }

    public static void registerBroadcast(BroadcastReceiver receiver, String... action) {
        IntentFilter filter = new IntentFilter();
        if (!Utils.isEmpty(action)) {
            for (String a : action)
                filter.addAction(a);
        }
        LocalBroadcastManager.getInstance(AppConfig.api()).registerReceiver(receiver, filter);
    }

    public static void unregisterBroadcast(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(AppConfig.api()).unregisterReceiver(receiver);
    }


}
