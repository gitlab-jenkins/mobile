package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.response.ChangeEmailResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestChangeEmail extends AsyncTask<ChangeEmailRequest, Void, ResponseMessage<ChangeEmailResponse>>  {

    private static final String TAG = "RequestChangeEmail";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<ChangeEmailResponse>> listener;

    public RequestChangeEmail(Context context, AsyncTaskCompleteListener<ResponseMessage<ChangeEmailResponse>> listener)
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
    protected ResponseMessage<ChangeEmailResponse> doInBackground(ChangeEmailRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.changeEmailResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<ChangeEmailResponse> changeMemorableWordResponseMessage)
    {
        super.onPostExecute(changeMemorableWordResponseMessage);
        listener.onTaskComplete(changeMemorableWordResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<ChangeEmailResponse> changeEmailResponseResponseMessage) {
        super.onCancelled(changeEmailResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}
