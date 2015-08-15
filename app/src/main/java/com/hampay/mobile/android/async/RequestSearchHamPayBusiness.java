package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/31/15.
 */
public class RequestSearchHamPayBusiness extends AsyncTask<BusinessSearchRequest, Void, ResponseMessage<BusinessListResponse>>  {

    private static final String TAG = "RequestSearchHamPayBusiness";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> listener;


    public RequestSearchHamPayBusiness(Context context, AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> listener)
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
    protected ResponseMessage<BusinessListResponse> doInBackground(BusinessSearchRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newSearchBusinessList(params[0]);
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

