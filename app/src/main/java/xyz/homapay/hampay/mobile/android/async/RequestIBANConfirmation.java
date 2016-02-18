package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

        WebServices webServices = new WebServices(context);

        return webServices.newIBANConfirmation(params[0]);
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

