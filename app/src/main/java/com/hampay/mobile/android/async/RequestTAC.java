package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTAC extends AsyncTask<TACRequest, Void, ResponseMessage<TACResponse>> {

    private static final String TAG = "RequestTAC";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<TACResponse>> listener;


    public RequestTAC(Context context, AsyncTaskCompleteListener<ResponseMessage<TACResponse>> listener)
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
    protected ResponseMessage<TACResponse> doInBackground(TACRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newTACResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<TACResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }


}

