package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTACAccept extends AsyncTask<TACAcceptRequest, Void, ResponseMessage<TACAcceptResponse>> {

    private static final String TAG = "RequestTACAccept";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>> listener;


    public RequestTACAccept(Context context, AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>> listener)
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
    protected ResponseMessage<TACAcceptResponse> doInBackground(TACAcceptRequest... params) {

        WebServices webServices = new WebServices(context);

        return webServices.tACAcceptResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<TACAcceptResponse> tacAcceptResponseMessage)
    {
        super.onPostExecute(tacAcceptResponseMessage);
        listener.onTaskComplete(tacAcceptResponseMessage);
    }


}

