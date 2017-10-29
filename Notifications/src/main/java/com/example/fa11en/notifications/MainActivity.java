package com.example.fa11en.notifications;

// Import required libraries
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Main activity screen that allows the user to see previously created reminders, edit reminders,
 * and create new ones.
 */
public class MainActivity extends AppCompatActivity {

    // declare UI elements
    FloatingActionButton addButton;
    ListView remindersList;
    RemindersArrayAdapter adapter;
    // declare important variables for handling data storage and viewing
    private Context context;
    final static String filename = "reminders.xml";
    public static ArrayList<Reminder> reminders;

    /**
     * Called when the activity is created (when the app is opened) and inflates the required views.
     * pre: there is a XML view file to present to the user
     * post: important variables declared above are instantiated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call super class' method and set the view to activity_main.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the context so that static methods can reference it.
        context = getApplicationContext();
        // empty the reminders ArrayList and get the saved reminders from stored xml file
        // (determined by contents of the filename variable)
        reminders = new ArrayList<>();
        getReminders(context, filename, reminders);

        // create an adapter object that presents the user with their reminders in a listview
        adapter = new RemindersArrayAdapter(this, reminders);
        remindersList = (ListView) findViewById(R.id.remindersList);
        remindersList.setAdapter(adapter);

        // create the FAB that allows the user to create new reminders
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // when clicked send user to the EditActivity activity
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Called when the activity resumes (returns from a different activity) and updates the adapter to
     * show the modified reminders ArrayList
     * pre: adapter object is previously created
     * post: the adapters contents will be updated
     */
    @Override
    protected void onResume () {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    /**
     * Called when the activity leaves the active screen and saves the current reminders to a xml file
     * pre: saveReminders method is created with the required parameter variables
     * post: the file specified with filename will be updated
     */
    @Override
    protected void onPause () {
        super.onPause();
        saveReminders(context, filename, reminders);
    }

    /**
     * Called when the activity is destroyed (unloads from RAM) and saves the reminders just to be safe
     * pre: saveReminders method is created with the required parameter variables
     * post: the file specified with filename will be updated
     */
    @Override
    protected void onDestroy () {
        super.onDestroy();
        saveReminders(context, filename, reminders);
    }

    /**
     * Gets the data stored in fname and updates reminds with the data it receives
     * pre: required imports are satisfied
     * post: reminds array is updated.
     * @param ctx
     * @param fname
     * @param reminds
     */
    public static void getReminders (Context ctx, String fname, ArrayList<Reminder> reminds) {

        String data;

        // get the data from the XML file and read it into a string
        try {
            FileInputStream fis = ctx.openFileInput(fname);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inBuff = new char[fis.available()];
            isr.read(inBuff);
            data = new String(inBuff);
            Log.i("Read data", data);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("Read Error", "Unable to find file");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Read Error", "IO Exception");
            return;
        }
        // start parsing the XML data from the string (data)
        XmlPullParserFactory factory;
        XmlPullParser xpp;
        int eventType;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(new StringReader(data));
            // used to determine location in XML file
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.i("XML Read Error", "Xml pull parser exception raised");
            return;
        }
        // initialize all information variables
        String date = "";
        String time = "";
        String contact = "";
        String number = "";
        String message = "";
        String text = "";
        // look through the XML file until the end is reached
        while (eventType != XmlPullParser.END_DOCUMENT) {
            try {
                String name = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        // get information from the tag
                        if (name.equals("date")) date = text;
                        else if (name.equals("time")) time = text;
                        else if (name.equals("name")) contact = text;
                        else if (name.equals("number")) number = text;
                        else if (name.equals("message")) message = text;
                        // if at the end of a reminder object add it to the reminds ArrayList
                        else if (name.equals("reminder")) reminds.add(new Reminder(date.split("/"), time.split(":"), contact, number, message));
                        break;
                }
                eventType = xpp.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.i("XML Read Error", "Xml pull parser exception raised");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("XML Read Error", "IO exception raised");
                return;
            }
        }
    }

    /**
     * Write the data stored in reminds to fname in xml format.
     * pre: required imports are satisfied
     * post: the contents of fname will be updated
     * @param context
     * @param fname
     * @param reminds
     */
    public static void saveReminders (Context context, String fname, ArrayList<Reminder> reminds) {
        try {
            // create file if doesn't exist
            // (FileOutputStream should create it automatically but just to be safe)
            String filepath = context.getFilesDir().getPath() + "/" + fname;
            File saveFile = new File(filepath);
            saveFile.createNewFile();
//            FileOutputStream fos = new FileOutputStream(saveFile);
            FileOutputStream fos = context.openFileOutput(fname, Context.MODE_PRIVATE);
            // create the serializer and writer objects
            XmlSerializer xmlSerial = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerial.setOutput(writer);
            xmlSerial.startDocument("UTF-8", null);
            xmlSerial.startTag(null, "reminders");
            // for each reminder write its data into a XML structure
            for (Reminder r : reminds) {
                xmlSerial.startTag(null, "reminder");
                xmlSerial.startTag(null, "date");
                xmlSerial.text(TextUtils.join("/", r.date));
                xmlSerial.endTag(null, "date");
                xmlSerial.startTag(null, "time");
                xmlSerial.text(TextUtils.join(":", r.time));
                xmlSerial.endTag(null, "time");
                xmlSerial.startTag(null, "name");
                xmlSerial.text(r.name);
                xmlSerial.endTag(null, "name");
                xmlSerial.startTag(null, "number");
                xmlSerial.text(r.number);
                xmlSerial.endTag(null, "number");
                xmlSerial.startTag(null, "message");
                xmlSerial.text(r.message);
                xmlSerial.endTag(null, "message");
                xmlSerial.endTag(null, "reminder");
            }
            // end and write to document and close streams
            xmlSerial.endTag(null, "reminders");
            xmlSerial.endDocument();
            xmlSerial.flush();
            String dWrite = writer.toString();
            fos.write(dWrite.getBytes());
            fos.close();
        } catch (IllegalArgumentException e) {
            Log.i("XML Write Error", "Illegal argument raised");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.i("XML Write Error", "Illegal state of the document/streams");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("XML Write Error", "IO exception raised");
            e.printStackTrace();
        }
    }

}

