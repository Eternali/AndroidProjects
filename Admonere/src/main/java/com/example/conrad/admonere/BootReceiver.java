package com.example.conrad.admonere;

// Import required libraries
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

// This broadcast receiver activates when the android device boots
public class BootReceiver extends BroadcastReceiver {

    /**
     * Called when the broadcast receiver receives a broadcast
     * pre: the correct parameters are passed
     * post: the alarm managers will be reset for every reminder
     * @param context
     * @param intent
     */
    @Override
    public void onReceive (Context context, Intent intent) {
        // create the reminders from the saved xml file and log
        Log.d("BootReceiver", "onReceive called");
        MainActivity.reminders = new ArrayList<>();
        MainActivity.getReminders(context, MainActivity.filename, MainActivity.reminders);
        // for each reminder set a alarm manager that will trigger the alarmReceiver at the specified time
        for (Reminder r : MainActivity.reminders) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(r.date[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(r.date[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(r.date[0]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(r.time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(r.time[1]));
            calendar.set(Calendar.SECOND, 0);

            AlarmManager almManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent almIntent = new Intent(context, AlarmReceiver.class);
            almIntent.putExtra("number", r.number);
            almIntent.putExtra("message", r.message);
            PendingIntent finIntent = PendingIntent.getBroadcast(context,
                                                                 MainActivity.reminders.indexOf(r),
                                                                 almIntent, 0);
            almManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), finIntent);
        }
    }
}
