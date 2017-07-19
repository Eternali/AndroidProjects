package com.example.conrad.admonere

// import required libraries
import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TimePicker
import java.math.BigDecimal

import java.util.Calendar

class EditActivity : Activity () {

    // integers for storing statuses of results from other activities and permissions
    private val RESULT_PICK_CONTACT : Int = 85500
    private val PERMISSIONS_REQUEST_SEND_SMS : Int = 0

    // variables for storing data user enters
    private var usrData : Reminder? = null
    private var dayBtns = arrayOfNulls<Button>(7)
    private var dayBtnActives : BooleanArray = BooleanArray(dayBtns.size)
    // array for checking if the user is allowed to change the state of the buttons (initialized to true)
    private var dayBtnAllows : BooleanArray = BooleanArray(dayBtns.size, { _ -> true })
    private var index : Int = 0

    // must initialize contact UI element outside of onCreate because of ActivityResult
    var contact : EditText? = null

    // private function to reset all the day arrays according to the days array
    // (length n, where each value is between 0 and 6, and the first value is the starting date (can't be changed))
    private fun setDays (days : IntArray) {
        if (null in dayBtns) return
        dayBtnAllows = BooleanArray(dayBtns.size, { _ -> true })
        dayBtnAllows[days[0]] = false
        dayBtnActives = BooleanArray(dayBtns.size)
        (0..days.size-1).forEach { d -> dayBtnActives[days[d]] = true }
        (0..dayBtnActives.size-1).forEach { d -> if (dayBtnActives[d])
                dayBtns[d]!!.setBackgroundResource(R.drawable.roundedbuttonselected) else
                dayBtns[d]!!.setBackgroundResource(R.drawable.roundedbutton) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the theme
        setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // set UI elements
        val datePicker = findViewById(R.id.datePick) as EditText
        val timePicker = findViewById(R.id.timePick) as EditText
        val numRepsET = findViewById(R.id.maxFreq) as EditText
        contact = findViewById(R.id.phoneNumber) as EditText
        val message = findViewById(R.id.message) as EditText
        val sendBtn = findViewById(R.id.sendBtn) as Button
        val backBtn = findViewById(R.id.backBtn) as Button
        dayBtns[0] = findViewById(R.id.sunBtn) as Button
        dayBtns[1] = findViewById(R.id.monBtn) as Button
        dayBtns[2] = findViewById(R.id.tueBtn) as Button
        dayBtns[3] = findViewById(R.id.wedBtn) as Button
        dayBtns[4] = findViewById(R.id.thuBtn) as Button
        dayBtns[5] = findViewById(R.id.friBtn) as Button
        dayBtns[6] = findViewById(R.id.satBtn) as Button

        // just to make sure no NullPointerExceptions check if there are any nulls in dayBtns or dayBtnActives
        if (null in dayBtns) { displayWarning(this, "Failed to load UI."); finish() }

        // don't show the keyboard when user taps these UI elements
        // (they will be sent to a different activity/dialog)
        datePicker.showSoftInputOnFocus = false
        timePicker.showSoftInputOnFocus = false
        (contact as EditText).showSoftInputOnFocus = false

        // try to get infomation from previous activity (MainActivity)
        var bundle : Bundle? = if (intent.extras != null) intent.extras else null
        // if we don't get any data then make the index -1 to let the rest of the activity know
        if (bundle != null) {
            index = bundle.getInt("index")
            usrData = getReminders(applicationContext, filename)[index]
        } else index = -1

        if (usrData != null) {
            val ud = (usrData as Reminder)
            // must reformat date for user (month + 1)
            val date = ud.dates[0].split("/").map { it -> it.toInt() } as ArrayList
            date[1] += 1
            datePicker.setText(TextUtils.join("/", date))
            timePicker.setText(TextUtils.join(":", ud.time))
            numRepsET.setText(ud.numReminds)
            (contact as EditText).setText("${ud.name} at ${ud.number}")
            message.setText(ud.message)
            val days = IntArray(ud.dates.size)
            for (d in 0..ud.dates.size-1) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, ud.dates[d].split("/")[0].toInt(),
                        Calendar.MONTH, ud.dates[d].split("/")[1].toInt(),
                        Calendar.YEAR, ud.dates[d].split("/")[2].toInt())
                days[d] = cal.get(Calendar.DAY_OF_WEEK)
            }
            setDays(days)
            if (numRepsET.text.toString().toInt() < dayBtnActives.filter { it }.size) numRepsET.setText(dayBtnActives.filter { it }.size.toString())
        }

        // loop through the day buttons and change the activation
        // variable and background for each when clicked
        for (b in 0..dayBtns.size-1) {
            dayBtns[b]!!.setOnClickListener {
                if (!dayBtnAllows[b]) return@setOnClickListener
                if (!dayBtnActives[b]) it.setBackgroundResource(R.drawable.roundedbuttonselected)
                else it.setBackgroundResource(R.drawable.roundedbutton)
                dayBtnActives[b] = !dayBtnActives[b]
                if (numRepsET.text.toString().toInt() < dayBtnActives.filter { it }.size) numRepsET.setText(dayBtnActives.filter { it }.size.toString())
            }
        }

        // send user to a DatePickerDialog when they tap on the datePicker edittext
        datePicker.setOnClickListener {
            // get the current date
            // TODO add the ability to have it initialize to the previously set date if possible
            val cCurrentDate : Calendar = Calendar.getInstance()
            val cYear : Int = cCurrentDate.get(Calendar.YEAR)
            val cMonth : Int = cCurrentDate.get(Calendar.MONTH)
            val cDay : Int = cCurrentDate.get(Calendar.DAY_OF_MONTH)

            // create date picker dialog and set current date to today
            val dateDialog : DatePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth
                        -> run {
                            // first set the text of the edittext to reflect the change
                            datePicker.setText("${dayOfMonth.toString()}/${(month+1).toString()}/${year.toString()}")
                            // then get the day of the week by creating a Calendar instance with the passed
                            // data and set the day repetition buttons accordingly
                            val setCalendar : Calendar = Calendar.getInstance()
                            setCalendar.set(year, month, dayOfMonth)
                            val day = setCalendar.get(Calendar.DAY_OF_WEEK) - 1
                            dayBtns[day]!!.setBackgroundResource(R.drawable.roundedbuttonselected)
                            dayBtnActives[day] = true
                            dayBtnAllows = BooleanArray(dayBtns.size, { x -> true })
                            dayBtnAllows[day] = false  // do not allow the user to change the button unless the date is changed
                        } }
                    , cYear, cMonth, cDay)
            dateDialog.setTitle("Select Date")
            dateDialog.show()
        }

        // send user to a TimePickerDialog when they tap on the timePicker edittext
        timePicker.setOnClickListener {
            // get the current time
            val cCurrentTime : Calendar = Calendar.getInstance()
            val cHour : Int = cCurrentTime.get(Calendar.HOUR_OF_DAY)
            val cMinute : Int = cCurrentTime.get(Calendar.MINUTE)

            // create time picker dialog and set current time to now
            val timeDialog : TimePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener { view, hour, minute
                        -> timePicker.setText("${hour.toString()}:${minute.toString()}") }
                    , cHour, cMinute, true)
            timeDialog.setTitle("Select Time")
            timeDialog.show()
        }

        // send user to the contacts contract activity to get the phone number to send reminder to
        (contact as EditText).setOnClickListener {
            val contactsPicker : Intent = Intent(Intent.ACTION_PICK
                                        , ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(contactsPicker, RESULT_PICK_CONTACT)
        }

        // save the reminder when user taps on the send button
        sendBtn.setOnClickListener {
            // check for permission to send SMS messages. If not, ask user for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                        PERMISSIONS_REQUEST_SEND_SMS)

            // get data from UI elements and alert user if an error occurs
            val calendar : Calendar = Calendar.getInstance()
            val date : MutableList<String>?  // note that a list is required because that is what is returned
            val time : List<String>?
            val numReps = numRepsET.text.toString().toInt()
            var name = ""
            var phoneNo = ""
            var msg = ""
            try {
                date = datePicker.text.toString().split("/") as MutableList<String>
                // must set month -1 because data shown to user is indexed from 1, not 0
                date[1] = (date[1].toInt()-1).toString()
                time = timePicker.text.toString().split(":")
                val info = (contact as EditText).text.toString().replace(" ", "").split("at")
                name = info[0]
                phoneNo = info[1]
                msg = message.text.toString()

                // set calendar to entered time and date
                calendar.set(date[2].toInt(), date[1].toInt(), date[0].toInt(), time[0].toInt(), time[1].toInt(), 0)

                // test if desired time is after current time
                val curCal : Calendar = Calendar.getInstance()
                if (curCal >= calendar) {
                    displayWarning(this, "Please choose a time in the future")
                    return@setOnClickListener
                }
            } catch (e : Exception) {
                e.printStackTrace()
                displayWarning(this, "Please enter valid input.")
                return@setOnClickListener
            }

            // create alarm manager to schedule reminders
            val alarmMgr : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent : Intent = Intent(this, AlarmReceiver::class.java)
            // send the user formatted month to the alarm receiver (because it does not
            // depend on this as the alarm manager handles this)
            // the alarm manager only depends on the reminders ArrayList
//            intent.putExtra("dates", this.getDates(calendar, dayBtnActives))
//            intent.putExtra("time", time.joinToString(":"))
//            intent.putExtra("name", name)
//            intent.putExtra("number", phoneNo)
//            intent.putExtra("message", msg)
            intent.putExtra("index", index)
            val alarmIntent : PendingIntent?
            // check if it's a new reminder
            if (index < 0) {
                alarmIntent = PendingIntent.getBroadcast(this, if (reminders != null) reminders!!.size else 0, intent, 0)
                reminders!!.add(Reminder(this.getDates(calendar, dayBtnActives).toTypedArray(),
                        numReps, time.toTypedArray(), name, phoneNo, msg))
            } else {
                alarmIntent = PendingIntent.getBroadcast(this, index, intent, 0)
                reminders!!.set(index, Reminder(this.getDates(calendar, dayBtnActives).toTypedArray(),
                        numReps, time.toTypedArray(), name, phoneNo, msg))
            }

            // set alarm and go back to main activity
            for (day in this.getDates(calendar, dayBtnActives)) {
                val cal = Calendar.getInstance()
                val d = day.split("/")
                cal.set(d[2].toInt(), d[1].toInt(), d[0].toInt(), time[0].toInt(), time[1].toInt(), 0)
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY * 7, alarmIntent)
            }
            goToMain()
        }

        // go back to main screen without saving the reminder
        backBtn.setOnClickListener {
            // we only have to remove a reminder if we are editing a current one
            if (index >= 0) {
                // get the alarm manager corresponding to this reminder and cancel its pendingIntent
                val alarmMgr = getSystemService(ALARM_SERVICE) as AlarmManager
                val rmIntent = Intent(this, AlarmReceiver::class.java)
                val pendIntent = PendingIntent.getBroadcast(this, index, rmIntent, 0)
                alarmMgr.cancel(pendIntent)
                // ensure reminders isn't null and remove the reminder we are editing
                if (reminders == null) reminders = getReminders(this, filename)
                (reminders as ArrayList<Reminder>).removeAt(index)
            }
            goToMain()
        }

    }

    // called when an activity returns a result to this activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if the activity returns with no errors
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                // if the user picked a contact parse it
                RESULT_PICK_CONTACT -> (contact as EditText).setText(parseSelectedContact(data!!))
            }
        } else {
            Log.e("EditActivity", "Failed to pick contact") // log that the user hasn't selected a contact
        }
    }

    // called when the user replies to a permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when (requestCode) {
            // if the user hasn't granted SMS permission tell them the reminder will fail
            PERMISSIONS_REQUEST_SEND_SMS -> {
                if (grantResults != null && grantResults.isEmpty() &&
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    displayWarning(this, "SMS will fail: Incorrect permissions")
                }
            }
        }
    }

    // function to set the repeating alarms (will return a string array of length true dayOfWeeks
    private fun getDates(startDate : Calendar, dayOfWeeks : BooleanArray) : ArrayList<String> {
        val retDates : Array<Calendar?> = arrayOfNulls(dayOfWeeks.filter { it }.size)
        retDates[0] = startDate
        for (r in 1..retDates.size-1) {
            retDates[r] = retDates[r-1]
            (0..dayOfWeeks.size)
                    .filter { dayOfWeeks[it] }
                    .forEach { retDates[r]!!.set(Calendar.DAY_OF_WEEK, it +1) }
        }

        val dates = arrayListOf<String>()
        (0..retDates.size-1).mapTo(dates) {
            arrayOf(retDates[it]!!.get(Calendar.DAY_OF_MONTH).toString(),
                    retDates[it]!!.get(Calendar.MONTH).toString(),
                    retDates[it]!!.get(Calendar.YEAR).toString()).joinToString("/")
        }

        return dates
    }

    private fun parseSelectedContact(data : Intent) : String {
        try {
            // get data and cursor from intent
            val uri: Uri = data.data
            val cursor: Cursor = contentResolver.query(uri, null, null, null, null)
            // move cursor to first returned result
            cursor.moveToFirst()
            // get the required data
            val phoneNo: String = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER))
            val name: String = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

            return "$name at $phoneNo"
        } catch (e: Exception) {
            e.printStackTrace()
            displayWarning(this, "Failed to retrieve contact data")
            return ""
        }
    }

    // close this activity and return to main activity
    // (without reloading the main activity as the RemindersArrayAdapter will detect a change in reminders)
    private inline fun goToMain () = this.finish()
}


