package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by amir on 2/8/16.
 */
public class AllowHamPaySSL {

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
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
