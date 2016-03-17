package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestLatestPayment extends AsyncTask<LatestPaymentRequest, Void, ResponseMessage<LatestPaymentResponse>> {

    private static final String TAG = "RequestLatestPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<LatestPaymentResponse>> listener;


    public RequestLatestPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<LatestPaymentResponse>> listener)
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
    protected ResponseMessage<LatestPaymentResponse> doInBackground(LatestPaymentRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.latestUserPayment(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<LatestPaymentResponse> latestPaymentResponseMessage)
    {
        super.onPostExecute(latestPaymentResponseMessage);
        listener.onTaskComplete(latestPaymentResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<LatestPaymentResponse> latestPaymentResponseMessage) {
        super.onCancelled(latestPaymentResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

