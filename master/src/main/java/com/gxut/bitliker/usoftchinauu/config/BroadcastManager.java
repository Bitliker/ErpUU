package com.gxut.bitliker.usoftchinauu.config;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.gxut.bitliker.baseutil.util.Utils;

/**
 * Created by Bitliker on 2017/6/22.
 */

public class BroadcastManager {
    public static final String USER_CHANGE = "APP_USER_CHANGE";
    public static final String LOCATION_CHANGE = "APP_LOCATION_CHANGE";
    public static final String MODEL_KEY = "MODEL_KEY";

    public static void sendLocalBroadcast(Intent intent) {
        if (intent == null) return;
        LocalBroadcastManager.getInstance(Utils.getContext()).sendBroadcast(intent);
    }

    public static void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        LocalBroadcastManager.getInstance(Utils.getContext()).registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(Utils.getContext()).unregisterReceiver(receiver);
    }


}
