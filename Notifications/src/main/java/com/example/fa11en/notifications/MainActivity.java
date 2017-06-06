package com.example.fa11en.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addButton;
    ListView remindersList;
    public static ArrayList<Reminder> reminders = new ArrayList<>();
    RemindersArrayAdapter adapter;
    final String filename = "reminders.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminders = getReminders(filename);

        adapter = new RemindersArrayAdapter(this, reminders);
        remindersList = (ListView) findViewById(R.id.remindersList);
        remindersList.setAdapter(adapter);

        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause () {
        super.onPause();
        saveReminders(filename, reminders);
    }

    public ArrayList<Reminder> getReminders (String fname) {
        String data = null;

        ArrayList<Reminder> tmpReminds = new ArrayList<>();
        try {
            FileInputStream fis = getApplicationContext().openFileInput(fname);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inBuff = new char[fis.available()];
            isr.read(inBuff);
            data = new String(inBuff);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XmlPullParserFactory factory;
        XmlPullParser xpp = null;
        int eventType = 0;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(new StringReader(data));
            eventType = xpp.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String[] date;
            String[] time;
            String name;
            String number;
            String message;

            try {
                if (eventType == XmlPullParser.START_TAG && "reminder".equals(xpp.getName())) {
                    eventType = xpp.next();
                    eventType = xpp.next();
                    date = xpp.getText().split("/");
                    eventType = xpp.next();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    time = xpp.getText().split(":");
                    eventType = xpp.next();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    name = xpp.getText();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    number = xpp.getText();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    eventType = xpp.next();
                    message = xpp.getText();
                    tmpReminds.add(new Reminder(date, time, name, number, message));
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tmpReminds;
    }

    public void saveReminders (String fname, ArrayList<Reminder> reminds) {
        try {
            // create file if doesn't exist
            // (FileOutputStream should create it automatically but just to be safe)
            File saveFile = new File(fname);
            saveFile.createNewFile();
//            FileOutputStream fos = new FileOutputStream(saveFile);
            FileOutputStream fos = getApplicationContext().openFileOutput(fname, Context.MODE_PRIVATE);
            XmlSerializer xmlSerial = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerial.setOutput(writer);
            xmlSerial.startDocument("UTF-8", null);
            xmlSerial.startTag(null, "reminders");
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
            xmlSerial.endTag(null, "reminders");
            xmlSerial.endDocument();
            xmlSerial.flush();
            String dWrite = writer.toString();
            fos.write(dWrite.getBytes());
            fos.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

