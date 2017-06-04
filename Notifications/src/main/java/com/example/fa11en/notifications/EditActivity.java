package com.example.fa11en.notifications;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditActivity extends Activity {

    private static final int RESULT_PICK_CONTACT = 85500;  // desired result code

    private EditText datePicker;
    private EditText timePicker;
    private EditText contact;
    private EditText message;
    private Button sendBtn;
    private Button backBtn;

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        datePicker = (EditText) findViewById(R.id.datePick);
        timePicker = (EditText) findViewById(R.id.timePick);
        contact = (EditText) findViewById(R.id.phoneNumber);
        message = (EditText) findViewById(R.id.message);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        datePicker.setShowSoftInputOnFocus(false);
        timePicker.setShowSoftInputOnFocus(false);
        contact.setShowSoftInputOnFocus(false);

        datePicker.setOnClickListener (new View.OnClickListener() {
            @Override
            @TargetApi(24)
            public void onClick (View v) {
                Calendar cCurrentDate = Calendar.getInstance();
                int cYear = cCurrentDate.get(Calendar.YEAR);
                int cMonth = cCurrentDate.get(Calendar.MONTH);
                int cDay = cCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(Integer.toString(dayOfMonth)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
                    }
                }, cYear, cMonth, cDay);
                datePickDialog.setTitle("Select Date");
                datePickDialog.show();
            }
        });

        timePicker.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Calendar cCurrentTime = Calendar.getInstance();
                int chour = cCurrentTime.get(Calendar.HOUR_OF_DAY);
                int cminute = cCurrentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickDialog = new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker.setText(""+Integer.toString(hourOfDay)+":"+Integer.toString(minute));
                    }
                }, chour, cminute, true);
                timePickDialog.setTitle("Select Time");
                timePickDialog.show();
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent contactPicker = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPicker, RESULT_PICK_CONTACT);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                String[] date = datePicker.getText().toString().split("/");
                String[] time = timePicker.getText().toString().split(":");
                String[] info = contact.getText().toString().split(" ");
                String name = info[0];
                String phoneNo = info[2];
                String msg = message.getText().toString();

                Reminder remind = new Reminder(date, time, name, phoneNo, msg);

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));
                calendar.set(Calendar.MONTH, Integer.parseInt(date[1]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                calendar.set(Calendar.SECOND, 0);

                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(EditActivity.this, AlarmReceiver.class);
                intent.putExtra("message", msg);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);

                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

                saveReminder(remind);

                goToMain();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                goToMain();
            }
        });

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    parseSelectedContact(data);
                    break;
            }
        } else {
            Log.e("EditActivity", "Failed to pick contact");
        }
    }

    private void goToMain () {
        Intent backIntent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(backIntent);
    }

    private void parseSelectedContact (Intent data) {
        Cursor cursor;
        try {
            String phoneNo;
            String name;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contact.setText(name + " at " + phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveReminder (Reminder reminder) {

    }

}
