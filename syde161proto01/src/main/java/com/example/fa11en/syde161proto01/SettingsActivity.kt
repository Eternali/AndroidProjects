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
        if (group.getChildAt(checkedId) > -1) group.clearCheck()
        for (r in 0..group.childCount) {
            val view: ToggleButton = group.getChildAt(r) as ToggleButton
            if (view.id == checkedId) group.check(checkedId)
        }
        Log.i("Toggle clicked", Integer.toString(checkedId))
    }

    lateinit var listToggle: ToggleButton
    lateinit var calToggle: ToggleButton

    fun toggleDisplay(view: View) {
        val group = view.parent as RadioGroup
        group.clearCheck()
        group.check(view.id)
        Log.i("Toggle clicked", Integer.toString(view.id))
//        (view.parent as RadioGroup).check(view.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        listToggle = findViewById(R.id.listToggle)
        calToggle = findViewById(R.id.calendarToggle)

        findViewById<RadioGroup>(R.id.displayToggleGroup).setOnCheckedChangeListener(displayToggleListener)
    }

}
