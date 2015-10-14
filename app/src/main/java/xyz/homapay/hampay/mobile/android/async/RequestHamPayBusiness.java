package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/31/15.
 */
public class RequestHamPayBusiness extends AsyncTask<BusinessListRequest, Void, ResponseMessage<BusinessListResponse>>  {

    private static final String TAG = "RequestHamPayBusiness";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> listener;


    public RequestHamPayBusiness(Context context, AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> listener)
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
    protected ResponseMessage<BusinessListResponse> doInBackground(BusinessListRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newGetHamPayBusiness(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<BusinessListResponse> businessListResponseMessage)
    {
        super.onPostExecute(businessListResponseMessage);
        listener.onTaskComplete(businessListResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<BusinessListResponse> businessListResponseResponseMessage) {
        super.onCancelled(businessListResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }

}

