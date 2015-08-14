package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.DeviceDTO;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIndividualPaymentConfirm extends AsyncTask<IndividualPaymentConfirmRequest, Void, ResponseMessage<IndividualPaymentConfirmResponse>>  {

    private static final String TAG = "RequestIndividualPaymentConfirm";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> listener;

    public RequestIndividualPaymentConfirm(Context context, AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> listener)
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
    protected ResponseMessage<IndividualPaymentConfirmResponse> doInBackground(IndividualPaymentConfirmRequest... params) {

        WebServices webServices = new WebServices(context);
        return webServices.newIndividualPaymentConfirm(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponseMessage)
    {
        super.onPostExecute(individualPaymentConfirmResponseMessage);
        listener.onTaskComplete(individualPaymentConfirmResponseMessage);
    }

}
