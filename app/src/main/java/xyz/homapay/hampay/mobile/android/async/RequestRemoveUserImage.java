package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
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

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.removeUserImageResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
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

