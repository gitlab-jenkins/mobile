package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestVerifyTransferMoney extends AsyncTask<VerifyTransferMoneyRequest, Void, ResponseMessage<VerifyTransferMoneyResponse>> {

    private static final String TAG = "RequestTAC";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>> listener;


    public RequestVerifyTransferMoney(Context context, AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>> listener)
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
    protected ResponseMessage<VerifyTransferMoneyResponse> doInBackground(VerifyTransferMoneyRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.verifyTransferMoneyResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponseMessage)
    {
        super.onPostExecute(verifyTransferMoneyResponseMessage);
        listener.onTaskComplete(verifyTransferMoneyResponseMessage);
    }


}

