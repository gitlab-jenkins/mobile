package com.hampay.mobile.android.messaging;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.hampay.mobile.android.R;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @author Siavash Mahmoudpour
 */
public class HttpClientHelper {

    private static final String TAG = HttpClientHelper.class.getName();

    public HttpClientHelper() {

    }


    public HttpClient getHttpClient() throws Exception {

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 30000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 30000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        return new DefaultHttpClient(httpParameters);
    }

    public HttpClient getSSLClient(Context context, int port, String protocol) throws Exception {
        try {

            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            AssetManager.AssetInputStream tbAssetInputStream = (AssetManager.AssetInputStream) context.getResources().openRawResource(R.raw.tbcert);
//            AssetManager.AssetInputStream tbAssetInputStream = (AssetManager.AssetInputStream) context.getResources().openRawResource(R.raw.server);
            AssetManager.AssetInputStream tbAssetInputStream = (AssetManager.AssetInputStream) context.getResources().openRawResource(R.raw.menginx);
            InputStream tbCaInput = new BufferedInputStream(tbAssetInputStream);
            X509Certificate tbCa;
            try {
                tbCa = (X509Certificate)cf.generateCertificate(tbCaInput);
//                Log.d(TAG, "Tejarat Bourse Certificate SubjectDN : " + tbCa.getSubjectDN());
//                Log.d(TAG, "ME Bourse Certificate SubjectDN : " + tbCa.getSubjectDN());
            } finally {
                tbCaInput.close();
                tbAssetInputStream.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", tbCa);

            // Create an SSLContext that uses our TrustManager
//            SSLContext ctx = SSLContext.getInstance("TLS");
//            SSLSocketFactory ssf = new RestSSLSocketFactory(tbCa, ctx, keyStore);
            SSLSocketFactory ssf = new RestSSLSocketFactory(keyStore);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            // Enable HTTP parameters
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 30000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 30000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);

            // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme(protocol, ssf, port));
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            // Create a new connection manager using the newly created registry and then create a new HTTP client
            // using this connection manager
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParameters, registry);
            DefaultHttpClient httpClient = new DefaultHttpClient(ccm, httpParameters);
            httpClient.setRoutePlanner(new DefaultHttpRoutePlanner(registry));
            return httpClient;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
