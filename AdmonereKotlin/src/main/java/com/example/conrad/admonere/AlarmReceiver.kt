package com.example.conrad.admonere

// import required libraries
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager

class AlarmReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        // get data sent with intent
        var number : String? = null
        var msg : String? = null
        if (intent != null) {
            number = intent.getStringExtra("number")
            msg = intent.getStringExtra("message")
        }

        try {
            val smsMgr : SmsManager = SmsManager.getDefault()
            smsMgr.sendTextMessage(number, null, msg, null, null)

            // build notification to show user that message has been sent
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Admonere Sent Reminder Alert")
                    .setContentText("Sent reminder to: $number")
                    .setTicker("Alert")
            if (context != null) {
                val notiMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notiMgr.notify(0, builder.build())
            } else {
                throw NullPointerException()
            }

        } catch (ne : NullPointerException) {
            // tell user notification has failed
            ne.printStackTrace()
        }

    }

}