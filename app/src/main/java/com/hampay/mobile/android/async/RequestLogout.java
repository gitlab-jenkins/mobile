package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.mobile.android.model.LogoutData;
import com.hampay.mobile.android.model.LogoutResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/23/15.
 */
public class RequestLogout extends AsyncTask<LogoutData, Void, LogoutResponse> {

    private static final String TAG = "RequestLogout";

    private Context context;
    private AsyncTaskCompleteListener<LogoutResponse> listener;
    private LogoutResponse logoutResponse;


    public RequestLogout(Context context, AsyncTaskCompleteListener<LogoutResponse> listener)
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
    protected LogoutResponse doInBackground(LogoutData... params) {

        WebServices webServices = new WebServices(context);

        try {
            logoutResponse = webServices.sendLogoutRequest(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return logoutResponse;

    }


    @Override
    protected void onPostExecute(LogoutResponse logoutResponse)
    {
        super.onPostExecute(logoutResponse);
        listener.onTaskComplete(logoutResponse);
    }


}

