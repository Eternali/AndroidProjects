package com.example.fa11en.notifications;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addButton;
    public ArrayList<Reminder> reminders = new ArrayList<>();
    final String filename = "reminders.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void saveReminders (String fname, ArrayList<Reminder> reminds) {
        try {
            // create file if doesn't exist
            // (FileOutputStream should create it automatically but just to be safe)
            File saveFile = new File(fname+".xml");
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

