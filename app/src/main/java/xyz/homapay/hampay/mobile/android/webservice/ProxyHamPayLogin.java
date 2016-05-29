package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 3/9/16.
 */
public class ProxyHamPayLogin {

    private Context context;
    private HttpURLConnection httpURLConnection;
    private HttpsURLConnection httpsURLConnection;
    private ConnectionType connectionType;
    private ConnectionMethod connectionMethod;
    private URL url;
    private BufferedReader bufferedReader;

    public ProxyHamPayLogin(Context context, ConnectionType connectionType, ConnectionMethod connectionMethod, URL url){
        this.context = context;
        this.connectionType = connectionType;
        this.connectionMethod = connectionMethod;
        this.url = url;
    }


    public int hamPayLogin(LoginData loginData) throws IOException {

        BufferedWriter output;
        int responseCode = 0;

        switch (connectionType){
            case HTTP:
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpURLConnection.setRequestProperty("username", loginData.getUserName());
                httpURLConnection.setRequestProperty("password", loginData.getUserPassword());
                httpURLConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
//                httpURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");
                try {
                    output = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                    output.write("{}");
                    output.flush();
                    output.close();
                } catch (Exception e) {}
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                responseCode = httpURLConnection.getResponseCode();
                break;

            case HTTPS:
                httpsURLConnection = new SSLConnection(context, url).setUpHttpsURLConnection();
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
                httpsURLConnection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
                httpsURLConnection.setRequestProperty("username", loginData.getUserName());
                httpsURLConnection.setRequestProperty("password", loginData.getUserPassword());
                httpsURLConnection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
                httpsURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");
                try {
                    output = new BufferedWriter(new OutputStreamWriter(httpsURLConnection.getOutputStream()));
                    output.write("{}");
                    output.flush();
                    output.close();
                } catch (Exception e) {}
                responseCode = httpsURLConnection.getResponseCode();
                if (responseCode == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                }else {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getErrorStream()));
                }
                break;
        }
        return responseCode;

    }

    public String hamPaySuccessLogin() throws Exception {

        String inputLine;
        StringBuffer response;
        response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
    }

    public String hamPayFailLogin() throws Exception {

        String inputLine;
        StringBuffer response;
        response = new StringBuffer();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        return response.toString();
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
