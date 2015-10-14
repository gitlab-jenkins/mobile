package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestBusinessPaymentConfirm extends AsyncTask<BusinessPaymentConfirmRequest, Void, ResponseMessage<BusinessPaymentConfirmResponse>>  {

    private static final String TAG = "RequestBusinessPaymentConfirm";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> listener;

    public RequestBusinessPaymentConfirm(Context context, AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> listener)
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
    protected ResponseMessage<BusinessPaymentConfirmResponse> doInBackground(BusinessPaymentConfirmRequest... params) {

        WebServices webServices = new WebServices(context);
        return webServices.newBusinessPaymentConfirm(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponseMessage)
    {
        super.onPostExecute(businessPaymentConfirmResponseMessage);
        listener.onTaskComplete(businessPaymentConfirmResponseMessage);
    }

}
