package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.mobile.android.model.LoginData;
import com.hampay.mobile.android.model.LoginResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/23/15.
 */
public class RequestLogin extends AsyncTask<LoginData, Void, LoginResponse> {

    private static final String TAG = "RequestLogin";

    private Context context;
    private AsyncTaskCompleteListener<LoginResponse> listener;
    private LoginResponse loginResponse;


    public RequestLogin(Context context, AsyncTaskCompleteListener<LoginResponse> listener)
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
    protected LoginResponse doInBackground(LoginData... params) {

        WebServices webServices = new WebServices(context);

        try {
            loginResponse = webServices.sendLoginRequest(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loginResponse;

    }


    @Override
    protected void onPostExecute(LoginResponse loginResponse)
    {
        super.onPostExecute(loginResponse);
        listener.onTaskComplete(loginResponse);
    }


}

