package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout


/**
 *
 */
class ReminderView (var ctx : Context) : RelativeLayout (ctx) {

    init {
        val li = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        li.inflate(R.layout.reminder_layout, this, true)
//        inflate(R.layout.reminder_layout, ctx,true)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        return false
    }

}