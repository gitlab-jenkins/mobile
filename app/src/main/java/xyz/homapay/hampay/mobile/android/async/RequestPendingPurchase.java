package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPendingPurchase extends AsyncTask<PendingPurchaseListRequest, Void, ResponseMessage<PendingPurchaseListResponse>> {

    private static final String TAG = "RequestPendingPurchase";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> listener;


    public RequestPendingPurchase(Context context, AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> listener)
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
    protected ResponseMessage<PendingPurchaseListResponse> doInBackground(PendingPurchaseListRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newPendingPurchase(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseMessage)
    {
        super.onPostExecute(pendingPurchaseListResponseMessage);
        listener.onTaskComplete(pendingPurchaseListResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseResponseMessage) {
        super.onCancelled(pendingPurchaseListResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

