package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.CalcFeeChargeRequest;
import xyz.homapay.hampay.common.core.model.response.CalcFeeChargeResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestCalcFeeCharge extends AsyncTask<CalcFeeChargeRequest, Void, ResponseMessage<CalcFeeChargeResponse>> {

    private static final String TAG = "RequestCalcFeeCharge";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<CalcFeeChargeResponse>> listener;


    public RequestCalcFeeCharge(Context context, AsyncTaskCompleteListener<ResponseMessage<CalcFeeChargeResponse>> listener)
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
    protected ResponseMessage<CalcFeeChargeResponse> doInBackground(CalcFeeChargeRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.calculateFeeCharge(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<CalcFeeChargeResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<CalcFeeChargeResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

