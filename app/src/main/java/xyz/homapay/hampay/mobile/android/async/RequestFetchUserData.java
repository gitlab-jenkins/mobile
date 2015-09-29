package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;;
import com.hampay.common.core.model.response.RegistrationFetchUserDataResponse;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestFetchUserData extends AsyncTask<RegistrationFetchUserDataRequest, Void, ResponseMessage<RegistrationFetchUserDataResponse>> {

    private static final String TAG = "RequestFetchUserData";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationFetchUserDataResponse>> listener;


    public RequestFetchUserData(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationFetchUserDataResponse>> listener)
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
    protected ResponseMessage<RegistrationFetchUserDataResponse> doInBackground(RegistrationFetchUserDataRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRegistrationFetchUserDataResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationFetchUserDataResponse> registrationFetchUserDataResponseMessage)
    {
        super.onPostExecute(registrationFetchUserDataResponseMessage);
        listener.onTaskComplete(registrationFetchUserDataResponseMessage);
    }


}
