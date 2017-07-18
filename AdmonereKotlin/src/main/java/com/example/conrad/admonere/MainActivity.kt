package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.util.Xml
import android.view.*
import android.widget.ListView
import android.widget.Toast

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


// package wide arrayList to hold reminders and filename where they are stored across app restarts
internal var reminders : ArrayList<Reminder>? = null
internal var filename : String = "reminders.xml"


// gets the data stored in fname and returns it in a arraylist<Reminder>
internal fun getReminders (ctx : Context, fname : String) : ArrayList<Reminder> {
    // temporary variables to hold the reminders and data
    val reminds : ArrayList<Reminder> = ArrayList()
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
        // used to determine location in XML file (i.e. current tag type we're parsing)
        eventType = xpp.eventType
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
        Log.i("XML Read Error", "Xml pull parser exception raised")
        return reminds
    }

    // initialize all information variables
    var dates = ""
    var numReminds = 0
    var time = ""
    var contact = ""
    var number = ""
    var message = ""
    var text = ""
    // look through the XML file until the end is reached
    while (eventType != XmlPullParser.END_DOCUMENT) {
        try {
            // get the current name of the tag we're on
            val name = xpp.name
            // if we're on a text tag then store the text in a temporary variable until we reach the end tag
            when (eventType) {
                XmlPullParser.START_TAG -> {
                }
                XmlPullParser.TEXT -> text = xpp.text
                XmlPullParser.END_TAG ->
                    // get information from the tag
                    when (name) {
                        "dates" -> dates = text
                        "numReminds" -> numReminds = text.toInt()
                        "time" -> time = text
                        "name" -> contact = text
                        "number" -> number = text
                        "message" -> message = text
                        "reminder" -> reminds.add(
                                Reminder(dates.split(",").toTypedArray(), numReminds,
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
    // create the XML serializer and writer objects
    val xmlSerial = Xml.newSerializer()
    val writer = StringWriter()
    xmlSerial.setOutput(writer)
    xmlSerial.startDocument("UTF-8", null)
    xmlSerial.startTag(null, "reminders")
    // for each reminder write its data into a XML structure
    for ((dates, numReminds, time, name, number, message) in reminds) {
        xmlSerial.startTag(null, "reminder")
        xmlSerial.startTag(null, "date")
        xmlSerial.text(TextUtils.join(",", dates))
        xmlSerial.endTag(null, "date")
        xmlSerial.startTag(null, "numReminds")
        xmlSerial.text(numReminds.toString())
        xmlSerial.endTag(null, "numReminds")
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
    // end, write to document and close streams
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

// order the reminders according to order: true = old to new; false = new to old
// NOTE this is a custom sorting function because I'm sorting a custom object (based on ____ sort method)
internal fun orderReminders (reminds : ArrayList<Reminder>, order : Boolean) {
    for (i in 0..reminds.size-1) {
        val cal1 = Calendar.getInstance()
        val tmp1 = reminds[i]
        cal1.set(Calendar.YEAR, tmp1.dates[0][2].toInt())
        cal1.set(Calendar.MONTH, tmp1.dates[0][1].toInt())
        cal1.set(Calendar.DAY_OF_MONTH, tmp1.dates[0][0].toInt())
        cal1.set(Calendar.HOUR_OF_DAY, tmp1.time[0].toInt())
        cal1.set(Calendar.MINUTE, tmp1.time[1].toInt())
        cal1.set(Calendar.SECOND, 0)
        for (j in i + 1..reminds.size - 1) {
            val cal2 = Calendar.getInstance()
            val tmp2 = reminds[j]
            cal2.set(Calendar.YEAR, tmp2.dates[0][2].toInt())
            cal2.set(Calendar.MONTH, tmp2.dates[0][1].toInt())
            cal2.set(Calendar.DAY_OF_MONTH, tmp2.dates[0][0].toInt())
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

// check if the theme needs to change (by getting it from shared preferences) and if so apply it and restart the activity
//
// NOTE the functionality of this function is currently disabled because it causes a massive memory leak!
internal fun setTheme (ctx : Context) {
    // get current theme and the desired theme
    val currentTheme: TypedValue = TypedValue()
    ctx.theme.resolveAttribute(R.attr.themeName, currentTheme, true)

    // get the settings shared preferences
    val sharedPref : SharedPreferences = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // get the theme, default to light with no navbar customization
    val theme : String = sharedPref.getString(ctx.getString(R.string.theme), ctx.getString(R.string.lightno))
    // if we need to change the theme then do so
    if (currentTheme.string != theme) {
        when (theme) {
            ctx.getString(R.string.lightno) -> ctx.setTheme(R.style.AppTheme)
            ctx.getString(R.string.lightyes) -> ctx.setTheme(R.style.AppThemeYes)
            ctx.getString(R.string.darkno) -> ctx.setTheme(R.style.AppThemeDark)
            ctx.getString(R.string.darkyes) -> ctx.setTheme(R.style.AppThemeDarkYes)
        }
    }

}

// This is a general function to make long toasts to display to the user if/when things go wrong
internal fun displayWarning (ctx : Context, warning : String = "A critical error occurred!") =
        Toast.makeText(ctx, warning, Toast.LENGTH_LONG).show()


/**
 * Main activity that allows user to see previously created reminders, edit reminders, and
 * create new ones
 */
class MainActivity : AppCompatActivity () {

    // package wide variables for use in other classes
    internal var context : Context? = null
    internal var adapter : RemindersArrayAdapter? = null
    internal var remindersList : ListView? = null
    internal var addButton : FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the theme, call the superclass method and set the view
        // note the memory leak could be caused by: recreate being called from onCreate() or
        // recreate always being called (setTheme always returning true)
        setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val viewPager = findViewById(R.id.pager) as ViewPager
        // get context so other methods can use it
        context = applicationContext
//        viewPager.adapter = RemindersTimeAdapter(context!!)

        // NOTE we'll assert that context will never be null which should occur but is not an ideal solution
        reminders = getReminders(context!!, filename)
        // create adapter that presents users with the reminders in a listview
        adapter = RemindersArrayAdapter(this, 0, reminders as ArrayList<Reminder>)
        remindersList = findViewById(R.id.remindersList) as ListView
        (remindersList as ListView).adapter= adapter

        // FAB that lets users add reminders
        addButton = findViewById(R.id.addButton) as FloatingActionButton
        (addButton as FloatingActionButton).setOnClickListener {
            val intent : Intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    // creates the overflow menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // handles option menu item selection
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            R.id.action_settings -> {
                val setIntent : Intent = Intent(this, SettingsActivity::class.java)
                startActivity(setIntent)
            }
        }

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