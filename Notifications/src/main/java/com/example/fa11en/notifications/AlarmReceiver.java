package com.example.fa11en.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {

//        Intent service = new Intent(context, alarmService.class);
//        context.startService(service);
        String msg = intent.getStringExtra("message");

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Notification")
                .setContentText(msg)
                .setTicker("Alert");
        NotificationManager notimgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notimgr.notify(0, builder.build());
    }

}
