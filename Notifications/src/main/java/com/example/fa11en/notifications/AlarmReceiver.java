package com.example.fa11en.notifications;

// Import required libraries
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;

// Broadcast receiver that will launch when the alarm (created in EditActivity) triggers
public class AlarmReceiver extends BroadcastReceiver {

    // Data to alert the user to
    String number;
    String msg;

    /**
     * Called when the BroadcastReceiver receives a broadcast
     * pre: the correct parameters are passed
     * post: an sms message will be sent and the user will be alerted.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive (Context context, Intent intent) {

        // Get the data sent with this intent
        number = intent.getStringExtra("number");
        msg = intent.getStringExtra("message");

        // Send the actual text message
        try {
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(number, null, msg, null, null);

            // Build and show a notification that the text message has been sent
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Admonere Sent Reminder Alert")
                    .setContentText("Sent reminder to " + number)
                    .setTicker("Alert");
            NotificationManager notimgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notimgr.notify(0, builder.build());
        } catch (Exception e) {
            // Tell the user it has failed
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Admonere Error Reminder Alert")
                    .setContentText("SMS reminder failed to " + number)
                    .setTicker("Alert");
            NotificationManager notimgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notimgr.notify(0, builder.build());
        }

    }

}
