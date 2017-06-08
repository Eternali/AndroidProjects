package com.example.conrad.contacts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static android.R.attr.data;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//    @Override
//    protected void onActivityResult (int requestCode, int resultCode,
//                                    Intent intent) {
//        Uri contactData = data.getData();
//        Cursor c = getContentResolver().query(contactData, null, null, null, null);
//        if (c.moveToFirst()) {
//            int phoneIndex = getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            String num = c.getString(phoneIndex);
//            Toast.makeText(MainActivity.this, "Number=" + num, Toast.LENGTH_LONG).show();
//        }
//    }

    public void contactList () {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, 1);
    }

}