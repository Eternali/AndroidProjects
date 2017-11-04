package com.example.fa11en.syde161proto01

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.ToggleButton

class SettingsActivity : Activity () {

    val displayToggleListener: RadioGroup.OnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        for (r in 0..group.childCount) {
            if (group.getChildAt(r) == null) continue
            val view: ToggleButton = group.getChildAt(r) as ToggleButton
            view.isChecked = view.id == checkedId
        }
    }

    lateinit var displayGroup: RadioGroup
    lateinit var listToggle: ToggleButton
    lateinit var calToggle: ToggleButton

    fun toggleDisplay(view: View) {
        displayGroup.clearCheck()
        displayGroup.check(view.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        displayGroup = findViewById(R.id.displayToggleGroup)
        listToggle = findViewById(R.id.listToggle)
        calToggle = findViewById(R.id.calendarToggle)

        displayGroup.setOnCheckedChangeListener(displayToggleListener)
    }

}
