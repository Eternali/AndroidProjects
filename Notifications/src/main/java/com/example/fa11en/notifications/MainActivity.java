package com.example.fa11en.notifications;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    private EditText datePicker;
    private EditText timePicker;
    private EditText phoneNumber;
    private EditText message;
    private Button sendBtn;

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = (EditText) findViewById(R.id.datePick);
        timePicker = (EditText) findViewById(R.id.timePick);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        message = (EditText) findViewById(R.id.message);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        datePicker.setShowSoftInputOnFocus(false);
        timePicker.setShowSoftInputOnFocus(false);

        datePicker.setOnClickListener (new View.OnClickListener() {
            @Override
            @TargetApi(24)
            public void onClick (View v) {
                Calendar cCurrentDate = Calendar.getInstance();
                int cYear = cCurrentDate.get(Calendar.YEAR);
                int cMonth = cCurrentDate.get(Calendar.MONTH);
                int cDay = cCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(""+Integer.toString(dayOfMonth)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
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

                TimePickerDialog timePickDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker.setText(""+Integer.toString(hourOfDay)+":"+Integer.toString(minute));
                    }
                }, chour, cminute, true);
                timePickDialog.setTitle("Select Time");
                timePickDialog.show();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                String[] date = datePicker.getText().toString().split("/");
                String[] time = timePicker.getText().toString().split(":");
                String phoneNo = phoneNumber.getText().toString();
                String msg = message.getText().toString();

                Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));
                calendar.set(Calendar.MONTH, Integer.parseInt(date[1]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                calendar.set(Calendar.SECOND, 0);

                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                intent.putExtra("message", msg);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
////                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
////                        SystemClock.elapsedRealtime()+20*1000,
////                        20*1000, alarmIntent);
//
////                alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+20*1000, 20*1000, alarmIntent);
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        });
    }

}
