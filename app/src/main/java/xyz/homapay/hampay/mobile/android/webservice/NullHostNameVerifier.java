package xyz.homapay.hampay.mobile.android.webservice;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by amir on 8/12/15.
 */
public class NullHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
//        Log.e("RestUtilImpl", "Approving certificate for " + hostname);
        return true;
    }
}