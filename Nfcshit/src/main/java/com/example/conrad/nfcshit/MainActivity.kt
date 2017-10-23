package com.example.conrad.nfcshit

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val ERROR_DETECTED : String = "No NFC tag detected!"
    val WRITE_SUCCESS = "Text written to the NFC tag successfully!"
    val WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?"
    var nfcAdapter : NfcAdapter? = null
    var pendIntent : PendingIntent? = null
    var writeTagFilters : Array<IntentFilter>? = null
    val tag : Tag? = null
    var context : Context? = null
    var writeMode : Boolean = false

    var nfcContent : TextView? = null
    var message : TextView? = null
    var btnwrite : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = context

        nfcContent = findViewById(R.id.nfc_contents)
        message = findViewById(R.id.edit_message)
        btnwrite = findViewById(R.id.button)

        if (nfcContent == null || message == null || btnwrite == null) finish()

        (btnwrite as Button).setOnClickListener {
            try {
                if (tag == null) Toast.makeText(context, ERROR_DETECTED, Toast.LENGTH_LONG).show()
                else {
                    write(message.text.toString(), tag)
                }
            } catch (e : IOException) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                Log.e("IOException", e.stackTrace.toString())
            } catch (e : FormatException) {
                Toast.makeText(context, WRITE_ERROR, Toast.LENGTH_LONG).show()
                Log.e("FormatException", e.stackTrace.toString())
            }
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
        }
        readFromIntent(getIntent())

        pendIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        writeTagFilters = arrayOf(tagDetected)

    }

    fun readFromIntent (intent : Intent) {
        val action = intent.action.toString()
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action
                || NfcAdapter.ACTION_TECH_DISCOVERED == action
                || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs : Array<Parcelable>? = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msgs : Array<NdefMessage?>
            if (rawMsgs != null) {
                msgs = arrayOfNulls<NdefMessage>(rawMsgs.size)
                for (i : Int in 0..rawMsgs.size) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }
            } else return
            buildTagViews(msgs)
        }
    }

    fun buildTagViews (msgs : Array<NdefMessage?>) {
        if (null in msgs || msgs.isEmpty()) return
        var text = ""
        var payload : ByteArray = msgs[0]!!.records[0].payload
        var textEncoding : String= if (payload[0].toInt() and 128 == 0) "UTF-8" else "UTF-16"
        val languageCodeLength : Int = payload[0].toInt() and (0063)
    }

}
