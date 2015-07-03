package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPassCodeEntry extends AsyncTask<RegistrationPassCodeEntryRequest, Void, ResponseMessage<RegistrationPassCodeEntryResponse>> {

    private static final String TAG = "RequestFetchUserData";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationPassCodeEntryResponse>> listener;


    public RequestPassCodeEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationPassCodeEntryResponse>> listener)
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
    protected ResponseMessage<RegistrationPassCodeEntryResponse> doInBackground(RegistrationPassCodeEntryRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.registrationPassCodeEntryResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationPassCodeEntryResponse> registrationPassCodeEntryResponseMessage)
    {
        super.onPostExecute(registrationPassCodeEntryResponseMessage);
        listener.onTaskComplete(registrationPassCodeEntryResponseMessage);
    }


}
