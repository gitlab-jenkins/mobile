package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTransactionDetail extends AsyncTask<TransactionDetailRequest, Void, ResponseMessage<TransactionDetailResponse>> {

    private static final String TAG = "RequestTransactionDetail";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<TransactionDetailResponse>> listener;


    public RequestTransactionDetail(Context context, AsyncTaskCompleteListener<ResponseMessage<TransactionDetailResponse>> listener)
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
    protected ResponseMessage<TransactionDetailResponse> doInBackground(TransactionDetailRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.transactionDetailResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<TransactionDetailResponse> transactionDetailResponseMessage)
    {
        super.onPostExecute(transactionDetailResponseMessage);
        listener.onTaskComplete(transactionDetailResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<TransactionDetailResponse> transactionDetailResponseMessage) {
        super.onCancelled(transactionDetailResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

