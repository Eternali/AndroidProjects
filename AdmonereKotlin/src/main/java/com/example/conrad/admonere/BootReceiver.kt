package com.example.conrad.admonere

// import required libraries
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast

import java.util.ArrayList
import java.util.Calendar


class BootReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_BOOT_COMPLETED) {
            this.alertFailed(context!!); return }
        // create reminders from saved xml file and log
        Log.d("BootReceiver", "onReceive called")
        try {
            if (context != null) reminders = getReminders(context, filename)
            else throw NullPointerException()

            val almMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // for each reminder set an alarm manager that will trigger alarm receiver
            for (reminder in (reminders as ArrayList<Reminder>)) {
                reminder.dates.forEach {
                    val calendar = Calendar.getInstance()
                    calendar.set(it.split("/")[2].toInt(), it.split("/")[1].toInt(), it.split("/")[0].toInt(),
                            reminder.time[0].toInt(), reminder.time[1].toInt(), 0)
                    val almIntent = Intent(context, AlarmReceiver::class.java)
                    almIntent.putExtra("index", (reminders as ArrayList<Reminder>).indexOf(reminder))
                    val pendIntent = PendingIntent.getBroadcast(context,
                            (reminders as ArrayList<Reminder>).indexOf(reminder), almIntent, 0)
                    almMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY * 7, pendIntent)

//                    reminders!!.set(index, Reminder(this.getDates(calendar, dayBtnActives).toTypedArray(),
//                            numReps, time.toTypedArray(), name, phoneNo, msg))
//
//                    val almMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//                    almIntent.putExtra("number", it.number)
//                    almIntent.putExtra("message", it.message)
//                    val pendIntent = PendingIntent.getBroadcast(context
//                            , (reminders as ArrayList<Reminder>).indexOf(it), almIntent, 0)
//                    almMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendIntent)
                }
            }
        } catch (ne : NullPointerException) {
            ne.printStackTrace()
            Log.e("BootRecevier", "Null pointer called")
            alertFailed(context)
        }
    }

    fun alertFailed(ctx : Context?) {
        Log.e("BootReceiver", "Failed to initialize: Invalid action")
        Toast.makeText(ctx, "ADMONERE: Alarm manager failed to initialize", Toast.LENGTH_LONG).show()
    }

}