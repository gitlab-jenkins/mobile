package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.GZip;

/**
 * Created by amir on 2/24/16.
 */
public class ProxyService {

    private Context context;
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;
    private ConnectionType type;
    private ConnectionMethod method;
    private String jsonBody;
    private URL url;
    private boolean enableGZip = false;

    public void setJsonBody(String jsonBody){
        this.jsonBody = jsonBody;
    }

    private byte[] getJsonBody(){
        if (enableGZip){
            return new GZip(jsonBody.getBytes()).compress();
        }else {
            return jsonBody.getBytes();
        }
    }

    public ProxyService(Context context, ConnectionType type, ConnectionMethod method, URL url){
        this.context = context;
        this.type = type;
        this.method = method;
        this.url = url;
    }

    public ProxyService(Context context, ConnectionType type, ConnectionMethod method, URL url, boolean enableGZip){
        this.context = context;
        this.type = type;
        this.method = method;
        this.url = url;
        this.enableGZip = enableGZip;
    }


    public InputStreamReader getInputStreamReader() throws IOException {

        OutputStream outputStream;
        String encoding;
        InputStreamReader inputStreamReader = null;
        boolean gzipped;

        switch (type){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                if (enableGZip){
                    httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
                }else {
                    httpURLConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
                }
                httpURLConnection.setRequestMethod(method.name());
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(getJsonBody());
                outputStream.flush();
                encoding = httpURLConnection.getHeaderField("Content-Encoding");
                gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
                if (gzipped){
                    InputStream gzipInputStream = new GZIPInputStream(httpURLConnection.getInputStream());
                    inputStreamReader = new InputStreamReader(gzipInputStream);
                }else {
                    inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                }

                break;

            case HTTPS:
                httpsURLConnection = new SSLConnection(context, url).setUpHttpsURLConnection();
                httpsURLConnection.setRequestMethod(method.name());
                if (enableGZip){
                    httpsURLConnection.setRequestProperty("Content-Encoding", "gzip");
                    httpsURLConnection.setRequestProperty("Accept-Encoding", "gzip");
                }else {
                    httpsURLConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
                }
                outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(getJsonBody());
                outputStream.flush();
                encoding = httpsURLConnection.getHeaderField("Content-Encoding");
                gzipped = encoding != null && encoding.toLowerCase().contains("gzip");
                if (gzipped){
                    InputStream gzipInputStream = new GZIPInputStream(httpsURLConnection.getInputStream());
                    inputStreamReader = new InputStreamReader(gzipInputStream);
                }else {
                    inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
                }

                break;
        }


        return inputStreamReader;

    }



    public void closeConnection(){
        if (httpURLConnection != null){
            httpURLConnection.disconnect();
        }
    }


}
