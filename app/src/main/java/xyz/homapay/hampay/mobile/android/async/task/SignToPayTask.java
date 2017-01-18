package xyz.homapay.hampay.mobile.android.async.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 9/12/16.
 */
public class SignToPayTask extends AsyncTask<Object, Object, Object> {

    private Context context;
    private String authToken;
    private OnTaskCompleted listener;
    private SignToPayRequest signToPayRequest;

    public SignToPayTask(Context context, OnTaskCompleted listener, SignToPayRequest signToPayRequest, String authToken){
        this.context = context;
        this.listener = listener;
        this.signToPayRequest = signToPayRequest;
        this.authToken = authToken;
    }


    @Override
    protected Object doInBackground(Object... params) {
        SecuredWebServices webService = new SecuredWebServices(context, ConnectionType.HTTPS, authToken);

        try {
            return webService.signToPay(signToPayRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        listener.OnTaskPreExecute();
    }

    @Override
    protected void onPostExecute(Object object){
        listener.OnTaskExecuted(object);
    }
}
