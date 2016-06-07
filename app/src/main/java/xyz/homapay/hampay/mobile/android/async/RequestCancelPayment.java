package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.CancelUserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CancelUserPaymentResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestCancelPayment extends AsyncTask<CancelUserPaymentRequest, Void, ResponseMessage<CancelUserPaymentResponse>> {

    private static final String TAG = "RequestCancelPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<CancelUserPaymentResponse>> listener;


    public RequestCancelPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<CancelUserPaymentResponse>> listener)
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
    protected ResponseMessage<CancelUserPaymentResponse> doInBackground(CancelUserPaymentRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.cancelUserPaymentResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<CancelUserPaymentResponse> cancelUserPaymentResponseMessage)
    {
        super.onPostExecute(cancelUserPaymentResponseMessage);
        listener.onTaskComplete(cancelUserPaymentResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<CancelUserPaymentResponse> cancelUserPaymentResponseMessage) {
        super.onCancelled(cancelUserPaymentResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

