package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

        WebServices webServices = new WebServices(context);

        return webServices.newPSPResultResponse(params[0]);
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

