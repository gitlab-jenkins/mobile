package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestVerifyMobile extends AsyncTask<RegistrationVerifyMobileRequest, Void, ResponseMessage<RegistrationVerifyMobileResponse>> {

    private static final String TAG = "RequestVerifyMobile";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>> listener;


    public RequestVerifyMobile(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyMobileResponse>> listener)
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
    protected ResponseMessage<RegistrationVerifyMobileResponse> doInBackground(RegistrationVerifyMobileRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationVerifyMobileResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseMessage)
    {
        super.onPostExecute(registrationVerifyMobileResponseMessage);
        listener.onTaskComplete(registrationVerifyMobileResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<RegistrationVerifyMobileResponse> registrationVerifyMobileResponseResponseMessage) {
        super.onCancelled(registrationVerifyMobileResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}
