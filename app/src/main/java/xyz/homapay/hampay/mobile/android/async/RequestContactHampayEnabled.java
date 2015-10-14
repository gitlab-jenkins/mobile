package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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
        return webServices.newGetEnabledHamPayContacts(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<ContactsHampayEnabledResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseResponseMessage) {
        super.onCancelled(contactsHampayEnabledResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
