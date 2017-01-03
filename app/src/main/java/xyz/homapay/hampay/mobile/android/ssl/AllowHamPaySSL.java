package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import static javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier;

/**
 * Created by amir on 2/8/16.
 */
public class AllowHamPaySSL {

    private TrustManager[] trustManagers;
    private SSLKeyStore sslKeyStore;
    Context context;

    public AllowHamPaySSL(Context context){
        this.context = context;
        sslKeyStore = new SSLKeyStore(this.context);
    }

    public void enableHamPaySSL(){

        SSLContext sslContext;

        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sslContext = SSLContext.getInstance("TLSv1.2");
            } else {
                sslContext = SSLContext.getInstance("TLS");
            }
            sslContext.init(null, new TrustManager[]{new HamPayX509TrustManager(sslKeyStore.getTokenPayKeyStore())}, null);

        }
        catch (KeyStoreException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return;
        }
        catch (NoSuchAlgorithmException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return;
        }
        catch (KeyManagementException m)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + m.toString());
            return;
        }


        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

//        setDefaultHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });
//
//        javax.net.ssl.SSLContext sslContext = null;
//
//        if (trustManagers == null) {
//            try {
//                trustManagers = new TrustManager[]{new
//                        HamPayX509TrustManager(sslKeyStore.getAppKeyStore())};
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (KeyStoreException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        try {
//            sslContext = SSLContext.getInstance("TLSv1.2");
//            sslContext.init(null, trustManagers, new SecureRandom());
//        } catch (NoSuchAlgorithmException e) {
//        } catch (KeyManagementException e) {
//        }
//        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
