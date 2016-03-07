package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.util.Log;

import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amir on 8/4/15.
 */
public class LoginStream {

    Context context;
    LoginData loginData;
    SSLConnection sslConnection;
    HttpsURLConnection connection;


    public LoginStream(Context context, LoginData loginData) {
        this.context = context;
        this.loginData = loginData;
    }

    public int resultCode() throws Exception {

        URL url = new URL(Constants.HTTPS_OPENAM_LOGIN_URL);

        sslConnection = new SSLConnection(context, url);
        connection = sslConnection.setUpHttpsURLConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(Constants.SERVICE_CONNECTION_TIMEOUT);
        connection.setReadTimeout(Constants.SERVICE_READ_TIMEOUT);
        connection.setRequestProperty("username", loginData.getUserName());
        connection.setRequestProperty("password", loginData.getUserPassword());
        connection.setRequestProperty("Content-Type", Constants.SERVICE_CONTENT_TYPE);
        connection.setRequestProperty("Accept-Encoding", "UTF-8");

        try {

            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            output.write("{}");
            output.flush();
            output.close();
        } catch (Exception e) {
            Log.e("Fail", "Login Failed.");
        }

        return connection.getResponseCode();

    }

    public String successLogin() throws Exception {

        String inputLine;
        StringBuffer response;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    public String failLogin() throws Exception {

        String inputLine;
        StringBuffer response;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()));

        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

}