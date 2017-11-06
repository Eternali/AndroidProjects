package com.example.fa11en.syde161proto01

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.ToggleButton
import java.util.*


fun getEvents (events: ArrayList<UserEvent>) {
    events.add(UserEvent(Date(), "TEST", "Test description"))
}


class MainActivity : AppCompatActivity() {

    val displayToggleListener: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        for (r in 0..group.childCount) {
            if (group.getChildAt(r) == null) continue
            val view: ToggleButton = group.getChildAt(r) as ToggleButton
            view.isChecked = view.id == checkedId
        }
    }

    lateinit var events: ArrayList<UserEvent>

    lateinit private var dotMenu: Menu
    lateinit var displayGroup: RadioGroup
    lateinit var dayToggle: ToggleButton
    lateinit var weekToggle: ToggleButton
    lateinit var monthToggle: ToggleButton

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
