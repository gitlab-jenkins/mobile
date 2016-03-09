package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import xyz.homapay.hampay.mobile.android.model.LogoutData;
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
    private ConnectionType connectionType;
    private ConnectionMethod connectionMethod;
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

    public ProxyService(Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url){
        this.context = context;
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
    }

    public ProxyService(Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url, boolean enableGZip){
        this.context = context;
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
        this.enableGZip = enableGZip;
    }


    public InputStreamReader getInputStreamReader() throws IOException {

        OutputStream outputStream;
        String encoding;
        InputStreamReader inputStreamReader = null;
        boolean gzipped;

        switch (connectionType){
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
                httpURLConnection.setRequestMethod(connectionMethod.name());
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
                httpsURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpsURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpsURLConnection.setRequestMethod(connectionMethod.name());
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

    public StringBuffer hamPaylogout(LogoutData logoutData) throws IOException {

        BufferedWriter output;
        StringBuffer response = null;
        BufferedReader bufferedReader;
        String inputLine;

        switch (connectionType){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpURLConnection.setRequestMethod(connectionMethod.name());
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("iplanetDirectoryPro", logoutData.getIplanetDirectoryPro());
                httpURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");
                output = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                output.write("");
                output.flush();
                output.close();
                bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream()));
                response = new StringBuffer();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();
                break;

            case HTTPS:
                httpsURLConnection = new SSLConnection(context, url).setUpHttpsURLConnection();
                httpsURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpsURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpsURLConnection.setRequestMethod(connectionMethod.name());
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestProperty("iplanetDirectoryPro", logoutData.getIplanetDirectoryPro());
                httpsURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");
                output = new BufferedWriter(new OutputStreamWriter(httpsURLConnection.getOutputStream()));
                output.write("");
                output.flush();
                output.close();

                bufferedReader = new BufferedReader(
                        new InputStreamReader(httpsURLConnection.getInputStream()));
                response = new StringBuffer();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();
                break;
        }
        return response;

    }

    public Bitmap imageInputStream() throws IOException {

        InputStream inputStream;
        Bitmap bitmap = null;

        switch (connectionType){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod(connectionMethod.name());
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                break;

            case HTTPS:
                httpsURLConnection = new SSLConnection(context, url).setUpHttpsURLConnection();
                httpsURLConnection.setRequestMethod(connectionMethod.name());
                inputStream = httpsURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                break;
        }
        return bitmap;

    }


    public void closeConnection(){
        if (connectionType == ConnectionType.HTTP){
            httpURLConnection.disconnect();
        }
        if (connectionType == ConnectionType.HTTPS){
            httpsURLConnection.disconnect();
        }
    }


}
