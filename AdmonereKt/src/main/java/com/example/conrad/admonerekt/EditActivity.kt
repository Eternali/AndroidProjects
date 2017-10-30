package com.example.conrad.admonerekt

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import android.content.Context
import java.util.*

/**
 * Edit activity screen that enables the user to edit existing reminders and add new reminders
 */
class EditActivity : Activity() {

    // Declare UI elements
    private var datePicker: EditText? = null
    private var timePicker: EditText? = null
    private var contact: EditText? = null
    private var message: EditText? = null
    private var sendBtn: Button? = null
    private var backBtn: Button? = null
    private var repetitionFreq: NumberPicker? = null

    // Variables for storing data the user enters
    private val data = arrayOfNulls<String>(5)
    internal var index: Int = 0 // index of object in the reminders array (-1) if user is creating a new one
    internal var bundle: Bundle? = null

    /**
     * Called when the activity is created (when the app is opened) and inflates the required views.
     * pre: there is a XML view file to present to the user
     * post: important variables declared above are instantiated.
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Set the UI element variables
        datePicker = findViewById(R.id.datePick) as EditText
        timePicker = findViewById(R.id.timePick) as EditText
        contact = findViewById(R.id.phoneNumber) as EditText
        message = findViewById(R.id.message) as EditText
        sendBtn = findViewById(R.id.sendBtn) as Button
        backBtn = findViewById(R.id.backBtn) as Button
        repetitionFreq = findViewById(R.id.repetitionFreq) as NumberPicker

        // Set values for number picker to limit repetition rate
        repetitionFreq!!.minValue = 0
        repetitionFreq!!.maxValue = 30
        repetitionFreq!!.wrapSelectorWheel = true

        // Do not show the keyboard when the user clicks on these UI elements
        // because they will be sent to a different activity/dialog
        datePicker!!.showSoftInputOnFocus = false
        timePicker!!.showSoftInputOnFocus = false
        contact!!.showSoftInputOnFocus = false

        // Try to get information from previous activity (MainActivity)
        bundle = intent.extras
        try {
            data[0] = bundle!!.getString("date")
            data[1] = bundle!!.getString("time")
            data[2] = bundle!!.getString("name")
            data[3] = bundle!!.getString("number")
            data[4] = bundle!!.getString("message")
            index = bundle!!.getInt("index")
        } catch (e: Exception) {
            e.printStackTrace()
            for (d in data.indices) {
                data[d] = null
            }
            index = -1
        }

        // Set the text of the UI elements if not null
        if (data[0] != null) datePicker!!.setText(data[0])
        if (data[1] != null) timePicker!!.setText(data[1])
        if (data[2] != null && data[3] != null) contact!!.setText(data[2] + " at " + data[3])
        if (data[4] != null) message!!.setText(data[4])

        // Send the user to a DatePickerDialog when they click on the datePicker EditText
        datePicker!!.setOnClickListener {
            // Get the current date
            val cCurrentDate = Calendar.getInstance()
            val cYear = cCurrentDate.get(Calendar.YEAR)
            val cMonth = cCurrentDate.get(Calendar.MONTH)
            val cDay = cCurrentDate.get(Calendar.DAY_OF_MONTH)

            // Create a datePickerDialog and set its initial date to today
            val datePickDialog = DatePickerDialog(this@EditActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> datePicker!!.setText(Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year)) }, cYear, cMonth, cDay)
            datePickDialog.setTitle("Select Date")
            datePickDialog.show()
        }

        // Send the user to a TimePickerDialog when they click on the timePicker EditText
        timePicker!!.setOnClickListener {
            // Get the current time
            val cCurrentTime = Calendar.getInstance()
            val chour = cCurrentTime.get(Calendar.HOUR_OF_DAY)
            val cminute = cCurrentTime.get(Calendar.MINUTE)

            // Create a timePickDialog and set its initial time to current
            val timePickDialog = TimePickerDialog(this@EditActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> timePicker!!.setText(Integer.toString(hourOfDay) + ":" + Integer.toString(minute)) }, chour, cminute, true)
            timePickDialog.setTitle("Select Time")
            timePickDialog.show()
        }

        // Listen for when the user changes the repetition frequency
        repetitionFreq!!.setOnValueChangedListener { picker, oldVal, newVal -> }

        // Send the user to the ContactsContract activity to get the phone number to send to
        contact!!.setOnClickListener {
            val contactPicker = Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(contactPicker, RESULT_PICK_CONTACT)
        }

        // Save the reminder when the user clicks on the send button
        sendBtn!!.setOnClickListener(View.OnClickListener {
            // Check for the permission to send text (SMS) messages
            // if not satisfied, ask user for permission
            if (ContextCompat.checkSelfPermission(this@EditActivity,
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@EditActivity,
                        arrayOf(Manifest.permission.SEND_SMS),
                        MY_PERMISSIONS_REQUEST_SEND_SMS)
            }

            // Important variables
            val date: Array<String>
            val time: Array<String>
            val name: String
            val phoneNo: String
            val msg: String
            val calendar = Calendar.getInstance()

            // Try to get the data from the UI elements and alert the user if the data is invalid
            try {
                date = datePicker!!.text.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                time = timePicker!!.text.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val info = contact!!.text.toString().replace("\\s".toRegex(), "").split("at".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                name = info[0]
                phoneNo = info[1]
                msg = message!!.text.toString()

                calendar.set(Calendar.YEAR, Integer.parseInt(date[2]))
                calendar.set(Calendar.MONTH, Integer.parseInt(date[1]))
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]))
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]))
                calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]))
                calendar.set(Calendar.SECOND, 0)

                // Test if the desired time is after the current time
                val curCalendar = Calendar.getInstance()
                if (curCalendar.compareTo(calendar) >= 0) {
                    Toast.makeText(this@EditActivity,
                            "Please choose a time in the future.",
                            Toast.LENGTH_LONG).show()
                    return@OnClickListener
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@EditActivity, "Please enter valid input.", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // Create an alarm manager to allow the sending of text message after app closure
            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent: Intent
            val alarmIntent: PendingIntent

            // Check if this is a new reminder or if we're editing an existing reminder
            if (index < 0) {
                intent = Intent(this@EditActivity, AlarmReceiver::class.java)
                intent.putExtra("number", phoneNo)
                intent.putExtra("message", msg)
                alarmIntent = PendingIntent.getBroadcast(this@EditActivity, MainActivity.reminders.size, intent, 0)
                MainActivity.reminders.add(Reminder(date, time, name, phoneNo, msg))
            } else {
                intent = Intent(this@EditActivity, AlarmReceiver::class.java)
                intent.putExtra("number", phoneNo)
                intent.putExtra("message", msg)
                // NOTE: since we are making an alarmIntent with a specific ID (index) this
                // will automatically override the existing one with the same ID.
                alarmIntent = PendingIntent.getBroadcast(this@EditActivity, index, intent, 0)
                MainActivity.reminders[index] = Reminder(date, time, name, phoneNo, msg)

            }

            // Set an alarm and go back to the MainActivity
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
            goToMain()
        })

        // Go back to the MainScreen and delete the reminder (only if it is created already)
        backBtn!!.setOnClickListener {
            if (index >= 0) {
                val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val rmIntent = Intent(this@EditActivity, AlarmReceiver::class.java)
                val pendIntent = PendingIntent.getBroadcast(this@EditActivity, index, rmIntent, 0)
                alarmMgr.cancel(pendIntent)
                MainActivity.reminders.removeAt(index)
            }
            goToMain()
        }

    }

    /**
     * Called when an activity returns a result to this activity
     * pre: the activity is of the correct format
     * post: an action will be done depending on the request code
     * @param requestCode
     * *
     * @param resultCode
     * *
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // If the activity returns with no errors
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
            // If the user has picked a contact parse it
                RESULT_PICK_CONTACT -> parseSelectedContact(data)
            }
        } else {
            Log.e("EditActivity", "Failed to pick contact")
        }
    }

    /**
     * Called when the user replies to a permission request
     * @param requestCode
     * *
     * @param permissions
     * *
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
        // if the user has not granted permission tell them the SMS will fail.
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults.size <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext,
                            "SMS will fail: Incorrect permissions", Toast.LENGTH_LONG).show()
                    return
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
    private fun parseSelectedContact(data: Intent) {
        val cursor: Cursor
        val phoneNo: String
        val name: String
        try {
            // Get the data and get a cursor
            val uri = data.data
            cursor = contentResolver.query(uri, null, null, null, null)
            // Move the cursor to the first returned result
            cursor.moveToFirst()
            // Get the required data
            phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            contact!!.setText(name + " at " + phoneNo)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@EditActivity, "Failed to retrieve contact info", Toast.LENGTH_LONG).show()
        }

    }

    // Method to close this activity and return to the previous
    private fun goToMain() {
        this.finish()
    }

    companion object {

        // Integers for storing statuses of results from other activities and the status of permissions
        private val RESULT_PICK_CONTACT = 85500
        private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    }

}
