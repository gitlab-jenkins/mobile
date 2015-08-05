package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BusinessListRequest;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.mobile.android.webservice.WebServices;

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

        return webServices.getHamPayBusiness(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<BusinessListResponse> businessListResponseMessage)
    {
        super.onPostExecute(businessListResponseMessage);
        listener.onTaskComplete(businessListResponseMessage);
    }

}

