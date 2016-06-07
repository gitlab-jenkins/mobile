package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.LatestInvoiceContactsRequest;
import xyz.homapay.hampay.common.core.model.response.LatestInvoiceContactsResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 3/18/16.
 */
public class RequestLatestInvoiceContacts extends AsyncTask<LatestInvoiceContactsRequest, Void, ResponseMessage<LatestInvoiceContactsResponse>> {

    private static final String TAG = "RequestLatestInvoiceContacts";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<LatestInvoiceContactsResponse>> listener;


    public RequestLatestInvoiceContacts(Context context, AsyncTaskCompleteListener<ResponseMessage<LatestInvoiceContactsResponse>> listener)
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
    protected ResponseMessage<LatestInvoiceContactsResponse> doInBackground(LatestInvoiceContactsRequest... params) {

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);

        try {
            return webServices.latestInvoiceContacts(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContactsResponseMessage)
    {
        super.onPostExecute(latestInvoiceContactsResponseMessage);
        listener.onTaskComplete(latestInvoiceContactsResponseMessage);
    }

    @Override
    protected void onCancelled(ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContactsResponseMessage) {
        super.onCancelled(latestInvoiceContactsResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

