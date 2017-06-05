package com.example.fa11en.notifications;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.Calendar;

public class Reminder {

    String[] date;
    String[] time;
    String name;
    String number;
    String message;

    Calendar calendar;
    AlarmManager alarmManager;
    Intent intent;
    PendingIntent alarmIntent;

    @TargetApi(19)
    public Reminder (String[] date, String[] time, String name, String number, String message
                    , Calendar calendar, AlarmManager alarmManager, Intent intent, PendingIntent alarmIntent) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.number = number;
        this.message = message;
        this.calendar = calendar;
        this.alarmManager = alarmManager;
        this.intent = intent;
        this.alarmIntent = alarmIntent;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

}
