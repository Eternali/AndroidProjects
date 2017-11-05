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
import android.widget.RadioGroup
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

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

    fun toggleDisplay (view: View) {
        displayGroup.clearCheck()
        displayGroup.check(view.id)

        if (view.id == R.id.monthToggle) {
            val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            val monthFrag = monthFragment()
            fragTransaction.add(R.id.displayFragContainer, monthFrag, "Month")
            fragTransaction.commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayGroup = findViewById(R.id.overviewLayoutSwitcher)
        dayToggle = findViewById(R.id.dayToggle)
        weekToggle = findViewById(R.id.weekToggle)
        monthToggle = findViewById(R.id.monthToggle)

        displayGroup.setOnCheckedChangeListener(displayToggleListener)
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
