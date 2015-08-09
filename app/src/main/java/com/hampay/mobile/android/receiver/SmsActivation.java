package com.hampay.mobile.android.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by amir on 7/29/15.
 */
public class SmsActivation extends BroadcastReceiver {


    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = SMessage.getOriginatingAddress();
            String body = SMessage.getMessageBody().toString();

            Intent in = new Intent("SmsMessage.intent.MAIN").putExtra(
                    "get_msg", sender + ":" + body);

            if (sender.contains("300042178") || sender.contains("10008096")) {
                abortBroadcast();
                context.sendBroadcast(in);
            }

        }
    }
}
