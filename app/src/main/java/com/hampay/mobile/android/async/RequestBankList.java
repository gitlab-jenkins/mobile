package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestBankList extends AsyncTask<BankListRequest, Void, ResponseMessage<BankListResponse>> {

    private static final String TAG = "RequestBankList";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<BankListResponse>> listener;

    public RequestBankList(Context context, AsyncTaskCompleteListener<ResponseMessage<BankListResponse>> listener)
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
    protected ResponseMessage<BankListResponse> doInBackground(BankListRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.getBankList();
    }


    @Override
    protected void onPostExecute(ResponseMessage<BankListResponse> bankListResponseMessage)
    {
        super.onPostExecute(bankListResponseMessage);
        listener.onTaskComplete(bankListResponseMessage);
    }

}
