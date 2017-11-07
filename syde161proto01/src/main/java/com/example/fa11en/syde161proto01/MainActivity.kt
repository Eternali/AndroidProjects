package com.example.fa11en.syde161proto01

import android.app.FragmentTransaction
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.RadioGroup
import android.widget.ToggleButton
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import java.util.*


enum class ParameterTypes {
    TITLE,
    DESCRIPTION,
    DATETIME,
    LOCATION,
    ENTITIES,
    REPEAT
}

internal var eventTypes = hashMapOf(
        "EVENT" to EventType("EVENT",
                                    arrayListOf(ParameterTypes.TITLE,
                                            ParameterTypes.DESCRIPTION,
                                            ParameterTypes.DATETIME,
                                            ParameterTypes.LOCATION,
                                            ParameterTypes.ENTITIES)),
        "EVALUATION" to EventType("EVALUATION",
                                    arrayListOf(ParameterTypes.TITLE,
                                            ParameterTypes.DESCRIPTION,
                                            ParameterTypes.DATETIME)),
        "PROJECT" to EventType("PROJECT",
                                    arrayListOf(ParameterTypes.TITLE,
                                            ParameterTypes.DESCRIPTION,
                                            ParameterTypes.DATETIME)),
        "REMINDER" to EventType("REMINDER",
                                    arrayListOf(ParameterTypes.TITLE,
                                            ParameterTypes.DESCRIPTION,
                                            ParameterTypes.DATETIME))
        )
internal var events: MutableList<UserEvent> = ArrayList()

class MainActivity : AppCompatActivity() {

    fun getEvents (events: MutableList<UserEvent>) {
        events.add(UserEvent(eventTypes["EVENT"]!!))
        events[events.size-1].setParam(ParameterTypes.TITLE, "TEST TITLE")
        events[events.size-1].setParam(ParameterTypes.DESCRIPTION, "TEST DESCRIPTION")
        events[events.size-1].setParam(ParameterTypes.DATETIME, Date())
        events[events.size-1].setParam(ParameterTypes.LOCATION, Location("gps"))
    }

    val displayToggleListener: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        for (r in 0..group.childCount) {
            if (group.getChildAt(r) == null) continue
            val view: ToggleButton = group.getChildAt(r) as ToggleButton
            view.isChecked = view.id == checkedId
        }
    }

    lateinit private var dotMenu: Menu
    lateinit var displayGroup: RadioGroup
    lateinit var dayToggle: ToggleButton
    lateinit var weekToggle: ToggleButton
    lateinit var monthToggle: ToggleButton

    lateinit var addMenu: FloatingActionsMenu

    fun toggleDisplay (view: View) {
        displayGroup.clearCheck()
        displayGroup.check(view.id)

        val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        when (view.id) {
            R.id.dayToggle -> {
                fragTransaction.replace(R.id.displayFragContainer, DayFragment(), "Day")
            }
            R.id.weekToggle -> {
                fragTransaction.replace(R.id.displayFragContainer, WeekFragment(), "Week")
            }
            R.id.monthToggle -> {
                fragTransaction.replace(R.id.displayFragContainer, MonthFragment(), "Month")
            }
        }
        fragTransaction.commit()
    }

    fun addMenuActions (view: View) {
        val editIntent = Intent(this, EditActivity::class.java)
//        val event = UserEvent()
//        when (view.id) {
//            R.id.action_addEvent -> {
//                event.type = EventTypes.EVENT
//            }
//            R.id.action_addDueDate -> {
//                event.type = EventTypes.DUEDATE
//            }
//            R.id.action_addProject -> {
//                event.type = EventTypes.PROJECT
//            }
//            R.id.action_addReminder -> {
//                event.type = EventTypes.REMINDER
//            }
//        }
//        editIntent.putExtra("data", event)
        startActivity(editIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getEvents(events)

        displayGroup = findViewById(R.id.overviewLayoutSwitcher)
        dayToggle = findViewById(R.id.dayToggle)
        weekToggle = findViewById(R.id.weekToggle)
        monthToggle = findViewById(R.id.monthToggle)

        displayGroup.setOnCheckedChangeListener(displayToggleListener)

        val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragTransaction.replace(R.id.displayFragContainer, WeekFragment(), "Week")
        fragTransaction.commit()

        addMenu = findViewById<FloatingActionsMenu>(R.id.addMenu)

        (addMenu.getChildAt(1) as FloatingActionButton).title = "Testing"
        // generate event adding buttons
//        for (c in 0..addMenu.childCount-2) {
//            (addMenu.getChildAt(c) as FloatingActionButton).title = eventTypes.keys.elementAt(c)
//            (addMenu.getChildAt(c) as FloatingActionButton).setOnClickListener {
//                val editIntent = Intent(this, EditActivity::class.java)
//                startActivity(editIntent)
//            }
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null) return false
        menuInflater.inflate(R.menu.dot_menu, menu)
        dotMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
        }

        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            dotMenu.performIdentifierAction(R.id.dot_menu, 0)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}
