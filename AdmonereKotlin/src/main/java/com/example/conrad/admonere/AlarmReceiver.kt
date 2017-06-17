package com.example.conrad.admonere

// import required libraries
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager

class AlarmReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        // get data sent with intent
        var date : String?
        var time : String?
        var name : String?
        var number : String?
        var msg : String?
        var index : Int?
        if (intent != null) {
            date = intent.getStringExtra("date")
            time = intent.getStringExtra("time")
            name = intent.getStringExtra("name")
            number = intent.getStringExtra("number")
            msg = intent.getStringExtra("message")
            index = intent.getIntExtra("index", 0)
        } else return

        try {
            // send user to edit activity of the reminder that was sent
            val notiIntent : Intent = Intent(context, EditActivity::class.java)
            notiIntent.putExtra("date", date)
            notiIntent.putExtra("time", time)
            notiIntent.putExtra("name", name)
            notiIntent.putExtra("number", number)
            notiIntent.putExtra("message", msg)
            notiIntent.putExtra("index", index)
            val notiPendIntent : PendingIntent =
                    PendingIntent.getActivity(context, index, notiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)

            val smsMgr : SmsManager = SmsManager.getDefault()
            smsMgr.sendTextMessage(number, null, msg, null, null)

            // build notification to show user that message has been sent
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentTitle("Admonere Sent Reminder Alert")
                    .setContentText("Sent reminder to: $number")
                    .setTicker("Alert")
                    .setContentIntent(notiPendIntent)
                    .setAutoCancel(true)
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