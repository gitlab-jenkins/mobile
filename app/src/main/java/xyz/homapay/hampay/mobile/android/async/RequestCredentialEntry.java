package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

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

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.registrationCredentialsResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationCredentialsResponse> registrationMemorableWordEntryResponseMessage)
    {
        super.onPostExecute(registrationMemorableWordEntryResponseMessage);
        listener.onTaskComplete(registrationMemorableWordEntryResponseMessage);
    }


}
