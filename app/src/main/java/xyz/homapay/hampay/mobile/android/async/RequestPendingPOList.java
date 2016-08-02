package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 3/18/16.
 */
public class RequestPendingPOList extends AsyncTask<PendingPOListRequest, Void, ResponseMessage<PendingPOListResponse>> {

    private static final String TAG = "RequestPendingPOList";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PendingPOListResponse>> listener;


    public RequestPendingPOList(Context context, AsyncTaskCompleteListener<ResponseMessage<PendingPOListResponse>> listener)
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
    protected ResponseMessage<PendingPOListResponse> doInBackground(PendingPOListRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.pendingPOList(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<PendingPOListResponse> pendingPOListResponseResponseMessage)
    {
        super.onPostExecute(pendingPOListResponseResponseMessage);
        listener.onTaskComplete(pendingPOListResponseResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PendingPOListResponse> pendingPOListResponseResponseMessage) {
        super.onCancelled(pendingPOListResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

