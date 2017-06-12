package com.example.conrad.admonerekt


// Import required libraries
import android.annotation.SuppressLint
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
 * An array adapter of type Reminder (custom object) that takes an xml file and fits the contents
 * of each object in the array to the view.
 */
open class RemindersArrayAdapter (context: Context, private val reminds: ArrayList<Reminder>)
        : ArrayAdapter<Reminder>(context, R.layout.reminder_layout, reminds) {

    // created for custom actions when superclass function called
    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        // sort the reminders according to date and time when a new one is added.
        orderReminders(reminds, true)
    }

    /**
     * Inflates the layout and sets the components of the layout to values from the array
     * pre: the array is of a proper format
     * post: returns a view for the mainActivity to display
     * @param position
     * *
     * @param convertView
     * *
     * @param parent
     * *
     * @return
     */
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var remindView: View? = null

        // inflate the remindView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        remindView = inflater.inflate(R.layout.reminder_layout, parent, false)

        // set the components of the layout to values from the reminds array
        val dateTime = remindView!!.findViewById(R.id.dateTime) as TextView
        val contactName = remindView.findViewById(R.id.contactName) as TextView
        val message = remindView.findViewById(R.id.message) as TextView
        dateTime.text = "${TextUtils.join(":", reminds[position].time)} ${TextUtils.join("/", reminds[position].date)}"
        contactName.text = reminds[position].name
        message.text = reminds[position].message

        // when the view is clicked start the EditActivity activity and give it the data required to
        // populate its fields
        remindView.setOnClickListener {
            val r = reminds[position]
            val editIntent = Intent(context, EditActivity::class.java)
            editIntent.putExtra("date", TextUtils.join("/", r.date))
            editIntent.putExtra("time", TextUtils.join(":", r.time))
            editIntent.putExtra("name", r.name)
            editIntent.putExtra("number", r.number)
            editIntent.putExtra("message", r.message)
            editIntent.putExtra("index", position)
            context.startActivity(editIntent)
        }

        return remindView
    }

}