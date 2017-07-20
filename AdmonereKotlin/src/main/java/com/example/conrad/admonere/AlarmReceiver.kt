package com.example.conrad.admonere

// import required libraries
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.NotificationCompat
import android.telephony.SmsManager

class AlarmReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        // get data sent with intent
        val date : String?
        val time : String?
        val name : String?
        val number : String? // MAKE SURE SAVING AND GETTING REMINDERS AFTER EVERYTHING
        val msg : String?
        val index : Int?
        val maxReminds : Int?
        val curReminds :Int?
        val reminds : ArrayList<Reminder>?
        if (intent != null && context != null) {

            index = intent.getIntExtra("index", 0)
            reminds = getReminders(context, filename)
        } else return

        try {
            // check if we can still send reminders
            if (reminds[index].numReminds <= 0) {
                // cancel the alarm manager and exit

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