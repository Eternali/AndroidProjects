package com.example.fa11en.syde161proto01

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.MenuPopupWindow


class EditActivity : Activity() {

    lateinit var bundle: Bundle

    lateinit var type: MenuPopupWindow.MenuDropDownListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        bundle = intent.extras

        if (bundle.getSerializable("data") != null) {

        }
    }

}