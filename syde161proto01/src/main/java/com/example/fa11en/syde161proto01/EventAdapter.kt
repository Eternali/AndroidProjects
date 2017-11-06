package com.example.fa11en.syde161proto01

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class EventAdapter (val ctx: Context, var resource: Int, var events: MutableList<UserEvent>)
        : ArrayAdapter<UserEvent> (ctx, resource, events) {

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        if (convertView == null) {
            val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val eventView = EventListView(ctx)

            // get data from event layout
            val dateTime = eventView.findViewById<TextView>(R.id.eventTime)
            val title = eventView.findViewById<TextView>(R.id.eventTitle)
            val desc = eventView.findViewById<TextView>(R.id.eventDesc)

            // since not all events are shown at once, we can't used the passed position argument
            title.text = events[position].title
            desc.text = events[position].desc

            return eventView
        }

        return convertView
    }

}