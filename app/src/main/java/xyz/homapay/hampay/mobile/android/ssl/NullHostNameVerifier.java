package xyz.homapay.hampay.mobile.android.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 8/12/15.
 */
public class NullHostNameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        return hostnameVerifier.verify(Constants.SERVER, session);
    }
}
