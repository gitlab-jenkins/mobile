package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPendingFundList extends AsyncTask<PendingFundListRequest, Void, ResponseMessage<PendingFundListResponse>> {

    private static final String TAG = "RequestPendingFundList";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PendingFundListResponse>> listener;

    public RequestPendingFundList(Context context, AsyncTaskCompleteListener<ResponseMessage<PendingFundListResponse>> listener)
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
    protected ResponseMessage<PendingFundListResponse> doInBackground(PendingFundListRequest... params) {
        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.fundListResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<PendingFundListResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PendingFundListResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

