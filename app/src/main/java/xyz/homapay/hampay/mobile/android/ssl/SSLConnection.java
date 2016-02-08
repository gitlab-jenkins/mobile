package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by amir on 8/12/15.
 */
public class SSLConnection  {

    private Context context;
    private String urlString;
    private SSLKeyStore sslKeyStore;

    public SSLConnection(Context context, String urlString){
        this.context = context;
        this.urlString = urlString;
        this.sslKeyStore = new SSLKeyStore(this.context);
    }

    public HttpsURLConnection setUpHttpsURLConnection(){
        try
        {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { new HamPayX509TrustManager(sslKeyStore.getAppKeyStore()) }, null);
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setHostnameVerifier(new NullHostNameVerifier());
            return urlConnection;
        }

        catch (IOException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
        catch (KeyStoreException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
        catch (NoSuchAlgorithmException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
        }
        catch (KeyManagementException m)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + m.toString());
            return null;
        }
    }

}
