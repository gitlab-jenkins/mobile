package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestBusinessPayment extends AsyncTask<BusinessPaymentRequest, Void, ResponseMessage<BusinessPaymentResponse>> {

    private static final String TAG = "RequestBusinessPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentResponse>> listener;


    public RequestBusinessPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentResponse>> listener)
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
    protected ResponseMessage<BusinessPaymentResponse> doInBackground(BusinessPaymentRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.businessPayment(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<BusinessPaymentResponse> businessPaymentResponseMessage)
    {
        super.onPostExecute(businessPaymentResponseMessage);
        listener.onTaskComplete(businessPaymentResponseMessage);
    }


}

