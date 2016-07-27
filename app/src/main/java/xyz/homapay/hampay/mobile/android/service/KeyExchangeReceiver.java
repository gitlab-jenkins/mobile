package xyz.homapay.hampay.mobile.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by amir on 7/26/16.
 */
public class KeyExchangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentService = new Intent(context, KeyExchangeService.class);
        context.startService(intentService);
        Log.e("Autostart", "started");

    }
}
