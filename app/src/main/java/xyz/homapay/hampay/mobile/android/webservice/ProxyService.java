package xyz.homapay.hampay.mobile.android.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 2/24/16.
 */
public class ProxyService {

    private HttpURLConnection httpURLConnection;
    private SSLConnection httpsURLConnection;
    private ConnectionType type;
    private ConnectionMethod method;
    private InputStream inputStream;
    private String jsonBody;

    public void setJsonBody(String jsonBody){
        this.jsonBody = jsonBody;
    }

    public ProxyService(ConnectionType type, ConnectionMethod method){
        this.type = type;
        this.method = method;
    }


    public InputStreamReader getInputStreamReader(URL url) throws IOException {

        OutputStream outputStream;
        String encoding;
        InputStreamReader inputStreamReader = null;
        boolean gzipped;

        switch (type){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpURLConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
                httpURLConnection.setRequestMethod(method.name());
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(jsonBody.getBytes());
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
