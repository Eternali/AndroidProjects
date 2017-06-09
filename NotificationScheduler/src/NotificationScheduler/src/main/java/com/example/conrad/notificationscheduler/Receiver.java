package com.example.conrad.notificationscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);

    }
}

/*
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Receiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, "Blabla", "Blablablabla", "Alert");

    }

    public void createNotification(Context context, String msg,
                                   String msgText, String msgAlert) {

        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(msg)
                .setContentText(msgText)
                .setTicker(msgAlert);

        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        showNotification(context, "Hello world.");
    }

    public void showNotification (Context context, String msg) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Title")
                .setContentText(msg);
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
//        NotificationManager notiMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//        Notification noti = new Notification.Builder(context)
//                .setSmallIcon(R.drawable.ic_stat_name)
//                .setContentTitle("NotificationScheduler")
//                .setContentText(msg)
//                .build();
//        notiMgr.notify(0, noti);
    }

}
*/
