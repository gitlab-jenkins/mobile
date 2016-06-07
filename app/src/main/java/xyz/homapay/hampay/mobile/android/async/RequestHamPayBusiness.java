package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
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

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.getHamPayBusinesses(params[0]);
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

