package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUserProfile extends AsyncTask<UserProfileRequest, Void, ResponseMessage<UserProfileResponse>>  {

    private static final String TAG = "RequestUserProfile";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<UserProfileResponse>> listener;

    public RequestUserProfile(Context context, AsyncTaskCompleteListener<ResponseMessage<UserProfileResponse>> listener)
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
    protected ResponseMessage<UserProfileResponse> doInBackground(UserProfileRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.newGetUserProfile(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<UserProfileResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<UserProfileResponse> userProfileResponseResponseMessage) {
        super.onCancelled(userProfileResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
