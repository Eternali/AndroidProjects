package com.example.conrad.admonerekt

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager

// Broadcast receiver that will launch when the alarm (created in EditActivity) triggers
class AlarmReceiver : BroadcastReceiver() {

    // Data to alert the user to
    internal var number: String = ""
    internal var msg: String = ""

    /**
     * Called when the BroadcastReceiver receives a broadcast
     * pre: the correct parameters are passed
     * post: an sms message will be sent and the user will be alerted.
     * @param context
     * *
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {

        // Get the data sent with this intent
        number = intent.getStringExtra("number")
        msg = intent.getStringExtra("message")

        // Send the actual text message
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, msg, null, null)

            // Build and show a notification that the text message has been sent
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Admonere Sent Reminder Alert")
                    .setContentText("Sent reminder to " + number)
                    .setTicker("Alert") as NotificationCompat.Builder
            val notimgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notimgr.notify(0, builder.build())
        } catch (e: Exception) {
            // Tell the user it has failed
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Admonere Error Reminder Alert")
                    .setContentText("SMS reminder failed to " + number)
                    .setTicker("Alert") as NotificationCompat.Builder
            val notimgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notimgr.notify(0, builder.build())
        }

    }

}
