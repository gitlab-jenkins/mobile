package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 8/12/15.
 */
public class SSLConnection  {

    private Context context;
    private URL url;
    private SSLKeyStore sslKeyStore;

    public SSLConnection(Context context, URL url){
        this.context = context;
        this.url = url;
        this.sslKeyStore = new SSLKeyStore(this.context);
    }

    public HttpsURLConnection setUpHttpsURLConnection(){
        try
        {
            SSLContext context;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context = SSLContext.getInstance("TLSv1.2");
            }else {
                context = SSLContext.getInstance("TLS");
            }
            context.init(null, new TrustManager[] { new HamPayX509TrustManager(sslKeyStore.getAppKeyStore()) }, null);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());
            urlConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
            urlConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
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
