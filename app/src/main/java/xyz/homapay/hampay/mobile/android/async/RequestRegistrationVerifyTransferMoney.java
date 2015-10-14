package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationVerifyTransferMoney extends AsyncTask<RegistrationVerifyTransferMoneyRequest, Void, ResponseMessage<RegistrationVerifyTransferMoneyResponse>> {

    private static final String TAG = "RequestRegistrationVerifyTransferMoney";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyTransferMoneyResponse>> listener;


    public RequestRegistrationVerifyTransferMoney(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyTransferMoneyResponse>> listener)
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
    protected ResponseMessage<RegistrationVerifyTransferMoneyResponse> doInBackground(RegistrationVerifyTransferMoneyRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationVerifyTransferMoneyResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationVerifyTransferMoneyResponse> registrationVerifyTransferMoneyResponseMessage)
    {
        super.onPostExecute(registrationVerifyTransferMoneyResponseMessage);
        listener.onTaskComplete(registrationVerifyTransferMoneyResponseMessage);
    }


}
