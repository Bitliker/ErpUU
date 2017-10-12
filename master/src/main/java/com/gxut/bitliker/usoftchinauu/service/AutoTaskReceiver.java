package com.gxut.bitliker.usoftchinauu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;

;

/**
 * Created by Bitliker on 2017/6/26.
 */

public class AutoTaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SettingSp.api().getBoolean(SettingSp.AUTO_TASK, false))
            return;
        String action = intent.getAction();
        LogUtil.i("onReceive=" + action);
        AutoTaskService.api().start();
    }

}
