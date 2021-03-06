package com.example.conrad.admonere

// import required libraries
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager
import android.util.Log

class AlarmReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        // get data sent with intent
        val index : Int?
        val reminds : ArrayList<Reminder>?
        if (intent == null || context == null) return

        index = intent.getIntExtra("index", 0)
        reminds = getReminders(context, filename)
        Log.i("Alarm called", "The alarm was called")

        try {
            // check if we can still send reminders
            if (reminds[index].numReminds <= 0) {
                // cancel the alarm manager and exit
                val alarmIntent = PendingIntent.getBroadcast(context, index, intent, 0)
                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(alarmIntent)
                return
            }
            // send user to edit activity of the reminder that was sent
            val notiIntent : Intent = Intent(context, EditActivity::class.java)
            notiIntent.putExtra("index", index)
            val notiPendIntent : PendingIntent =
                    PendingIntent.getActivity(context, index, notiIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT)

            val smsMgr : SmsManager = SmsManager.getDefault()
            smsMgr.sendTextMessage(reminds[index].number, null, reminds[index].message, null, null)

            // build notification to show user that message has been sent
            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentTitle("Admonere Sent Reminder Alert")
                    .setContentText("Sent reminder to: ${reminds[index].name}")
                    .setTicker("Alert")
                    .setContentIntent(notiPendIntent)
                    .setAutoCancel(true)
            val notiMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notiMgr.notify(0, builder.build())

            // when we're done decrement the numReminds and save it
            reminds[index].numReminds--
            saveReminders(context, filename, reminds)

        } catch (ne : NullPointerException) {
            // tell user notification has failed
            ne.printStackTrace()
            displayWarning(context)
        }

    }

}