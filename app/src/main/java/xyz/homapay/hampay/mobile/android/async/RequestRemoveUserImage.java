package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRemoveUserImage extends AsyncTask<RemoveUserImageRequest, Void, ResponseMessage<RemoveUserImageResponse>> {

    private static final String TAG = "RequestRemoveUserImage";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RemoveUserImageResponse>> listener;


    public RequestRemoveUserImage(Context context, AsyncTaskCompleteListener<ResponseMessage<RemoveUserImageResponse>> listener)
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
    protected ResponseMessage<RemoveUserImageResponse> doInBackground(RemoveUserImageRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newRemoveUserImageResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RemoveUserImageResponse> removeUserImageResponseMessage)
    {
        super.onPostExecute(removeUserImageResponseMessage);
        listener.onTaskComplete(removeUserImageResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<RemoveUserImageResponse> removeUserImageResponseMessage) {
        super.onCancelled(removeUserImageResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

