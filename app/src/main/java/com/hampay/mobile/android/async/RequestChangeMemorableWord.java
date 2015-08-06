package com.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestChangeMemorableWord extends AsyncTask<ChangeMemorableWordRequest, Void, ResponseMessage<ChangeMemorableWordResponse>>  {

    private static final String TAG = "RequestChangeMemorableWord";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<ChangeMemorableWordResponse>> listener;

    public RequestChangeMemorableWord(Context context, AsyncTaskCompleteListener<ResponseMessage<ChangeMemorableWordResponse>> listener)
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
    protected ResponseMessage<ChangeMemorableWordResponse> doInBackground(ChangeMemorableWordRequest... params) {


        WebServices webServices = new WebServices(context);
        return webServices.changeMemorableWordResponse(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<ChangeMemorableWordResponse> changeMemorableWordResponseMessage)
    {
        super.onPostExecute(changeMemorableWordResponseMessage);
        listener.onTaskComplete(changeMemorableWordResponseMessage);
    }

}
