package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.response.ChangePassCodeResponse;
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


        WebServices webServices = new WebServices(context);
        return webServices.changePassCodeResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<ChangePassCodeResponse> changePassCodeResponseMessage)
    {
        super.onPostExecute(changePassCodeResponseMessage);
        listener.onTaskComplete(changePassCodeResponseMessage);
    }

}
