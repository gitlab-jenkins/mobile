package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.DeviceDTO;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.mobile.android.activity.PayOneActivity;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIndividualPaymentConfirm extends AsyncTask<IndividualPaymentConfirmRequest, Void, ResponseMessage<IndividualPaymentConfirmResponse>>  {

    private static final String TAG = "RequestIndividualPaymentConfirm";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> listener;

    public RequestIndividualPaymentConfirm(Context context, AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> listener)
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
    protected ResponseMessage<IndividualPaymentConfirmResponse> doInBackground(IndividualPaymentConfirmRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);
        try {
            return webServices.individualPaymentConfirm(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponseMessage)
    {
        super.onPostExecute(individualPaymentConfirmResponseMessage);
        listener.onTaskComplete(individualPaymentConfirmResponseMessage);
    }

}
