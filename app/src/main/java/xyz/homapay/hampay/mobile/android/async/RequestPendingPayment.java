package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingPaymentListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPendingPayment extends AsyncTask<PendingPaymentListRequest, Void, ResponseMessage<PendingPaymentListResponse>> {

    private static final String TAG = "RequestPendingPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PendingPaymentListResponse>> listener;


    public RequestPendingPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<PendingPaymentListResponse>> listener)
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
    protected ResponseMessage<PendingPaymentListResponse> doInBackground(PendingPaymentListRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newPendingPayment(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<PendingPaymentListResponse> pendingPaymentListResponseMessage)
    {
        super.onPostExecute(pendingPaymentListResponseMessage);
        listener.onTaskComplete(pendingPaymentListResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PendingPaymentListResponse> pendingPaymentListResponseMessage) {
        super.onCancelled(pendingPaymentListResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

