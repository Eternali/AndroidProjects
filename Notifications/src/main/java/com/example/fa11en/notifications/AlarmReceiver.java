package com.example.fa11en.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    String number;
    String msg;

    @Override
    public void onReceive (Context context, Intent intent) {

        number = intent.getStringExtra("number");
        msg = intent.getStringExtra("message");

//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(number, null, msg, null, null);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Admonere Sent Reminder Alert")
                .setContentText("Sent reminder to " + number)
                .setTicker("Alert");
        NotificationManager notimgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notimgr.notify(0, builder.build());
    }

}
