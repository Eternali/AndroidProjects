package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.os.Bundle
import android.widget.Switch

class SettingsActivity : Activity () {

    // checked state of theme
    var isDark : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val darkSwitch : Switch = findViewById(R.id.darkSwitch) as Switch
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> {
            if (b) {
                isDark = !isDark
            }
        } }

    }

}