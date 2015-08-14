package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/31/15.
 */
public class RequestUserTransaction extends AsyncTask<TransactionListRequest, Void, ResponseMessage<TransactionListResponse>>  {

    private static final String TAG = "RequestUserTransaction";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> listener;


    public RequestUserTransaction(Context context, AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> listener)
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
    protected ResponseMessage<TransactionListResponse> doInBackground(TransactionListRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newGetUserTransaction(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<TransactionListResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}

