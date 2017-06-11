package com.example.conrad.admonerekt

// Import required libraries
import android.content.Context
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

/**
 * Main activity screen that allows the user to see previously created reminders, edit reminders,
 * and create new ones.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        val filename: String = "reminders.xml"
        var context: Context? = null
        var reminders: ArrayList<Reminder> = ArrayList<Reminder>()
    }

    var adapter: RemindersArrayAdapter? = null

    /**
     * Called when the activity is created (when the app is opened) and inflates the required views.
     * pre: there is a XML view file to present to the user
     * post: important variables declared above are instantiated.
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // call super class' method and set the view to activity_main.xml
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext
        getReminders(context!!, filename, reminders)
        adapter = RemindersArrayAdapter(context, reminders)
        // declare UI elements
        var addButton: FloatingActionButton = findViewById(R.id.addButton) as FloatingActionButton
        var remindersList: ListView = findViewById(R.id.remindersList) as ListView
        remindersList.adapter = adapter

        addButton.setOnClickListener({
            // when clicked send user to the EditActivity activity
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            startActivity(intent)
        })
    }

    /**
     * Called when the activity resumes (returns from a different activity) and updates the adapter to
     * show the modified reminders ArrayList
     * pre: adapter object is previously created
     * post: the adapters contents will be updated
     */
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    /**
     * Called when the activity leaves the active screen and saves the current reminders to a xml file
     * pre: saveReminders method is created with the required parameter variables
     * post: the file specified with filename will be updated
     */
    override fun onPause() {
        super.onPause()
        saveReminders(context!!, filename, reminders)
    }

    /**
     * Called when the activity is destroyed (unloads from RAM) and saves the reminders just to be safe
     * pre: saveReminders method is created with the required parameter variables
     * post: the file specified with filename will be updated
     */
    override fun onDestroy() {
        super.onDestroy()
        saveReminders(context!!, filename, reminders)
    }
}

/**
 * Gets the data stored in fname and updates reminds with the data it receives
 * pre: required imports are satisfied
 * post: reminds array is updated.
 * @param ctx
 * *
 * @param fname
 * *
 * @param reminds
 */
fun getReminders(ctx: Context, fname: String, reminds: ArrayList<Reminder>) {

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
        return
    } catch (e: IOException) {
        e.printStackTrace()
        Log.i("Read Error", "IO Exception")
        return
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
        return
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
                    if (name == "date")
                        date = text
                    else if (name == "time")
                        time = text
                    else if (name == "name")
                        contact = text
                    else if (name == "number")
                        number = text
                    else if (name == "message")
                        message = text
                    else if (name == "reminder") reminds.add(Reminder(date.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(), time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(), contact, number, message))// if at the end of a reminder object add it to the reminds ArrayList
            }
            eventType = xpp.next()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            Log.i("XML Read Error", "Xml pull parser exception raised")
            return
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("XML Read Error", "IO exception raised")
            return
        }

    }
}

/**
 * Write the data stored in reminds to fname in xml format.
 * pre: required imports are satisfied
 * post: the contents of fname will be updated
 * @param context
 * *
 * @param fname
 * *
 * @param reminds
 */
fun saveReminders(context: Context, fname: String, reminds: ArrayList<Reminder>) {
    try {
        // create file if doesn't exist
        // (FileOutputStream should create it automatically but just to be safe)
        val filepath = context.filesDir.path + "/" + fname
        val saveFile = File(filepath)
        saveFile.createNewFile()
        //            FileOutputStream fos = new FileOutputStream(saveFile);
        val fos = context.openFileOutput(fname, Context.MODE_PRIVATE)
        // create the serializer and writer objects
        val xmlSerial = Xml.newSerializer()
        val writer = StringWriter()
        xmlSerial.setOutput(writer)
        xmlSerial.startDocument("UTF-8", null)
        xmlSerial.startTag(null, "reminders")
        // for each reminder write its data into a XML structure
        for (r in reminds) {
            xmlSerial.startTag(null, "reminder")
            xmlSerial.startTag(null, "date")
            xmlSerial.text(TextUtils.join("/", r.date))
            xmlSerial.endTag(null, "date")
            xmlSerial.startTag(null, "time")
            xmlSerial.text(TextUtils.join(":", r.time))
            xmlSerial.endTag(null, "time")
            xmlSerial.startTag(null, "name")
            xmlSerial.text(r.name)
            xmlSerial.endTag(null, "name")
            xmlSerial.startTag(null, "number")
            xmlSerial.text(r.number)
            xmlSerial.endTag(null, "number")
            xmlSerial.startTag(null, "message")
            xmlSerial.text(r.message)
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

}

/**
 * This method sorts the reminders ArrayList<Reminder> according to date and time using the
 * selection sorting method.
 * pre: reminds parameter is of type Reminder
 * post: reminds arrayList will be sorted.
 * @param reminds: arraylist to sort
 * *
 * @param order: true is ascending; false is descending
</Reminder> */
fun orderReminders(reminds: ArrayList<Reminder>, order: Boolean) {
    if (order) {
        for (i in 0..reminds.size - 1 - 1) {
            val cal1 = Calendar.getInstance()
            val tmp1 = reminds[i]
            cal1.set(Calendar.YEAR, Integer.parseInt(tmp1.date[2]))
            cal1.set(Calendar.MONTH, Integer.parseInt(tmp1.date[1]))
            cal1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tmp1.date[0]))
            cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmp1.time[0]))
            cal1.set(Calendar.MINUTE, Integer.parseInt(tmp1.time[1]))
            cal1.set(Calendar.SECOND, 0)
            for (j in i + 1..reminds.size - 1) {
                val cal2 = Calendar.getInstance()
                val tmp2 = reminds[j]
                cal2.set(Calendar.YEAR, Integer.parseInt(tmp2.date[2]))
                cal2.set(Calendar.MONTH, Integer.parseInt(tmp2.date[1]))
                cal2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tmp2.date[0]))
                cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmp2.time[0]))
                cal2.set(Calendar.MINUTE, Integer.parseInt(tmp2.time[1]))
                cal2.set(Calendar.SECOND, 0)
                if (cal1.compareTo(cal2) > 0) {
                    reminds[i] = tmp2
                    reminds[j] = tmp1
                }
            }
        }
    } else {
        for (i in 0..reminds.size - 1 - 1) {
            val cal1 = Calendar.getInstance()
            val tmp1 = reminds[i]
            cal1.set(Calendar.YEAR, Integer.parseInt(tmp1.date[2]))
            cal1.set(Calendar.MONTH, Integer.parseInt(tmp1.date[1]))
            cal1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tmp1.date[0]))
            cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmp1.time[0]))
            cal1.set(Calendar.MINUTE, Integer.parseInt(tmp1.time[1]))
            cal1.set(Calendar.SECOND, 0)
            for (j in i + 1..reminds.size - 1) {
                val cal2 = Calendar.getInstance()
                val tmp2 = reminds[j]
                cal2.set(Calendar.YEAR, Integer.parseInt(tmp2.date[2]))
                cal2.set(Calendar.MONTH, Integer.parseInt(tmp2.date[1]))
                cal2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tmp2.date[0]))
                cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmp2.time[0]))
                cal2.set(Calendar.MINUTE, Integer.parseInt(tmp2.time[1]))
                cal2.set(Calendar.SECOND, 0)
                if (cal1.compareTo(cal2) < 0) {
                    reminds[i] = tmp2
                    reminds[j] = tmp1
                }
            }
        }
    }
}
