package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPSPResult extends AsyncTask<PSPResultRequest, Void, ResponseMessage<PSPResultResponse>> {

    private static final String TAG = "RequestPSPResult";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> listener;


    public RequestPSPResult(Context context, AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> listener)
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
    protected ResponseMessage<PSPResultResponse> doInBackground(PSPResultRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.pspResult(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<PSPResultResponse> pspResultResponseMessage)
    {
        super.onPostExecute(pspResultResponseMessage);
        listener.onTaskComplete(pspResultResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {
        super.onCancelled(pspResultResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

