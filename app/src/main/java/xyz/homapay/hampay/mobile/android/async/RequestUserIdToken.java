package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;

import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUserIdToken extends AsyncTask<GetUserIdTokenRequest, Void, ResponseMessage<GetUserIdTokenResponse>> {

    private static final String TAG = "RequestUserIdToken";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<GetUserIdTokenResponse>> listener;


    public RequestUserIdToken(Context context, AsyncTaskCompleteListener<ResponseMessage<GetUserIdTokenResponse>> listener)
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
    protected ResponseMessage<GetUserIdTokenResponse> doInBackground(GetUserIdTokenRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newGetUserIdTokenResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<GetUserIdTokenResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<GetUserIdTokenResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

