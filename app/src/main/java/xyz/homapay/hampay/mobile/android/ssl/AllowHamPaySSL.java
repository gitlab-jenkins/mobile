package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
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

        setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        javax.net.ssl.SSLContext sslContext = null;

        if (trustManagers == null) {
            try {
                trustManagers = new TrustManager[]{new
                        HamPayX509TrustManager(sslKeyStore.getAppKeyStore())};
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

        }

        try {
            sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
