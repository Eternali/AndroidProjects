package com.example.conrad.admoneretest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private var curTab = 0

    fun setTab (tabs : Array<Button>) {
        for (tab in 0 until tabs.size) {
            if (curTab == tab) tabs[tab].setBackgroundResource(R.drawable.pagerectanglebuttonselected)
            else tabs[tab].setBackgroundResource(R.drawable.pagerectanglebutton)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ongoingBtn = findViewById<View>(R.id.ongoingButton) as Button
        val pastBtn = findViewById<View>(R.id.pastButton) as Button

        val tabs = arrayOf(ongoingBtn, pastBtn)
        setTab(tabs)

        tabs.forEach { it.setOnClickListener {
            if (tabs.indexOf(it) != curTab) {
                curTab = tabs.indexOf(it)
                setTab(tabs)
            }
        } }



    }
}
