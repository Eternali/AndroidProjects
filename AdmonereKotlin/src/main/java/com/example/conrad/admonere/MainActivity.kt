package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.text.TextUtils
import android.util.Log
import android.util.Xml
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewConfiguration
import android.widget.ListView

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.io.StringWriter
import java.util.ArrayList
import java.util.Calendar


// package wide arrayList to hold reminders
internal var reminders : ArrayList<Reminder>? = null
internal var filename : String = "reminders.xml"


// gets the data stored in fname and returns it in a arraylist<Reminder>
internal fun getReminders (ctx : Context, fname : String) : ArrayList<Reminder> {
    var reminds : ArrayList<Reminder> = ArrayList()
    val data: String
    // get the data from the XML file and read it into a string
    try {
        val fis = ctx.openFileInput(fname)
        val isr = InputStreamReader(fis)
        val inBuff = CharArray(fis.available())
        isr.read(inBuff)
        data = String(inBuff)
        Log.i("Read data", data)
        isr.close()
        fis.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        Log.i("Read Error", "Unable to find file")
        return reminds
    } catch (e: IOException) {
        e.printStackTrace()
        Log.i("Read Error", "IO Exception")
        return reminds
    }

    // start parsing the XML data from the string (data)
    val factory: XmlPullParserFactory
    val xpp: XmlPullParser
    var eventType: Int
    try {
        factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        xpp = factory.newPullParser()
        xpp.setInput(StringReader(data))
        // used to determine location in XML file
        eventType = xpp.eventType
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
        Log.i("XML Read Error", "Xml pull parser exception raised")
        return reminds
    }

    // initialize all information variables
    var date = ""
    var time = ""
    var contact = ""
    var number = ""
    var message = ""
    var text = ""
    // look through the XML file until the end is reached
    while (eventType != XmlPullParser.END_DOCUMENT) {
        try {
            val name = xpp.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                }
                XmlPullParser.TEXT -> text = xpp.text
                XmlPullParser.END_TAG ->
                    // get information from the tag
                    when (name) {
                        "date" -> date = text
                        "time" -> time = text
                        "name" -> contact = text
                        "number" -> number = text
                        "message" -> message = text
                        "reminder" -> reminds.add(Reminder(date.split("/").toTypedArray(),
                                time.split(":").toTypedArray(), contact, number, message))
                    }
            }
            eventType = xpp.next()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            Log.i("XML Read Error", "Xml pull parser exception raised")
            return reminds
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("XML Read Error", "IO exception raised")
            return reminds
        }
    }
    return reminds
}

// takes the data stored in reminds and saves it to an xml file fname
internal fun saveReminders (ctx : Context, fname : String, reminds : ArrayList<Reminder>) = try {
    // create file if doesn't exist
    // (FileOutputStream should create it automatically but just to be safe)
    val filepath = ctx.filesDir.path + "/" + fname
    val saveFile = File(filepath)
    saveFile.createNewFile()
    //            FileOutputStream fos = new FileOutputStream(saveFile);
    val fos = ctx.openFileOutput(fname, Context.MODE_PRIVATE)
    // create the serializer and writer objects
    val xmlSerial = Xml.newSerializer()
    val writer = StringWriter()
    xmlSerial.setOutput(writer)
    xmlSerial.startDocument("UTF-8", null)
    xmlSerial.startTag(null, "reminders")
    // for each reminder write its data into a XML structure
    for ((date, time, name, number, message) in reminds) {
        xmlSerial.startTag(null, "reminder")
        xmlSerial.startTag(null, "date")
        xmlSerial.text(TextUtils.join("/", date))
        xmlSerial.endTag(null, "date")
        xmlSerial.startTag(null, "time")
        xmlSerial.text(TextUtils.join(":", time))
        xmlSerial.endTag(null, "time")
        xmlSerial.startTag(null, "name")
        xmlSerial.text(name)
        xmlSerial.endTag(null, "name")
        xmlSerial.startTag(null, "number")
        xmlSerial.text(number)
        xmlSerial.endTag(null, "number")
        xmlSerial.startTag(null, "message")
        xmlSerial.text(message)
        xmlSerial.endTag(null, "message")
        xmlSerial.endTag(null, "reminder")
    }
    // end and write to document and close streams
    xmlSerial.endTag(null, "reminders")
    xmlSerial.endDocument()
    xmlSerial.flush()
    val dWrite = writer.toString()
    fos.write(dWrite.toByteArray())
    fos.close()
} catch (e: IllegalArgumentException) {
    Log.i("XML Write Error", "Illegal argument raised")
    e.printStackTrace()
} catch (e: IllegalStateException) {
    Log.i("XML Write Error", "Illegal state of the document/streams")
    e.printStackTrace()
} catch (e: IOException) {
    Log.i("XML Write Error", "IO exception raised")
    e.printStackTrace()
}

internal fun orderReminders (reminds : ArrayList<Reminder>, order : Boolean) {
    if (order) {
        for (i in 0..reminds.size-1) {
            var cal1 = Calendar.getInstance()
            var tmp1 = reminds.get(i)
            cal1.set(Calendar.YEAR, tmp1.date[2].toInt())
            cal1.set(Calendar.MONTH, tmp1.date[1].toInt())
            cal1.set(Calendar.DAY_OF_MONTH, tmp1.date[0].toInt())
            cal1.set(Calendar.HOUR_OF_DAY, tmp1.time[0].toInt())
            cal1.set(Calendar.MINUTE, tmp1.time[1].toInt())
            cal1.set(Calendar.SECOND, 0)
            for (j in i + 1..reminds.size - 1) {
                val cal2 = Calendar.getInstance()
                val tmp2 = reminds[j]
                cal2.set(Calendar.YEAR, tmp2.date[2].toInt())
                cal2.set(Calendar.MONTH, tmp2.date[1].toInt())
                cal2.set(Calendar.DAY_OF_MONTH, tmp2.date[0].toInt())
                cal2.set(Calendar.HOUR_OF_DAY, tmp2.time[0].toInt())
                cal2.set(Calendar.MINUTE, tmp2.time[1].toInt())
                cal2.set(Calendar.SECOND, 0)
                if (order && cal1 > cal2) {
                    reminds[i] = tmp2
                    reminds[j] = tmp1
                }
                if (!order && cal1 < cal2) {
                    reminds[i] = tmp2
                    reminds[j] = tmp1
                }
            }
        }
    }
}

/**
 * Main activity that allows user to see previously created reminders, edit reminders, and
 * create new ones
 */
class MainActivity : AppCompatActivity () {

    internal var context : Context? = null
    internal var adapter : RemindersArrayAdapter? = null
    internal var remindersList : ListView? = null
    internal var addButton : FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // call superclass' method and set the view to activity_main.xml
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val viewPager = findViewById(R.id.pager) as ViewPager
        // get context so other methods can use it
        context = applicationContext
//        viewPager.adapter = RemindersTimeAdapter(context!!)

        reminders = getReminders(context!!, filename)
        // create adapter that presents users with the reminders in a listview
        adapter = RemindersArrayAdapter(this, 0, reminders as ArrayList<Reminder>)
        remindersList = findViewById(R.id.remindersList) as ListView
        (remindersList as ListView).adapter= adapter

        addButton = findViewById(R.id.addButton) as FloatingActionButton
        (addButton as FloatingActionButton).setOnClickListener {
            val intent : Intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    //
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // when activity resumes reload the arrayadapter with possible new data
    override fun onResume() {
        super.onResume()
        if (adapter != null) (adapter as RemindersArrayAdapter).notifyDataSetChanged()
    }

    // when the user leaves the activity save the current reminders
    override fun onPause() {
        super.onPause()
        saveReminders(context!!, filename, reminders!!)
    }

    // when the activity is destroyed save the current reminders
    override fun onDestroy() {
        super.onDestroy()
        saveReminders(context!!, filename, reminders!!)
    }

}