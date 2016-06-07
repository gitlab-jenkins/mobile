package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTACAccept extends AsyncTask<TACAcceptRequest, Void, ResponseMessage<TACAcceptResponse>> {

    private static final String TAG = "RequestTACAccept";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>> listener;


    public RequestTACAccept(Context context, AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>> listener)
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
    protected ResponseMessage<TACAcceptResponse> doInBackground(TACAcceptRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.tacAcceptResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<TACAcceptResponse> tacAcceptResponseMessage)
    {
        super.onPostExecute(tacAcceptResponseMessage);
        listener.onTaskComplete(tacAcceptResponseMessage);
    }


}

