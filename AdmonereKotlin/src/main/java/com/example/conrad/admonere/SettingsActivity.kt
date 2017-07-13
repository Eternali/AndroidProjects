package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Switch

class SettingsActivity : Activity () {

    // this is to override back button behaviour so that if the user changes the theme,
    // the mainActivity will reload to apply the theme.
    var shouldReload : Boolean = false

    // method for overriding backbutton pressed
    override fun onBackPressed() {
        // start the MainActivity intent and finish this one (garbage collect because the user will
        // never press the back button to get back to this activity)
        val mainIntent : Intent = Intent(this, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(mainIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // set the theme
        setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        val darkSwitch = findViewById(R.id.darkSwitch) as Switch
        val sharedPref : SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDark : Boolean = sharedPref.getBoolean(getString(R.string.isdark), false)
        darkSwitch.isChecked = isDark
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val sharePref : SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
            val spEditor : SharedPreferences.Editor = sharePref.edit()
            spEditor.putBoolean(getString(R.string.isdark), b)
            spEditor.apply()
            recreate()
            shouldReload = !shouldReload
        } }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0 && shouldReload) {
            onBackPressed()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}