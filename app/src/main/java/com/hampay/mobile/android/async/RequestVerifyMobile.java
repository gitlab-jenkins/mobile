package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestVerifyMobile extends AsyncTask<RegistrationVerifyMobileRequest, Void, ResponseMessage<RegistrationVerifyMobileResponse>> {

    private static final String TAG = "RequestVerifyMobile";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>> listener;


    public RequestVerifyMobile(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>> listener)
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
    protected ResponseMessage<RegistrationVerifyMobileResponse> doInBackground(RegistrationVerifyMobileRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.registrationVerifyMobileResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseMessage)
    {
        super.onPostExecute(registrationVerifyMobileResponseMessage);
        listener.onTaskComplete(registrationVerifyMobileResponseMessage);
    }


}
