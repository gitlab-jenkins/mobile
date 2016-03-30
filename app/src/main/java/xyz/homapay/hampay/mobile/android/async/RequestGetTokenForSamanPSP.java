package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.GetTokenForSamanPSPRequest;
import xyz.homapay.hampay.common.core.model.response.GetTokenForSamanPSPResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 3/30/16.
 */
public class RequestGetTokenForSamanPSP  extends AsyncTask<GetTokenForSamanPSPRequest, Void, ResponseMessage<GetTokenForSamanPSPResponse>> {

    private static final String TAG = "RequestGetTokenForSamanPSP";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<GetTokenForSamanPSPResponse>> listener;

    private int transactionType;

    public RequestGetTokenForSamanPSP(Context context, AsyncTaskCompleteListener<ResponseMessage<GetTokenForSamanPSPResponse>> listener, int transactionType)
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
    protected ResponseMessage<GetTokenForSamanPSPResponse> doInBackground(GetTokenForSamanPSPRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.getTokenForSamanPSP(params[0], transactionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<GetTokenForSamanPSPResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<GetTokenForSamanPSPResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

