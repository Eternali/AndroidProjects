package com.example.conrad.admonere

// import required libraries
import android.view.View
import android.content.Context
import android.view.MotionEvent


/**
 *
 */
class ReminderView (var ctx : Context) : View (ctx) {

    init {
        inflate(context, R.layout.reminder_layout, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        return false
    }

}