package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestCancelPurchase extends AsyncTask<CancelPurchasePaymentRequest, Void, ResponseMessage<CancelPurchasePaymentResponse>> {

    private static final String TAG = "RequestCancelPurchase";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<CancelPurchasePaymentResponse>> listener;


    public RequestCancelPurchase(Context context, AsyncTaskCompleteListener<ResponseMessage<CancelPurchasePaymentResponse>> listener)
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
    protected ResponseMessage<CancelPurchasePaymentResponse> doInBackground(CancelPurchasePaymentRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.cancelPurchasePaymentResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<CancelPurchasePaymentResponse> cancelPurchasePaymentResponseMessage)
    {
        super.onPostExecute(cancelPurchasePaymentResponseMessage);
        listener.onTaskComplete(cancelPurchasePaymentResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<CancelPurchasePaymentResponse> cancelPurchasePaymentResponseMessage) {
        super.onCancelled(cancelPurchasePaymentResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

