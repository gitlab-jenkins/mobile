package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationSendSmsToken extends AsyncTask<RegistrationSendSmsTokenRequest, Void, ResponseMessage<RegistrationSendSmsTokenResponse>> {
    private static final String TAG = "RequestRegistrationSendSmsToken";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> listener;


    public RequestRegistrationSendSmsToken(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> listener)
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
    protected ResponseMessage<RegistrationSendSmsTokenResponse> doInBackground(RegistrationSendSmsTokenRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.registrationSendSmsToken(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponseMessage)
    {
        super.onPostExecute(registrationSendSmsTokenResponseMessage);
        listener.onTaskComplete(registrationSendSmsTokenResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponseResponseMessage) {
        super.onCancelled(registrationSendSmsTokenResponseResponseMessage);

        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
