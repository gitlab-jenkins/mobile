package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingCountRequest;
import xyz.homapay.hampay.common.core.model.response.PendingCountResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPendingCount extends AsyncTask<PendingCountRequest, Void, ResponseMessage<PendingCountResponse>> {

    private static final String TAG = "RequestPendingCount";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PendingCountResponse>> listener;


    public RequestPendingCount(Context context, AsyncTaskCompleteListener<ResponseMessage<PendingCountResponse>> listener)
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
    protected ResponseMessage<PendingCountResponse> doInBackground(PendingCountRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.pendingCount(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<PendingCountResponse> pendingCountResponseMessage)
    {
        super.onPostExecute(pendingCountResponseMessage);
        listener.onTaskComplete(pendingCountResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PendingCountResponse> pendingCountResponseMessage) {
        super.onCancelled(pendingCountResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }
}

