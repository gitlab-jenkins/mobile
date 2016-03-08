package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.userTransaction(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<TransactionListResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<TransactionListResponse> transactionListResponseResponseMessage) {
        super.onCancelled(transactionListResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}

