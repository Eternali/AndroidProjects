package com.example.fa11en.admonere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;
    String phoneNo;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = (Button) findViewById(R.id.btnSendSMS);
        txtphoneNo = (EditText) findViewById(R.id.editText);
        txtMessage = (EditText) findViewById(R.id.editText2);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

    protected void sendSMSMessage() {
        phoneNo = txtphoneNo.getText().toString();
        message = txtMessage.getText().toString();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}



/*
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import org.w3c.dom.Text;

import java.lang.annotation.Target;


 * Admonere is latin for remind: this is meant to help people remind others of chores, events,
 * deadlines, etc.
 * @author Conrad Heidebrecht
 * @since 26/05/2017


public class MainActivity extends AppCompatActivity {

    Button btn;

    Button btnSend;
    EditText tvMessage;
    EditText tvNumber;
    IntentFilter intentFilter;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // display message to textview
            TextView inTxt = (TextView) findViewById(R.id.textMsg);
            inTxt.setText(intent.getExtras().getString("message"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.noti);

        // intent to filter for sms messages received
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        btnSend = (Button) findViewById(R.id.btnSend);
        tvMessage = (EditText) findViewById(R.id.tvMessage);
        tvNumber = (EditText) findViewById(R.id.tvNumber);

        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        btnSend.setOnClickListener(new View.OnClickListener () {
//            @Override
//            public void onClick (View view) {
//                String myMsg = tvMessage.getText().toString();
//                String theNumber = tvNumber.getText().toString();
//                sendMsg(theNumber, myMsg);
//            }
//        });
    }

    protected void sendMsg (String theNumber, String myMsg) {
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(theNumber, null, myMsg, sentPI, deliveredPI);
    }

    @Override
    protected void onResume () {
        // register the receiver
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause () {
        // unregister the receiver
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

    public void smsClick (View view) {
        String myMsg = tvMessage.getText().toString();
        String theNumber = tvNumber.getText().toString();
        sendMsg(theNumber, myMsg);
    }

    @TargetApi(16)
    public void sendNotification (View view) {
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Testing Notification")
                .setContentText("This is the text field")
                .build();
        notiMgr.notify(0, noti);
    }

}
*/

