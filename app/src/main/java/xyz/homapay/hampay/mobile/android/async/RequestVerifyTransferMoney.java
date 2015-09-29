package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestVerifyTransferMoney extends AsyncTask<VerifyTransferMoneyRequest, Void, ResponseMessage<VerifyTransferMoneyResponse>> {

    private static final String TAG = "RequestTAC";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>> listener;


    public RequestVerifyTransferMoney(Context context, AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>> listener)
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
    protected ResponseMessage<VerifyTransferMoneyResponse> doInBackground(VerifyTransferMoneyRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newVerifyTransferMoneyResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponseMessage)
    {
        super.onPostExecute(verifyTransferMoneyResponseMessage);
        listener.onTaskComplete(verifyTransferMoneyResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponseResponseMessage) {
        super.onCancelled(verifyTransferMoneyResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

