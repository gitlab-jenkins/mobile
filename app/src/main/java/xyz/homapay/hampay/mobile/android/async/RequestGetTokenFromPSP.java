package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.GetTokenFromPSPRequest;
import xyz.homapay.hampay.common.core.model.response.GetTokenFromPSPResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 3/30/16.
 */
public class RequestGetTokenFromPSP extends AsyncTask<GetTokenFromPSPRequest, Void, ResponseMessage<GetTokenFromPSPResponse>> {

    private static final String TAG = "RequestGetTokenFromPSP";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<GetTokenFromPSPResponse>> listener;

    private int transactionType;

    public RequestGetTokenFromPSP(Context context, AsyncTaskCompleteListener<ResponseMessage<GetTokenFromPSPResponse>> listener, int transactionType)
    {
        this.context = context;
        this.listener = listener;
        this.transactionType = transactionType;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected ResponseMessage<GetTokenFromPSPResponse> doInBackground(GetTokenFromPSPRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.getTokenFromPSP(params[0], transactionType);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<GetTokenFromPSPResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<GetTokenFromPSPResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

