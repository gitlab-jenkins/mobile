package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestContactHampayEnabled extends AsyncTask<ContactsHampayEnabledRequest, Void, ResponseMessage<ContactsHampayEnabledResponse>>  {

    private static final String TAG = "RequestHttpRegistrationEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> listener;

    public RequestContactHampayEnabled(Context context, AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> listener)
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
    protected ResponseMessage<ContactsHampayEnabledResponse> doInBackground(ContactsHampayEnabledRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.getEnabledHamPayContacts(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<ContactsHampayEnabledResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}
