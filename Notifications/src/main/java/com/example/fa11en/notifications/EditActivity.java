package com.example.fa11en.notifications;

import java.security.KeyException;
import java.util.Calendar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditActivity extends Activity {

    private static final int RESULT_PICK_CONTACT = 85500;  // desired result code
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private EditText datePicker;
    private EditText timePicker;
    private EditText contact;
    private EditText message;
    private Button sendBtn;
    private Button backBtn;

    private String[] data = new String[5];
    int index;
    Bundle bundle;

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

        bundle = getIntent().getExtras();
        try {
            data[0] = bundle.getString("date");
            data[1] = bundle.getString("time");
            data[2] = bundle.getString("name");
            data[3] = bundle.getString("number");
            data[4] = bundle.getString("message");
            index = bundle.getInt("index");
        } catch (Exception e) {
            e.printStackTrace();
            for (int d = 0; d < data.length; d++) {
                data[d] = null;
            }
            index = -1;
        }

        if (data[0] != null) datePicker.setText(data[0]);
        if (data[1] != null) timePicker.setText(data[1]);
        if (data[2] != null && data[3] != null) contact.setText(data[2] + " at " + data[3]);
        if (data[4] != null) message.setText(data[4]);

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
                        timePicker.setText(Integer.toString(hourOfDay)+":"+Integer.toString(minute));
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
                if (ContextCompat.checkSelfPermission(EditActivity.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditActivity.this,
                            new String[] {Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }

                String[] date = datePicker.getText().toString().split("/");
                String[] time = timePicker.getText().toString().split(":");
                String[] info = contact.getText().toString().split(" ");
                String name = info[0];
                String phoneNo = info[2];
                String msg = message.getText().toString();

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));
                calendar.set(Calendar.MONTH, Integer.parseInt(date[1]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                calendar.set(Calendar.SECOND, 0);

                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent;
                PendingIntent alarmIntent;

                if (index < 0) {
                    intent = new Intent(EditActivity.this, AlarmReceiver.class);
                    intent.putExtra("number", phoneNo);
                    intent.putExtra("message", msg);
                    alarmIntent = PendingIntent.getBroadcast(EditActivity.this, MainActivity.reminders.size(), intent, 0);
                    MainActivity.reminders.add(new Reminder(date, time, name, phoneNo, msg));
                } else {
//                    Intent prevIntent = new Intent(EditActivity.this, AlarmReceiver.class);
//                    prevIntent.putExtra("number", phoneNo);
//                    prevIntent.putExtra("message", msg);
//                    PendingIntent prevAlarmIntent = PendingIntent.getBroadcast(EditActivity.this, index, prevIntent, 0)
//                    alarmMgr.cancel(prevAlarmIntent);
                    intent = new Intent(EditActivity.this, AlarmReceiver.class);
                    intent.putExtra("number", phoneNo);
                    intent.putExtra("message", msg);
                    alarmIntent = PendingIntent.getBroadcast(EditActivity.this, index, intent, 0);
                    MainActivity.reminders.set(index, new Reminder(date, time, name, phoneNo, msg));

                }
                Log.i("Reminders", MainActivity.reminders.get(0).message);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

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

    @Override
    @TargetApi(19)
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length <= 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed: Incorrect permissions", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void goToMain () {
        Intent backIntent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(backIntent);
    }

    private void parseSelectedContact (Intent data) {
        Cursor cursor;
        String phoneNo;
        String name;
        try {
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
