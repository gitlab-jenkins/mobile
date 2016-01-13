package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.psp.model.request.PurchaseRequest;
import xyz.homapay.hampay.common.psp.model.response.PurchaseResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPurchase extends AsyncTask<PurchaseRequest, Void, ResponseMessage<PurchaseResponse>> {

    private static final String TAG = "RequestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PurchaseResponse>> listener;


    public RequestPurchase(Context context, AsyncTaskCompleteListener<ResponseMessage<PurchaseResponse>> listener)
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
    protected ResponseMessage<PurchaseResponse> doInBackground(PurchaseRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newPurchaseResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<PurchaseResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PurchaseResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

