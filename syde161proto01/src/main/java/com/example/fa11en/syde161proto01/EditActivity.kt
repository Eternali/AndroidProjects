package com.example.fa11en.syde161proto01

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*


class EditActivity : Activity() {

    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        bundle = intent.extras

        val typeName = bundle.getString("typeName")
        val parameters = bundle.getStringArrayList("parameters")

        if (typeName == null || typeName !in eventTypes.keys) return
        val event = UserEvent(eventTypes[typeName] as EventType)

        //  set layout dynamically according to event type  //

        val root = findViewById<ScrollView>(R.id.edit_activity_root)

        val typeSpinner = findViewById<Spinner>(R.id.eventTypeSpinner)
        val typeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                                               eventTypes.keys.toList())
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(typeAdapter.getPosition(typeName))

        // load the proper fragment for each type (and on type change)
        event.params.forEach {
            val linearParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            linearParams.setMargins(10, 10, 10, 10)
            when (it.key) {
                ParameterTypes.TITLE -> {
                    val curLayout = LinearLayout(this)
                    val labelText = TextView(this)
                    val titleEdit = EditText(this)

                    curLayout.orientation = LinearLayout.HORIZONTAL
                    curLayout.layoutParams = linearParams
                    labelText.layoutParams = LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            0.5f)
                    labelText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    labelText.text = it.key.param
                    titleEdit.layoutParams = LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            0.5f)
                    titleEdit.hint = it.key.param

                    curLayout.addView(labelText)
                    curLayout.addView(titleEdit)
                    root.addView(curLayout)
                }
            }
            root.invalidate()
        }

    }

}