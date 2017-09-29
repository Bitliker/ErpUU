package com.gxut.bitliker.usoftchinauu.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.ui.activity.WorkActivity;

/**
 * Created by Bitliker on 2017/8/7.
 */

public class NotificationUtil {


    public static final void sendNotification(String message) {
        NotificationManager manager = (NotificationManager) AppConfig.api().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AppConfig.api());
        Intent intent = new Intent(AppConfig.api(), WorkActivity.class);
        PendingIntent pi = PendingIntent.getActivities(AppConfig.api(), 0, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("UU 提示");
        mBuilder.setContentText(message);
        Notification notification=  mBuilder.build();
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        manager.notify(10, notification);
    }

    public static final void sendNotification(String title, String message) {

    }

    public static final void sendNotification(String title, String message, int groupID) {

    }

}
