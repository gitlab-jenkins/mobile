package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationVerifyMobileResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

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

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.registrationVerifyMobileResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
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
