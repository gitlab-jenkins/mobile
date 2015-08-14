package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestMemorableWordEntry extends AsyncTask<RegistrationMemorableWordEntryRequest, Void, ResponseMessage<RegistrationMemorableWordEntryResponse>> {

    private static final String TAG = "RequestMemorableWordEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationMemorableWordEntryResponse>> listener;


    public RequestMemorableWordEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationMemorableWordEntryResponse>> listener)
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
    protected ResponseMessage<RegistrationMemorableWordEntryResponse> doInBackground(RegistrationMemorableWordEntryRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationMemorableWordEntryResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationMemorableWordEntryResponse> registrationMemorableWordEntryResponseMessage)
    {
        super.onPostExecute(registrationMemorableWordEntryResponseMessage);
        listener.onTaskComplete(registrationMemorableWordEntryResponseMessage);
    }


}
