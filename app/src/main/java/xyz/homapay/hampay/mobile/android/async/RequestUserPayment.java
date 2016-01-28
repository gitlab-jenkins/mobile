package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUserPayment extends AsyncTask<UserPaymentRequest, Void, ResponseMessage<UserPaymentResponse>> {

    private static final String TAG = "RequestTAC";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> listener;


    public RequestUserPayment(Context context, AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> listener)
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
    protected ResponseMessage<UserPaymentResponse> doInBackground(UserPaymentRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newUserPaymentResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<UserPaymentResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<UserPaymentResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

