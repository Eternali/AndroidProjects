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
        setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val darkSwitch : Switch = findViewById(R.id.darkSwitch) as Switch
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val sharePref : SharedPreferences = getPreferences(Context.MODE_PRIVATE)
            val spEditor : SharedPreferences.Editor = sharePref.edit()
            spEditor.putBoolean(getString(R.string.isdark), b)
            spEditor.commit()
            finish()
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        } }

    }

}