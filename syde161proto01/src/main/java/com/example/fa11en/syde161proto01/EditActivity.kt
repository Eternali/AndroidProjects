package com.example.fa11en.syde161proto01

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.MenuPopupWindow
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner


class EditActivity : Activity() {

    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        bundle = intent.extras

        if (bundle.getString("typeName") == null) return

        //  set layout dynamically according to event type  //
        val root = findViewById<LinearLayout>(R.id.edit_activity_root)

        val typeSpinner = findViewById<Spinner>(R.id.eventTypeSpinner)
        val typeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eventTypes.keys.toList())
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(typeAdapter.getPosition(bundle.getString("typeName")))

    }

}