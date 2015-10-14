package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegisterVerifyAccount extends AsyncTask<RegistrationVerifyAccountRequest, Void, ResponseMessage<RegistrationVerifyAccountResponse>> {

    private static final String TAG = "RequestRegisterVerifyAccount";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyAccountResponse>> listener;


    public RequestRegisterVerifyAccount(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyAccountResponse>> listener)
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
    protected ResponseMessage<RegistrationVerifyAccountResponse> doInBackground(RegistrationVerifyAccountRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationVerifyAccountResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationVerifyAccountResponse> registrationVerifyAccountResponseMessage)
    {
        super.onPostExecute(registrationVerifyAccountResponseMessage);
        listener.onTaskComplete(registrationVerifyAccountResponseMessage);
    }


}

