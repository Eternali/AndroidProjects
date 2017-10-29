package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.content.Intent
import android.support.v4.view.GestureDetectorCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList


/**
 * An array adapter of type Reminder and takes an xml file and fits the contents of each object
 * in the arraylist to the view
 */
class RemindersArrayAdapter (var ctx : Context, var resource : Int, var reminds : ArrayList<Reminder>)
        : ArrayAdapter<Reminder> (ctx, resource, reminds) {

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        orderReminders(reminds, false)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // inflate the remindView
        if (convertView == null) {
            val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val remindView: View = inflater.inflate(R.layout.reminder_layout, parent, false)
            // set components of the layout to values from the reminds array
            val dateTime = remindView.findViewById(R.id.dateTime) as TextView
            val contactName = remindView.findViewById(R.id.contactName) as TextView
            val message = remindView.findViewById(R.id.message) as TextView
            // since not all reminders are shown at once, we can't use the passed position argument
            // temporarily add 1 to the month (calendar months are indexed from 0)
            val tmpdate = reminds[position].dates[0].split("/") as ArrayList
            tmpdate[1] = (tmpdate[1].toInt() + 1).toString()
            dateTime.text = "${reminds[position].time.joinToString(":")} ${tmpdate.joinToString("/")}"
            contactName.text = reminds[position].name
            message.text = reminds[position].message

            // when a element is tapped, start an intent to EditActivity and put its data into it.
            remindView.setOnClickListener({
                val editIntent = Intent(context, EditActivity::class.java)
                editIntent.putExtra("index", position)
                context.startActivity(editIntent)
            })

            remindView.setOnTouchListener(View.OnTouchListener(view, event : Motionevent) {
                override fun onTouch () {

                }
            })
            remindView.onTouchEvent(GestureDetectorCompat(context, MainGestureDetector(MainActivity::changeTabTouch)))

            return remindView
        }

        return convertView
    }

}
