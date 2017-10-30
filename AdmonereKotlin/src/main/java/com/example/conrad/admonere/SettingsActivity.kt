package com.example.conrad.admonere

// import required libraries
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Switch

// this will return the theme that corresponds to the states of the preference switches
internal fun getNewTheme (ctx : Context, dark : Boolean, nav : Boolean) : String {
    if (!dark && !nav) return ctx.getString(R.string.lightno)
    else if (!dark && nav) return ctx.getString(R.string.lightyes)
    else if (dark && !nav) return ctx.getString(R.string.darkno)
    else if (dark && nav) return ctx.getString(R.string.darkyes)
    else return ""
}

class SettingsActivity : Activity () {

    // this is to override back button behaviour so that if the user changes the theme,
    // the mainActivity will reload to apply the theme.
    var srtheme : Boolean = false
    var srnav : Boolean = false

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

        srtheme = false
        srnav = false

        // get the two switches to control overall theme and the enabling of changing the navbar color
        // (some might not like it but I know some who will)
        val darkSwitch = findViewById(R.id.darkSwitch) as Switch
        val navbarSwitch = findViewById(R.id.navbarSwitch) as Switch
        val sharedPref : SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // get the saved settings from preferences and set the switches to match them
        val isDark : Boolean = sharedPref.getBoolean(getString(R.string.isdark), false)
        val isNav : Boolean = sharedPref.getBoolean(getString(R.string.navcolor), false)
        darkSwitch.isChecked = isDark
        navbarSwitch.isChecked = isNav

        // set the listeners for the switches and change the settings accordingly
        darkSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val theme = getNewTheme(this, b, isNav)
            val spEditor : SharedPreferences.Editor = sharedPref.edit()
            if (theme.isNotEmpty()) {
                spEditor.putBoolean(getString(R.string.isdark), b)
                spEditor.putString(getString(R.string.theme), theme)
            }

            spEditor.apply()
            recreate()
            srtheme = !srtheme
        } }

        navbarSwitch.setOnCheckedChangeListener { compoundButton, b -> run {
            val theme = getNewTheme(this, isDark, b)
            val spEditor : SharedPreferences.Editor = sharedPref.edit()
            if (theme.isNotEmpty()) {
                spEditor.putBoolean(getString(R.string.navcolor), b)
                spEditor.putString(getString(R.string.theme), theme)
            }

            spEditor.apply()
            recreate()
            srnav = !srnav
        } }

    }

    // this is called when a key is pressed
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // I will check if we want to reload the application before the user leaves (presses back button)
        // and if so restart the mainActivity
        if (event != null && keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0 && (srtheme || srnav)) {
            onBackPressed()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}