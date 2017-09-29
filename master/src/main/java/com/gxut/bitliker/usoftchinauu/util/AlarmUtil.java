package com.gxut.bitliker.usoftchinauu.util;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.gxut.bitliker.baseutil.util.LogUtil;
import com.gxut.bitliker.baseutil.util.SystemUtil;
import com.gxut.bitliker.baseutil.util.TimeUtil;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.service.AutoTaskReceiver;

/**
 * AlarmManager.ELAPSED_REALTIME 表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3；
 * AlarmManager.ELAPSED_REALTIME_WAKEUP 表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
 * AlarmManager.RTC 表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
 * AlarmManager.RTC_WAKEUP 表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0；
 * AlarmManager.POWER_OFF_WAKEUP 表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
 * Created by Bitliker on 2017/8/7.
 */

public class AlarmUtil {
    public static final int AUTOTASK_REQUESTCODE = 0x2231;
    public static final String AUTOTASK_ACTION = "AUTOTASK_ACTION";


    @TargetApi(19)
    public static void startAlarm(int requestCode, String action, long nextAlarmTimeMillis) {
        AlarmManager manager = (AlarmManager) AppConfig.api().getSystemService(Context.ALARM_SERVICE);
        LogUtil.i("nextAlarmTimeMillis="+ TimeUtil.long2Str(nextAlarmTimeMillis,"yyyy-MM-dd HH:mm:ss"));
        if (SystemUtil.getOsVersion() >= 19) {
            manager.setExact(AlarmManager.RTC_WAKEUP
                    , nextAlarmTimeMillis
                    , getPendingIntent(requestCode, action));
        } else {
            manager.set(AlarmManager.RTC_WAKEUP
                    , nextAlarmTimeMillis
                    , getPendingIntent(requestCode, action));
        }
    }

    public static void cancelAlarm(int id, String action) {
        LogUtil.i("cancelAlarm");
        AlarmManager manager = (AlarmManager) AppConfig.api().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(getPendingIntent(id, action));
    }

    private static PendingIntent getPendingIntent(int id, String action) {
        Intent intent = new Intent(AppConfig.api(), AutoTaskReceiver.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(AppConfig.api(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
