package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 2/18/16.
 */

public class RequestIBANChange extends AsyncTask<IBANChangeRequest, Void, ResponseMessage<IBANChangeResponse>> {

    private static final String TAG = "RequestIBANChange";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IBANChangeResponse>> listener;


    public RequestIBANChange(Context context, AsyncTaskCompleteListener<ResponseMessage<IBANChangeResponse>> listener)
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
    protected ResponseMessage<IBANChangeResponse> doInBackground(IBANChangeRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.ibanChange(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<IBANChangeResponse> ibanChangeResposneMessage)
    {
        super.onPostExecute(ibanChangeResposneMessage);
        listener.onTaskComplete(ibanChangeResposneMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<IBANChangeResponse> ibanChangeResposneMessage) {
        super.onCancelled(ibanChangeResposneMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

