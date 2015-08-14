package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationVerifyTransferMoney extends AsyncTask<RegistrationVerifyTransferMoneyRequest, Void, ResponseMessage<RegistrationVerifyTransferMoneyResponse>> {

    private static final String TAG = "RequestRegistrationVerifyTransferMoney";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyTransferMoneyResponse>> listener;


    public RequestRegistrationVerifyTransferMoney(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyTransferMoneyResponse>> listener)
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
    protected ResponseMessage<RegistrationVerifyTransferMoneyResponse> doInBackground(RegistrationVerifyTransferMoneyRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationVerifyTransferMoneyResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationVerifyTransferMoneyResponse> registrationVerifyTransferMoneyResponseMessage)
    {
        super.onPostExecute(registrationVerifyTransferMoneyResponseMessage);
        listener.onTaskComplete(registrationVerifyTransferMoneyResponseMessage);
    }


}
