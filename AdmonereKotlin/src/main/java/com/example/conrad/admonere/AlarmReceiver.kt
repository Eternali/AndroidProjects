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
        if (intent != null) val number = intent.getStringExtra("number")
    }

}