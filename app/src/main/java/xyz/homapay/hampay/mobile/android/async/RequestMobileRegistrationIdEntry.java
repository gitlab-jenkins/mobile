package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import com.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

        WebServices webServices = new WebServices(context);
        return webServices.newRegistrationDeviceRegId(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<MobileRegistrationIdEntryResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}
