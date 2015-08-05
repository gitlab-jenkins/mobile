package com.hampay.mobile.android.webservice;

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

/**
 * Created by amir on 8/4/15.
 */
public class LoginStream {

    LoginData loginData;

    HttpURLConnection httpURLConnection;

    public LoginStream(LoginData loginData) {
        this.loginData = loginData;
    }

    public int resultCode() throws Exception {


        URL urlConnection = new URL(Constants.OPENAM_LOGIN_URL);
        httpURLConnection = (HttpURLConnection) urlConnection.openConnection();

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(20 * 1000);
        httpURLConnection.setReadTimeout(20 * 1000);

        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestProperty("X-OpenAM-Username", loginData.getUserName());
        httpURLConnection.setRequestProperty("X-OpenAM-Password", loginData.getUserPassword());
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept-Encoding", "UTF-8");

        try {

            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            output.write("{}");
            output.flush();
            output.close();
        } catch (Exception e) {
        }

        return httpURLConnection.getResponseCode();

    }

    public String successLogin() throws Exception {

        String inputLine;
        StringBuffer response;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream()));

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
                new InputStreamReader(httpURLConnection.getErrorStream()));

        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

}
