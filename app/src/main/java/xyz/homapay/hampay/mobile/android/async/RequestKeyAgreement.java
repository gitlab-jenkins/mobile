package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestKeyAgreement extends AsyncTask<KeyAgreementRequest, Void, ResponseMessage<KeyAgreementResponse>> {

    private static final String TAG = "RequestKeyAgreement";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<KeyAgreementResponse>> listener;


    public RequestKeyAgreement(Context context, AsyncTaskCompleteListener<ResponseMessage<KeyAgreementResponse>> listener)
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
    protected ResponseMessage<KeyAgreementResponse> doInBackground(KeyAgreementRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.keyAgreement(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<KeyAgreementResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<KeyAgreementResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

