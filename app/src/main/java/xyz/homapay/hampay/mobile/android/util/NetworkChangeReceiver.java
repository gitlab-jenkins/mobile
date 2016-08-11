package xyz.homapay.hampay.mobile.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by amir on 8/10/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkConnectivity networkConnectivity = new NetworkConnectivity();
        boolean isOnline = networkConnectivity.isOnline(context);
        if (isOnline) {
            Intent in = new Intent("network.intent.MAIN").putExtra("get_status", isOnline);
            context.sendBroadcast(in);
        }
    }
}
