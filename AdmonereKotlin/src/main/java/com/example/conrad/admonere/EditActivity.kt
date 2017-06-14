package com.example.conrad.admonere

// import required libraries
import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast

import java.util.Calendar

class EditActivity : Activity () {

    // integers for storing statuses of results from other activities and permissions
    private val RESULT_PICK_CONTACT : Int = 85500
    private val PERMISSIONS_REQUEST_SEND_SMS : Int = 0

    // variables for storing data user enters
    private var usrData = arrayOfNulls<String>(5)
    private var dayBtns = arrayOfNulls<Button>(7)
    private var dayBtnActives : BooleanArray = BooleanArray(dayBtns.size)
    private var index : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // set UI elements
        var datePicker = findViewById(R.id.datePick) as EditText
        var timePicker = findViewById(R.id.timePick) as EditText
        var contact = findViewById(R.id.phoneNumber) as EditText
        var message = findViewById(R.id.message) as EditText
        var sendBtn = findViewById(R.id.sendBtn) as Button
        var backBtn = findViewById(R.id.backBtn) as Button
        dayBtns[0] = findViewById(R.id.sunBtn) as Button
        dayBtns[1] = findViewById(R.id.monBtn) as Button
        dayBtns[2] = findViewById(R.id.thuBtn) as Button
        dayBtns[3] = findViewById(R.id.wedBtn) as Button
        dayBtns[4] = findViewById(R.id.thuBtn) as Button
        dayBtns[5] = findViewById(R.id.friBtn) as Button
        dayBtns[6] = findViewById(R.id.satBtn) as Button

        // don't show the keyboard when user taps these UI elements
        // (they will be sent to a different activity/dialog
        datePicker.showSoftInputOnFocus = false
        timePicker.showSoftInputOnFocus = false
        contact.showSoftInputOnFocus = false

        // try to get infomation from previous activity (MainActivity)
        var bundle : Bundle = intent.extras
        try {
            usrData[0] = bundle.getString("date")
            usrData[1] = bundle.getString("time")
            usrData[2] = bundle.getString("name")
            usrData[3] = bundle.getString("number")
            usrData[4] = bundle.getString("message")
            index = bundle.getInt("index")
        } catch (e : Exception) {
            e.printStackTrace()
            index = -1
        }

        if (usrData[0] != null) datePicker.setText(usrData[0])
        if (usrData[1] != null) timePicker.setText(usrData[1])
        if (usrData[2] != null && usrData[3] != null) contact.setText(usrData[2] + " at " + usrData[3])
        if (usrData[4] != null) datePicker.setText(usrData[0])

        // loop through the day buttons and change the activation
        // variable and background for each when clicked
        for (b in 0..dayBtns.size) {
            dayBtns[b]!!.setOnClickListener {
                it.setBackgroundColor(Color.parseColor("#FFFFFF"))
                dayBtnActives[b] = !dayBtnActives[b]
            }
        }

    }
}


