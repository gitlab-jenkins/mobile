package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationEntry extends AsyncTask<RegistrationEntryRequest, Void, ResponseMessage<RegistrationEntryResponse>>  {

    private static final String TAG = "RequestHttpRegistrationEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener;


    public RequestRegistrationEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener)
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
    protected ResponseMessage<RegistrationEntryResponse> doInBackground(RegistrationEntryRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.registrationEntry(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationEntryResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}
