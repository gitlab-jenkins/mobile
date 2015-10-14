package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestCredentialEntry extends AsyncTask<RegistrationCredentialsRequest, Void, ResponseMessage<RegistrationCredentialsResponse>> {

    private static final String TAG = "RequestCredentialEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationCredentialsResponse>> listener;


    public RequestCredentialEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationCredentialsResponse>> listener)
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
    protected ResponseMessage<RegistrationCredentialsResponse> doInBackground(RegistrationCredentialsRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationCredentialsResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationCredentialsResponse> registrationMemorableWordEntryResponseMessage)
    {
        super.onPostExecute(registrationMemorableWordEntryResponseMessage);
        listener.onTaskComplete(registrationMemorableWordEntryResponseMessage);
    }


}
