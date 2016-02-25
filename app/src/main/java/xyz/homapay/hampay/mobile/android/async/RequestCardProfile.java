package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.CardProfileResponse;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 2/20/16.
 */
public class RequestCardProfile extends AsyncTask<CardProfileRequest, Void, ResponseMessage<CardProfileResponse>> {

    private static final String TAG = "RequestTAC";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<CardProfileResponse>> listener;


    public RequestCardProfile(Context context, AsyncTaskCompleteListener<ResponseMessage<CardProfileResponse>> listener)
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
    protected ResponseMessage<CardProfileResponse> doInBackground(CardProfileRequest... params) {

        WebServices webServices = new WebServices(context);

            return webServices.newCardProfile(params[0]);

    }


    @Override
    protected void onPostExecute(ResponseMessage<CardProfileResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<CardProfileResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

