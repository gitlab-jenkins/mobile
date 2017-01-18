package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.CancelFundRequest;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestCancelPayment extends AsyncTask<CancelFundRequest, Void, ResponseMessage<CancelFundResponse>> {

    private static final String TAG = "RequestCancelPayment";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<CancelFundResponse>> listener;


    public RequestCancelPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<CancelFundResponse>> listener)
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
    protected ResponseMessage<CancelFundResponse> doInBackground(CancelFundRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.cancelFund(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<CancelFundResponse> cancelFundResponse)
    {
        super.onPostExecute(cancelFundResponse);
        listener.onTaskComplete(cancelFundResponse);
    }

    @Override
    protected void onCancelled(ResponseMessage<CancelFundResponse> cancelFundResponse) {
        super.onCancelled(cancelFundResponse);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

