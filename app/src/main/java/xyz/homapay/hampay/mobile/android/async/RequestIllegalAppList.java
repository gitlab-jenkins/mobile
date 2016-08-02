package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIllegalAppList extends AsyncTask<IllegalAppListRequest, Void, ResponseMessage<IllegalAppListResponse>> {

    private static final String TAG = "RequestIllegalAppList";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> listener;

    public RequestIllegalAppList(Context context, AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> listener)
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
    protected ResponseMessage<IllegalAppListResponse> doInBackground(IllegalAppListRequest... params) {

//        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);
        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.getIllegalAppList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<IllegalAppListResponse> illegalAppListResponseMessage)
    {
        super.onPostExecute(illegalAppListResponseMessage);
        listener.onTaskComplete(illegalAppListResponseMessage);
    }

}
