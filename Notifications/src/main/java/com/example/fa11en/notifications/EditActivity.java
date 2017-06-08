package com.example.fa11en.notifications;

// Import required libraries
import java.util.Calendar;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Edit activity screen that enables the user to edit existing reminders and add new reminders
 */
public class EditActivity extends Activity {

    // Integers for storing statuses of results from other activities and the status of permissions
    private static final int RESULT_PICK_CONTACT = 85500;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    // Declare UI elements
    private EditText datePicker;
    private EditText timePicker;
    private EditText contact;
    private EditText message;
    private Button sendBtn;
    private Button backBtn;

    // Variables for storing data the user enters
    private String[] data = new String[5];
    int index; // index of object in the reminders array (-1) if user is creating a new one
    Bundle bundle;

    /**
     * Called when the activity is created (when the app is opened) and inflates the required views.
     * pre: there is a XML view file to present to the user
     * post: important variables declared above are instantiated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Set the UI element variables
        datePicker = (EditText) findViewById(R.id.datePick);
        timePicker = (EditText) findViewById(R.id.timePick);
        contact = (EditText) findViewById(R.id.phoneNumber);
        message = (EditText) findViewById(R.id.message);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        backBtn = (Button) findViewById(R.id.backBtn);

        // Do not show the keyboard when the user clicks on these UI elements
        // because they will be sent to a different activity/dialog
        datePicker.setShowSoftInputOnFocus(false);
        timePicker.setShowSoftInputOnFocus(false);
        contact.setShowSoftInputOnFocus(false);

        // Try to get information from previous activity (MainActivity)
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

        // Set the text of the UI elements if not null
        if (data[0] != null) datePicker.setText(data[0]);
        if (data[1] != null) timePicker.setText(data[1]);
        if (data[2] != null && data[3] != null) contact.setText(data[2] + " at " + data[3]);
        if (data[4] != null) message.setText(data[4]);

        // Send the user to a DatePickerDialog when they click on the datePicker EditText
        datePicker.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Get the current date
                Calendar cCurrentDate = Calendar.getInstance();
                int cYear = cCurrentDate.get(Calendar.YEAR);
                int cMonth = cCurrentDate.get(Calendar.MONTH);
                int cDay = cCurrentDate.get(Calendar.DAY_OF_MONTH);

                // Create a datePickerDialog and set its initial date to today
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

        // Send the user to a TimePickerDialog when they click on the timePicker EditText
        timePicker.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Get the current time
                Calendar cCurrentTime = Calendar.getInstance();
                int chour = cCurrentTime.get(Calendar.HOUR_OF_DAY);
                int cminute = cCurrentTime.get(Calendar.MINUTE);

                // Create a timePickDialog and set its initial time to current
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

        // Send the user to the ContactsContract activity to get the phone number to send to
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent contactPicker = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPicker, RESULT_PICK_CONTACT);
            }
        });

        // Save the reminder when the user clicks on the send button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Check for the permission to send text (SMS) messages
                // if not satisfied, ask user for permission
                if (ContextCompat.checkSelfPermission(EditActivity.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditActivity.this,
                            new String[] {Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }

                // Important variables
                String[] date, time;
                String name, phoneNo, msg;
                Calendar calendar = Calendar.getInstance();

                // Try to get the data from the UI elements and alert the user if the data is invalid
                try {
                    date = datePicker.getText().toString().split("/");
                    time = timePicker.getText().toString().split(":");
                    String[] info = contact.getText().toString().split(" ");
                    name = info[0];
                    phoneNo = info[2];
                    msg = message.getText().toString();

                    calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));
                    calendar.set(Calendar.MONTH, Integer.parseInt(date[1]));
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                    calendar.set(Calendar.SECOND, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "Please enter valid input.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Create an alarm manager to allow the sending of text message after app closure
                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent;
                PendingIntent alarmIntent;

                // Check if this is a new reminder or if we're editing an existing reminder
                if (index < 0) {
                    intent = new Intent(EditActivity.this, AlarmReceiver.class);
                    intent.putExtra("number", phoneNo);
                    intent.putExtra("message", msg);
                    alarmIntent = PendingIntent.getBroadcast(EditActivity.this, MainActivity.reminders.size(), intent, 0);
                    MainActivity.reminders.add(new Reminder(date, time, name, phoneNo, msg));
                } else {
                    intent = new Intent(EditActivity.this, AlarmReceiver.class);
                    intent.putExtra("number", phoneNo);
                    intent.putExtra("message", msg);
                    // NOTE: since we are making an alarmIntent with a specific ID (index) this
                    // will automatically override the existing one with the same ID.
                    alarmIntent = PendingIntent.getBroadcast(EditActivity.this, index, intent, 0);
                    MainActivity.reminders.set(index, new Reminder(date, time, name, phoneNo, msg));

                }

                // Set an alarm and go back to the MainActivity
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                goToMain();

            }
        });

        // Go back to the MainScreen and delete the reminder (only if it is created already)
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (index >= 0) MainActivity.reminders.remove(index);
                goToMain();
            }
        });

    }

    /**
     * Called when an activity returns a result to this activity
     * pre: the activity is of the correct format
     * post: an action will be done depending on the request code
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // If the activity returns with no errors
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // If the user has picked a contact parse it
                case RESULT_PICK_CONTACT:
                    parseSelectedContact(data);
                    break;
            }
        } else {
            Log.e("EditActivity", "Failed to pick contact");
        }
    }

    /**
     * Called when the user replies to a permission request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            // if the user has not granted permission tell them the SMS will fail.
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length <= 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "SMS will fail: Incorrect permissions", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    /**
     * Will parse returned data from an intent
     * pre: an intent is provided to parse
     * post: the contact UI element will have its text set
     * @param data
     */
    private void parseSelectedContact (Intent data) {
        Cursor cursor;
        String phoneNo;
        String name;
        try {
            // Get the data and get a cursor
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            // Move the cursor to the first returned result
            cursor.moveToFirst();
            // Get the required data
            phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contact.setText(name + " at " + phoneNo);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EditActivity.this, "Failed to retrieve contact info", Toast.LENGTH_LONG).show();
        }
    }

    // Method to close this activity and return to the previous
    private void goToMain () {
        this.finish();
    }

}
