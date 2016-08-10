package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.content.SharedPreferences;
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

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.response.DecryptedResponseInfo;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.security.KeyExchange;
import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ConvertUtils;
import xyz.homapay.hampay.mobile.android.util.GZip;

/**
 * Created by amir on 2/24/16.
 */
public class SecuredProxyService {

    private Context context;
    private SharedPreferences prefs;
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;
    private ConnectionType connectionType;
    private ConnectionMethod connectionMethod;
    private String jsonBody;
    private URL url;
    private boolean enableGZip = false;
    private MessageEncryptor messageEncryptor;
    private boolean encryptionEnabled = false;
    private KeyExchange keyExchange;

    public void setJsonBody(String jsonBody) throws EncryptionException {
        if (encryptionEnabled){
            this.jsonBody = messageEncryptor.encryptRequest(jsonBody, keyExchange.getKey(), keyExchange.getIv(), keyExchange.getEncId());
        }else {
            this.jsonBody = jsonBody;
        }
    }

    private byte[] getJsonBody(){
        if (enableGZip){
            return new GZip(jsonBody.getBytes()).compress();
        }else {
            return jsonBody.getBytes();
        }
    }

    public SecuredProxyService(boolean encryptionEnabled, Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url){
        this.encryptionEnabled = encryptionEnabled;
        this.context = context;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
        keyExchange = new KeyExchange(context);
        messageEncryptor = new AESMessageEncryptor();
    }

    public SecuredProxyService(Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url){
        this.context = context;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
        keyExchange = new KeyExchange(context);
        messageEncryptor = new AESMessageEncryptor();
    }

    public SecuredProxyService(boolean encryptionEnabled, Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url, boolean enableGZip){
        this.encryptionEnabled = encryptionEnabled;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        this.context = context;
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
        this.enableGZip = enableGZip;
        keyExchange = new KeyExchange(context);
        messageEncryptor = new AESMessageEncryptor();
    }

    public SecuredProxyService(Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url, boolean enableGZip){
        this.context = context;
        prefs = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
        this.enableGZip = enableGZip;
        keyExchange = new KeyExchange(context);
        messageEncryptor = new AESMessageEncryptor();
    }


    public String getResponse() throws IOException, EncryptionException {

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

        if (encryptionEnabled){
            DecryptedResponseInfo decryptedResponseInfo = messageEncryptor.decryptResponse(new ConvertUtils().streamToString(inputStreamReader), keyExchange.getKey(), keyExchange.getIv());
            if (decryptedResponseInfo.getResponseCode() == 0){
                return decryptedResponseInfo.getPayload();
            }else {
                return "";
            }

        }else {
            return new ConvertUtils().streamToString(inputStreamReader);
        }
    }

    public Bitmap imageInputStream() throws IOException {

        OutputStream outputStream;
        InputStream inputStream;
        Bitmap bitmap = null;

        switch (connectionType){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod(connectionMethod.name());
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(getJsonBody());
                outputStream.flush();
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                break;

            case HTTPS:
                httpsURLConnection = new SSLConnection(context, url).setUpHttpsURLConnection();
                httpsURLConnection.setRequestMethod(connectionMethod.name());
                outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(getJsonBody());
                outputStream.flush();
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
