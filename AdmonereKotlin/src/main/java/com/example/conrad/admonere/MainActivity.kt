package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.util.Xml
import android.view.View
import android.widget.ListView

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.io.StringWriter
import java.util.ArrayList
import java.util.Calendar


// package wide arrayList to hold reminders
internal var reminders : ArrayList<Reminder>? = null
internal var filename : String = "reminders.xml"

internal fun getReminders (ctx : Context, fname : String) : ArrayList<Reminder> {

}

/**
 * Main activity that allows user to see previously created reminders, edit reminders, and
 * create new ones
 */
class MainActivity : AppCompatActivity () {

    internal var context : Context? = null
    internal var adapter : RemindersArrayAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        // call superclass' method and set the view to activity_main.xml
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        // get context so other methods can use it
        context = applicationContext
        reminders = getReminders(context!!, filename)
        // create adapter that presents users with the reminders in a listview
        adapter = RemindersArrayAdapter(this, reminders)

    }
}