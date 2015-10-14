package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUploadImage extends AsyncTask<UploadImageRequest, Void, ResponseMessage<UploadImageResponse>> {

    private static final String TAG = "RequestUploadImage";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<UploadImageResponse>> listener;


    public RequestUploadImage(Context context, AsyncTaskCompleteListener<ResponseMessage<UploadImageResponse>> listener)
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
    protected ResponseMessage<UploadImageResponse> doInBackground(UploadImageRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newUploadImage(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<UploadImageResponse> uploadImageResponseMessage)
    {
        super.onPostExecute(uploadImageResponseMessage);
        listener.onTaskComplete(uploadImageResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<UploadImageResponse> uploadImageResponseMessage) {
        super.onCancelled(uploadImageResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

