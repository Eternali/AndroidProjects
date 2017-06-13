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
class RemindersArrayAdapter (var ctx : Context, var reminds : ArrayList<Reminder>)
        : ArrayAdapter<Reminder> () {

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getView(position, convertView, parent)
    }

}
