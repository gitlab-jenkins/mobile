package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 2/18/16.
 */
public class RequestIBANConfirmation extends AsyncTask<IBANConfirmationRequest, Void, ResponseMessage<IBANConfirmationResponse>> {

    private static final String TAG = "RequestIBANConfirmation";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IBANConfirmationResponse>> listener;


    public RequestIBANConfirmation(Context context, AsyncTaskCompleteListener<ResponseMessage<IBANConfirmationResponse>> listener)
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
    protected ResponseMessage<IBANConfirmationResponse> doInBackground(IBANConfirmationRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.ibanConfirmation(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<IBANConfirmationResponse> ibanConfirmationResponseMessage)
    {
        super.onPostExecute(ibanConfirmationResponseMessage);
        listener.onTaskComplete(ibanConfirmationResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<IBANConfirmationResponse> ibanConfirmationResponseResponseMessage) {
        super.onCancelled(ibanConfirmationResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

