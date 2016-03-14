package xyz.homapay.hampay.mobile.android.webservice;

import android.content.Context;
import android.util.Log;

import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.ssl.SSLConnection;
import xyz.homapay.hampay.mobile.android.util.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
    ProxyHamPayLogin proxyHamPayLogin;
    URL url;


    public LoginStream(Context context, LoginData loginData) throws IOException{
        this.context = context;
        this.loginData = loginData;
        if (Constants.CONNECTION_TYPE == ConnectionType.HTTPS) {
            url = new URL(Constants.HTTPS_OPENAM_LOGIN_URL);
        }else {
            url = new URL(Constants.HTTP_OPENAM_LOGIN_URL);
        }
        proxyHamPayLogin = new ProxyHamPayLogin(context, Constants.CONNECTION_TYPE, ConnectionMethod.POST, url);
    }

    public int resultCode() throws Exception {
        int responseCode = proxyHamPayLogin.hamPayLogin(loginData);
        return responseCode;

    }

    public String successLogin() throws Exception {

      return proxyHamPayLogin.hamPaySuccessLogin();

    }

    public String failLogin() throws Exception {

       return proxyHamPayLogin.hamPayFailLogin();
    }

    public void closeConnection(){
        proxyHamPayLogin.closeConnection();
    }

}