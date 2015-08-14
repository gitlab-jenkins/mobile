package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.response.RegistrationConfirmUserDataResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestConfirmUserData extends AsyncTask<RegistrationConfirmUserDataRequest, Void, ResponseMessage<RegistrationConfirmUserDataResponse>> {

    private static final String TAG = "RequestConfirmUserData";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationConfirmUserDataResponse>> listener;


    public RequestConfirmUserData(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationConfirmUserDataResponse>> listener)
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
    protected ResponseMessage<RegistrationConfirmUserDataResponse> doInBackground(RegistrationConfirmUserDataRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationConfirmUserDataResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationConfirmUserDataResponse> registrationConfirmUserDataResponseMessage)
    {
        super.onPostExecute(registrationConfirmUserDataResponseMessage);
        listener.onTaskComplete(registrationConfirmUserDataResponseMessage);
    }


}
