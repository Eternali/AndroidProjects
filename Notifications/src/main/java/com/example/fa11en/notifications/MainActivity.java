package com.example.fa11en.notifications;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    RemindersArrayAdapter adapter;
    private Context context;
    final static String filename = "reminders.xml";
    public static ArrayList<Reminder> reminders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        getReminders(context, filename, reminders);

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
        saveReminders(context, filename, reminders);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        saveReminders(context, filename, reminders);
    }

//    @Override
//    protected void onActivityResult (int reqCode, int resCode, Intent data) {
//        if (resCode == RESULT_OK) {
//            switch (reqCode) {
//                case RESULT_GOT_REMINDER:
//                    updateReminders(data);
//                    break;
//            }
//        } else {
//            Log.e("Editactivity Result", "Failed to get new reminder");
//        }
//    }

    /*
    private static void updateReminders (Context ctx, Intent data) {
        Cursor cursor;
        Reminder r;
        String date = "";
        String time = "";
        String name = "";
        String number = "";
        String message = "";
        try {
            Uri uri = data.getData();
            cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            date = cursor.getString(cursor.getColumnIndex(EditActivity.))
        }

        reminders.add(r);
    }*/

    public static void getReminders (Context ctx, String fname, ArrayList<Reminder> reminds) {

        String data = "";
//        String[] date = {"07", "06", "2017"};
//        String[] time = {"9", "40"};
//        reminds.add(new Reminder(date, time, "Billy", "5554", "reminder"));

        try {
            FileInputStream fis = ctx.openFileInput(fname);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inBuff = new char[fis.available()];
            isr.read(inBuff);
            data = new String(inBuff);
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
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
            return;
        }
        String date = "";
        String time = "";
        String contact = "";
        String number = "";
        String message = "";
        String text = "";
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
                        if (name.equals("date")) date = text;
                        else if (name.equals("time")) time = text;
                        else if (name.equals("name")) contact = text;
                        else if (name.equals("number")) number = text;
                        else if (name.equals("message")) message = text;
                        else if (name.equals("reminder")) reminds.add(new Reminder(date.split("/"), time.split(":"), contact, number, message));
                        break;
                }
                eventType = xpp.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }

    }

    public static void saveReminders (Context context, String fname, ArrayList<Reminder> reminds) {
        try {
            // create file if doesn't exist
            // (FileOutputStream should create it automatically but just to be safe)
            File saveFile = new File(fname);
            saveFile.createNewFile();
//            FileOutputStream fos = new FileOutputStream(saveFile);
            FileOutputStream fos = context.getApplicationContext().openFileOutput(fname, Context.MODE_PRIVATE);
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

