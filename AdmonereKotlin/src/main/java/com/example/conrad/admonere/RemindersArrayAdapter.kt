package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.content.Intent
import android.text.TextUtils
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
        orderReminders(reminds, true)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // inflate the remindView
        var inflater : LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var remindView : View = inflater.inflate(R.layout.reminder_layout, parent, false)
        // set components of the layout to values from the reminds array
        val dateTime = remindView.findViewById(R.id.dateTime) as TextView
        val contactName = remindView.findViewById(R.id.contactName) as TextView
        val message = remindView.findViewById(R.id.message) as TextView
        dateTime.text = "${reminds[position].time.joinToString(":")} ${reminds[position].date.joinToString("/")}"
        contactName.text = reminds[position].name
        message.text = reminds[position].message

        // when a element is tapped, start an intent to EditActivity and put its data into it.
        remindView!!.setOnClickListener({
            val r = reminds[position]
            val editIntent = Intent(context, EditActivity::class.java)
            editIntent.putExtra("date", r.date.joinToString("/"))
            editIntent.putExtra("time", r.time.joinToString(":"))
            editIntent.putExtra("name", r.name)
            editIntent.putExtra("number", r.number)
            editIntent.putExtra("message", r.message)
            editIntent.putExtra("index", position)
            context.startActivity(editIntent)
        })

        return remindView
    }

}
