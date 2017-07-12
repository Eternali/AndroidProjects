package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.content.Intent
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
            val convertView: View = inflater.inflate(R.layout.reminder_layout, parent, false)
            // set components of the layout to values from the reminds array
            val dateTime = convertView.findViewById(R.id.dateTime) as TextView
            val contactName = convertView.findViewById(R.id.contactName) as TextView
            val message = convertView.findViewById(R.id.message) as TextView
            // temporarily add 1 to the month (calendar months are indexed from 0)
            reminds[position].date.set(1, (reminds[position].date.get(1).toInt() + 1).toString())
            dateTime.text = "${reminds[position].time.joinToString(":")} ${reminds[position].date.joinToString("/")}"
            reminds[position].date.set(1, (reminds[position].date.get(1).toInt() - 1).toString())
            contactName.text = reminds[position].name
            message.text = reminds[position].message

            // when a element is tapped, start an intent to EditActivity and put its data into it.
            convertView.setOnClickListener({
                val r = reminds[position]
                r.date[1] = (r.date[1].toInt() + 1).toString()  // send the user formatted month
                val editIntent = Intent(context, EditActivity::class.java)
                editIntent.putExtra("date", r.date.joinToString("/"))
                editIntent.putExtra("time", r.time.joinToString(":"))
                editIntent.putExtra("name", r.name)
                editIntent.putExtra("number", r.number)
                editIntent.putExtra("message", r.message)
                editIntent.putExtra("index", position)
                context.startActivity(editIntent)
            })
        }

        return convertView!!
    }

}
