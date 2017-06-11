package com.example.conrad.admonerekt

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.ArrayList
import java.util.Calendar

// This broadcast receiver activates when the android device boots and resets the alarm managers
class BootReceiver : BroadcastReceiver() {

    /**
     * Called when the broadcast receiver receives a broadcast
     * pre: the correct parameters are passed
     * post: the alarm managers will be reset for every reminder
     * @param context
     * *
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        // create the reminders from the saved xml file and log
        Log.d("BootReceiver", "onReceive called")
        MainActivity.reminders = ArrayList<Reminder>()
        getReminders(context, MainActivity.filename, MainActivity.reminders)
        // for each reminder set a alarm manager that will trigger the alarmReceiver at the specified time
        for (r in MainActivity.reminders) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, Integer.parseInt(r.date[2]))
            calendar.set(Calendar.MONTH, Integer.parseInt(r.date[1]))
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(r.date[0]))
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(r.time[0]))
            calendar.set(Calendar.MINUTE, Integer.parseInt(r.time[1]))
            calendar.set(Calendar.SECOND, 0)

            val almManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val almIntent = Intent(context, AlarmReceiver::class.java)
            almIntent.putExtra("number", r.number)
            almIntent.putExtra("message", r.message)
            val finIntent = PendingIntent.getBroadcast(context,
                    MainActivity.reminders.indexOf(r),
                    almIntent, 0)
            almManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, finIntent)
        }
    }
}
