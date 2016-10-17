package xyz.homapay.hampay.mobile.android.async;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.LogoutRequest;
import xyz.homapay.hampay.common.common.response.LogoutResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestNewLogout extends AsyncTask<LogoutRequest, Void, ResponseMessage<LogoutResponse>> {

    private static final String TAG = "RequestNewLogout";

    private Activity context;
    private AsyncTaskCompleteListener<ResponseMessage<LogoutResponse>> listener;


    public RequestNewLogout(Activity context, AsyncTaskCompleteListener<ResponseMessage<LogoutResponse>> listener)
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
    protected ResponseMessage<LogoutResponse> doInBackground(LogoutRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.newLogout(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<LogoutResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<LogoutResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

