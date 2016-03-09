package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestLatestPurchase extends AsyncTask<LatestPurchaseRequest, Void, ResponseMessage<LatestPurchaseResponse>> {

    private static final String TAG = "RequestLatestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<LatestPurchaseResponse>> listener;


    public RequestLatestPurchase(Context context, AsyncTaskCompleteListener<ResponseMessage<LatestPurchaseResponse>> listener)
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
    protected ResponseMessage<LatestPurchaseResponse> doInBackground(LatestPurchaseRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.latestUserPurchase(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<LatestPurchaseResponse> latestPurchaseResponseMessage)
    {
        super.onPostExecute(latestPurchaseResponseMessage);
        listener.onTaskComplete(latestPurchaseResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<LatestPurchaseResponse> latestPurchaseResponseMessage) {
        super.onCancelled(latestPurchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

