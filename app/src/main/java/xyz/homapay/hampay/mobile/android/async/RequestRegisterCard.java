package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.psp.model.request.RegisterCardRequest;
import xyz.homapay.hampay.common.psp.model.response.RegisterCardResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegisterCard extends AsyncTask<RegisterCardRequest, Void, ResponseMessage<RegisterCardResponse>> {

    private static final String TAG = "RequestRegisterCard";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegisterCardResponse>> listener;


    public RequestRegisterCard(Context context, AsyncTaskCompleteListener<ResponseMessage<RegisterCardResponse>> listener)
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
    protected ResponseMessage<RegisterCardResponse> doInBackground(RegisterCardRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.registerCardResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegisterCardResponse> registerCardResponseMessage)
    {
        super.onPostExecute(registerCardResponseMessage);
        listener.onTaskComplete(registerCardResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<RegisterCardResponse> registerCardResponseMessage) {
        super.onCancelled(registerCardResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

