package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Switch

class SettingsActivity : Activity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the theme
        if (!isDark) setTheme(R.style.AppTheme) else setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val darkSwitch : Switch = findViewById(R.id.darkSwitch) as Switch
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            isDark = !isDark
            finish()
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        } }

    }

}