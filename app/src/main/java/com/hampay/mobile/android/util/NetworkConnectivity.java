package com.hampay.mobile.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by amir on 7/3/15.
 */
public class NetworkConnectivity {

    Context context;

    public NetworkConnectivity(Context context){
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

}
