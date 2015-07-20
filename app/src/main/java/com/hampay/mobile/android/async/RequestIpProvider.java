package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.HamPaySSLSocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIpProvider extends AsyncTask<Void, Void, String> {

    private static final String TAG = "RequestIpProvider";

    private Context context;
    private AsyncTaskCompleteListener<String> listener;
    private String validIpAddress = "";


    public RequestIpProvider(Context context, AsyncTaskCompleteListener<String> listener)
    {
        this.context = context;
        this.listener = listener;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected String doInBackground(Void... params) {

        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(Constants.VALID_IP_ADDRESS_SERVICE);
            HttpResponse response = httpClient.execute(httpRequest);
            inStream = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()));

            StringBuffer buffer = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = inStream.readLine()) != null) {
                buffer.append(line + NL);
            }
            inStream.close();

            validIpAddress = buffer.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return validIpAddress.trim();
    }


    @Override
    protected void onPostExecute(String validIpAddress)
    {
        super.onPostExecute(validIpAddress);
        listener.onTaskComplete(validIpAddress);
    }



    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            HamPaySSLSocketFactory sf = new HamPaySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}

