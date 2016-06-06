package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RecentPendingFundRequest;
import xyz.homapay.hampay.common.core.model.response.RecentPendingFundResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRecentPendingFund extends AsyncTask<RecentPendingFundRequest, Void, ResponseMessage<RecentPendingFundResponse>> {

    private static final String TAG = "RequestRecentPendingFund";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RecentPendingFundResponse>> listener;


    public RequestRecentPendingFund(Context context, AsyncTaskCompleteListener<ResponseMessage<RecentPendingFundResponse>> listener)
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
    protected ResponseMessage<RecentPendingFundResponse> doInBackground(RecentPendingFundRequest... params) {

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.recentPendingFund(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (EncryptionException e) {
//            e.printStackTrace();
//        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<RecentPendingFundResponse> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<RecentPendingFundResponse> tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

