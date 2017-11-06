package com.example.fa11en.syde161proto01

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView


class DayFragment : Fragment () {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        if (inflater != null && container != null) {
            val view: View = inflater.inflate(R.layout.day_fragment_layout, null)

            val dayListView: ListView = view.findViewById(R.id.dayView)
            dayListView.adapter = EventAdapter(activity, 0, events)

            return view
        }

        return View(activity)
    }

}