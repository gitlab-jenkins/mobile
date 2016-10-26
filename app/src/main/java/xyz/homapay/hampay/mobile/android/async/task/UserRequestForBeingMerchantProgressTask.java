package xyz.homapay.hampay.mobile.android.async.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.core.model.request.UserRequestForBeingMerchantProgressRequest;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 10/26/16.
 */
public class UserRequestForBeingMerchantProgressTask extends AsyncTask<Object, Object, Object> {

    private Context context;
    private String authToken;
    private OnTaskCompleted listener;
    private UserRequestForBeingMerchantProgressRequest userRequestForBeingMerchantProgressRequest;

    public UserRequestForBeingMerchantProgressTask(Context context, OnTaskCompleted listener, UserRequestForBeingMerchantProgressRequest userRequestForBeingMerchantProgressRequest, String authToken){
        this.context = context;
        this.listener = listener;
        this.userRequestForBeingMerchantProgressRequest = userRequestForBeingMerchantProgressRequest;
        this.authToken = authToken;
    }

    @Override
    protected Object doInBackground(Object... params) {
        SecuredWebServices webService = new SecuredWebServices(context, ConnectionType.HTTPS, authToken);

        try {
            return webService.userRequestForBeingMerchantProgress(userRequestForBeingMerchantProgressRequest);
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
