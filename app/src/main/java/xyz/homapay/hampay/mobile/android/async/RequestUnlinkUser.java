package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.UnlinkUserRequest;
import com.hampay.common.core.model.response.UnlinkUserResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUnlinkUser extends AsyncTask<UnlinkUserRequest, Void, ResponseMessage<UnlinkUserResponse>>  {

    private static final String TAG = "UnlinkUserResponse";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<UnlinkUserResponse>> listener;

    public RequestUnlinkUser(Context context, AsyncTaskCompleteListener<ResponseMessage<UnlinkUserResponse>> listener)
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
    protected ResponseMessage<UnlinkUserResponse> doInBackground(UnlinkUserRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.unlinkUserResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<UnlinkUserResponse> changeMemorableWordResponseMessage)
    {
        super.onPostExecute(changeMemorableWordResponseMessage);
        listener.onTaskComplete(changeMemorableWordResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<UnlinkUserResponse> unlinkUserResponseResponseMessage) {
        super.onCancelled(unlinkUserResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
