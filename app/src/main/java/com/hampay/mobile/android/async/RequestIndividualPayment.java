package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.IndividualPaymentRequest;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIndividualPayment extends AsyncTask<IndividualPaymentRequest, Void, ResponseMessage<IndividualPaymentResponse>> {

    private static final String TAG = "RequestIndividualPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentResponse>> listener;


    public RequestIndividualPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentResponse>> listener)
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
    protected ResponseMessage<IndividualPaymentResponse> doInBackground(IndividualPaymentRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newIndividualPayment(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<IndividualPaymentResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }


}

