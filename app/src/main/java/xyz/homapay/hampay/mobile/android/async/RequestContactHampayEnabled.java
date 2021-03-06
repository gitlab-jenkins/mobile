package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestContactHampayEnabled extends AsyncTask<ContactsHampayEnabledRequest, Void, ResponseMessage<ContactsHampayEnabledResponse>>  {

    private static final String TAG = "RequestContactHampayEnabled";

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


        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);
        try {
            return webServices.getEnabledHamPayContacts(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
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
