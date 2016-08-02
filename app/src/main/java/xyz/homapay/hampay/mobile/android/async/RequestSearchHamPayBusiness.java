package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

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

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.searchBusinessList(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
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

