package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.psp.model.response.UnregisterCardResponse;
import xyz.homapay.hampay.common.psp.model.request.UnregisterCardRequest;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUnlinkUser extends AsyncTask<UnregisterCardRequest, Void, ResponseMessage<UnregisterCardResponse>>  {

    private static final String TAG = "UnlinkUserResponse";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<UnregisterCardResponse>> listener;

    public RequestUnlinkUser(Context context, AsyncTaskCompleteListener<ResponseMessage<UnregisterCardResponse>> listener)
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
    protected ResponseMessage<UnregisterCardResponse> doInBackground(UnregisterCardRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.unregisterCardResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<UnregisterCardResponse> changeMemorableWordResponseMessage)
    {
        super.onPostExecute(changeMemorableWordResponseMessage);
        listener.onTaskComplete(changeMemorableWordResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<UnregisterCardResponse> unlinkUserResponseResponseMessage) {
        super.onCancelled(unlinkUserResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
