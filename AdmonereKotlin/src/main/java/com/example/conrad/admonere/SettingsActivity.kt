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

        // get the two switches to control overall theme and the enabling of changing the navbar color
        // (some might not like it but I know some who will)
        val darkSwitch = findViewById(R.id.darkSwitch) as Switch
        val navbarSwitch = findViewById(R.id.navbarSwitch) as Switch
        val sharedPref : SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val theme : String = sharedPref.getString(getString(R.string.theme), getString(R.string.lightno))

        when (theme) {
            getString(R.string.lightno) -> { darkSwitch.isChecked = false; navbarSwitch.isChecked = false }
            getString(R.string.lightyes) -> { darkSwitch.isChecked = false; navbarSwitch.isChecked = true }
            getString(R.string.darkno) -> { darkSwitch.isChecked = true; navbarSwitch.isChecked = false }
            getString(R.string.darkyes) -> { darkSwitch.isChecked = true; navbarSwitch.isChecked = true }
        }

        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val spEditor : SharedPreferences.Editor = sharedPref.edit()
            spEditor.putString()
        } }

        val isDark : Boolean = sharedPref.getBoolean(getString(R.string.isdark), false)
        val isNav : Boolean = sharedPref.getBoolean(getString(R.string.navcolor), false)
        darkSwitch.isChecked = isDark
        navbarSwitch.isChecked = isNav
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val spEditor : SharedPreferences.Editor = sharedPref.edit()
            spEditor.putBoolean(getString(R.string.isdark), b)
            spEditor.apply()
            recreate()
            shouldReload = !shouldReload
        } }

        navbarSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val spEditor : SharedPreferences.Editor = sharedPref.edit()
            spEditor.putBoolean(getString(R.string.navcolor), b)
            spEditor.apply()
            recreate()
            shouldReload = !shouldReload
        } }

    }

    // this is called when a key is pressed
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // I will check if we want to reload the application before the user leaves (presses back button)
        // and if so restart the mainActivity
        if (event != null && keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0 && shouldReload) {
            onBackPressed()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}