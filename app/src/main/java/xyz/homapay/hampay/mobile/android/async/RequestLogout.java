package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            logoutResponse = webServices.logoutRequest(params[0]);
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

