package com.hampay.mobile.android.webservice;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hampay.mobile.android.model.LoginData;
import com.hampay.mobile.android.model.SuccessLoginResponse;
import com.hampay.mobile.android.util.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
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

        sslConnection = new SSLConnection(context, Constants.HTTPS_OPENAM_LOGIN_URL);
        connection = sslConnection.setUpHttpsURLConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(20 * 1000);
        connection.setReadTimeout(20 * 1000);

        connection.setDoOutput(true);

        connection.setRequestMethod("POST");

        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("username", loginData.getUserName());
        connection.setRequestProperty("password", loginData.getUserPassword());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Encoding", "UTF-8");

        try {

            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            output.write("{}");
            output.flush();
            output.close();
        } catch (Exception e) {
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