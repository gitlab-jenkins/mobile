package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.response.ChangePassCodeResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestChangePassCode extends AsyncTask<ChangePassCodeRequest, Void, ResponseMessage<ChangePassCodeResponse>>  {

    private static final String TAG = "RequestHttpRegistrationEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<ChangePassCodeResponse>> listener;

    public RequestChangePassCode(Context context, AsyncTaskCompleteListener<ResponseMessage<ChangePassCodeResponse>> listener)
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
    protected ResponseMessage<ChangePassCodeResponse> doInBackground(ChangePassCodeRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);
        try {
            return webServices.changePassCodeResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<ChangePassCodeResponse> changePassCodeResponseMessage)
    {
        super.onPostExecute(changePassCodeResponseMessage);
        listener.onTaskComplete(changePassCodeResponseMessage);
    }

}
