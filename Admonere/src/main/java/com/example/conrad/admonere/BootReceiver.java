package com.example.conrad.admonere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.d("BootReceiver", "onReceive called");
        for (Reminder r : MainActivity.reminders) {
            AlarmManager almManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent almIntent = new Intent(context, AlarmReceiver.class);
            almIntent.putExtra("number", r.number);
            almIntent.putExtra("message", r.message);
            PendingIntent finIntent = PendingIntent.getBroadcast(context,
                                                                 MainActivity.reminders.size(),
                                                                 almIntent, 0);
            almManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), finIntent);
        }
    }
}
