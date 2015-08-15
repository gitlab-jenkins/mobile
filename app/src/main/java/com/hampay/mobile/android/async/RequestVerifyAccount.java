package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.response.VerifyAccountResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 8/9/15.
 */
public class RequestVerifyAccount extends AsyncTask<VerifyAccountRequest, Void, ResponseMessage<VerifyAccountResponse>> {

    private static final String TAG = "RequestVerifyAccount";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<VerifyAccountResponse>> listener;


    public RequestVerifyAccount(Context context, AsyncTaskCompleteListener<ResponseMessage<VerifyAccountResponse>> listener)
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
    protected ResponseMessage<VerifyAccountResponse> doInBackground(VerifyAccountRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newVerifyAccountResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<VerifyAccountResponse> registrationVerifyMobileResponseMessage)
    {
        super.onPostExecute(registrationVerifyMobileResponseMessage);
        listener.onTaskComplete(registrationVerifyMobileResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<VerifyAccountResponse> verifyAccountResponseResponseMessage) {
        super.onCancelled(verifyAccountResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}
