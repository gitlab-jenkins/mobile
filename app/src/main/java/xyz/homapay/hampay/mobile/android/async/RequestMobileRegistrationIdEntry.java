package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestMobileRegistrationIdEntry extends AsyncTask<MobileRegistrationIdEntryRequest, Void, ResponseMessage<MobileRegistrationIdEntryResponse>>  {

    private static final String TAG = "RequestMobileRegistrationIdEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<MobileRegistrationIdEntryResponse>> listener;

    public RequestMobileRegistrationIdEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<MobileRegistrationIdEntryResponse>> listener)
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
    protected ResponseMessage<MobileRegistrationIdEntryResponse> doInBackground(MobileRegistrationIdEntryRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);
        try {
            return webServices.registrationDeviceRegId(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<MobileRegistrationIdEntryResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}
