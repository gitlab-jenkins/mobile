package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import xyz.homapay.hampay.mobile.android.util.HamPayX509TrustManager;

/**
 * Created by amir on 8/12/15.
 */
public class SSLConnection  {

    private Context context;
    private String urlString;

    public SSLConnection(Context context, String urlString){
        this.context = context;
        this.urlString = urlString;
    }

    public HttpsURLConnection setUpHttpsURLConnection(){
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/tejarat_nginx.crt"));
            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/nginx.crt"));
            Certificate certificate = certificateFactory.generateCertificate(caInput);
//            Log.e("ca=", ((X509Certificate) certificate).getSubjectDN() + "");
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

//            String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
//            trustManagerFactory.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");


            context.init(null, new TrustManager[] { new HamPayX509TrustManager(keyStore) }, null);

//            context.init(null, trustManagerFactory.getTrustManagers(), null);

            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            urlConnection.setHostnameVerifier(new NullHostNameVerifier());

            return urlConnection;

        }
        catch (CertificateException ex)
        {
            Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());
            return null;
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
