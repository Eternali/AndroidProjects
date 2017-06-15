package com.example.conrad.admonere

// import required libraries
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import java.util.ArrayList
import java.util.Calendar

class BootReceiver : BroadcastReceiver () {

    // called when the broadcast receiver gets a broadcast
    override fun onReceive(context: Context?, intent: Intent?) {
        // create reminders from saved xml file and log
        Log.d("BootReceiver", "onReceive called")
        reminders = ArrayList<Reminder>()
        try {
            if (context != null) reminders = getReminders(context, filename)
            else throw NullPointerException()

            // for each reminder set an alarm manager that will trigger alarm receiver
            (reminders as ArrayList<Reminder>).forEach {
                val calendar : Calendar = Calendar.getInstance()
                calendar.set(it.date[2].toInt(), it.date[1].toInt(), it.date[0].toInt(),
                        it.time[0].toInt(), it.time[1].toInt(), 0)
                val almMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val almIntent = Intent(context, AlarmReceiver::class.java)
                almIntent.putExtra("number", it.number)
                almIntent.putExtra("message", it.message)
                val pendIntent = PendingIntent.getBroadcast(context
                        , (reminders as ArrayList<Reminder>).indexOf(it), almIntent, 0)
                almMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendIntent)

            }
        } catch (ne : NullPointerException) {
            ne.printStackTrace()
            Log.e("BootRecevier", "Null pointer called")
        }
    }

}