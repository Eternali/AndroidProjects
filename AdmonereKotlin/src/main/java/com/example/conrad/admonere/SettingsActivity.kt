package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch

class SettingsActivity : Activity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the theme
//        val isDark : Boolean = setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val darkSwitch : Switch = findViewById(R.id.darkSwitch) as Switch
//        darkSwitch.isChecked = isDark
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val sharePref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
            val spEditor : SharedPreferences.Editor = sharePref.edit()
            spEditor.putBoolean(getString(R.string.isdark), b)
            spEditor.commit()
            recreate()
        } }

    }

}