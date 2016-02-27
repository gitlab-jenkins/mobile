package xyz.homapay.hampay.mobile.android.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by amir on 7/29/15.
 */
public class SmsActivation extends BroadcastReceiver {


    public void onReceive(Context context, Intent intent) {


        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;
        String sender = "";
        String body = "";

        if (myBundle != null)
        {
            Object[] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                sender = messages[i].getOriginatingAddress();
                body = messages[i].getMessageBody();
            }
        }

        Intent in = new Intent("SmsMessage.intent.MAIN").putExtra(
                    "get_msg", sender + ":" + body);
        if (sender.contains("300042178") || sender.contains("10008096")) {
                abortBroadcast();
                context.sendBroadcast(in);
        }
    }
}
