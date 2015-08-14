package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationSendSmsToken extends AsyncTask<RegistrationSendSmsTokenRequest, Void, ResponseMessage<RegistrationSendSmsTokenResponse>> {
    private static final String TAG = "RequestRegistrationSendSmsToken";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> listener;


    public RequestRegistrationSendSmsToken(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> listener)
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
    protected ResponseMessage<RegistrationSendSmsTokenResponse> doInBackground(RegistrationSendSmsTokenRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationSendSmsToken(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponseMessage)
    {
        super.onPostExecute(registrationSendSmsTokenResponseMessage);
        listener.onTaskComplete(registrationSendSmsTokenResponseMessage);
    }

}
