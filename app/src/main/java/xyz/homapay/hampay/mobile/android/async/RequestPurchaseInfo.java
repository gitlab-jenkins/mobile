package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;


/**
 * Created by amir on 3/12/16.
 */
public class RequestPurchaseInfo extends AsyncTask<PurchaseInfoRequest, Void, ResponseMessage<PurchaseInfoResponse>> {

    private static final String TAG = "RequestPurchaseInfo";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PurchaseInfoResponse>> listener;


    public RequestPurchaseInfo(Context context, AsyncTaskCompleteListener<ResponseMessage<PurchaseInfoResponse>> listener)
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
    protected ResponseMessage<PurchaseInfoResponse> doInBackground(PurchaseInfoRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.purchaseInfo(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<PurchaseInfoResponse> purchaseInfoResponseMessage)
    {
        super.onPostExecute(purchaseInfoResponseMessage);
        listener.onTaskComplete(purchaseInfoResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PurchaseInfoResponse> purchaseInfoResponseMessage) {
        super.onCancelled(purchaseInfoResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}


