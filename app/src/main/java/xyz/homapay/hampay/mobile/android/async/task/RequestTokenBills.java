package xyz.homapay.hampay.mobile.android.async.task;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.model.BillsTokenDoWork;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTokenBills extends AsyncTask<BillsTokenDoWork, Void, MKAArrayOfKeyValueOfstringstring> {

    private static final String TAG = "RequestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<MKAArrayOfKeyValueOfstringstring> listener;
    private String payUrl;


    public RequestTokenBills(Context context, AsyncTaskCompleteListener<MKAArrayOfKeyValueOfstringstring> listener, String payUrl)
    {
        this.context = context;
        this.listener = listener;
        this.payUrl = payUrl;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected MKAArrayOfKeyValueOfstringstring doInBackground(BillsTokenDoWork... params) {

        WebServices webServices = new WebServices(context);

        try {
            return webServices.tokenBills(params[0], payUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(MKAArrayOfKeyValueOfstringstring purchaseResponseMessage)
    {
        super.onPostExecute(purchaseResponseMessage);
        listener.onTaskComplete(purchaseResponseMessage);
    }

    @Override
    protected void onCancelled(MKAArrayOfKeyValueOfstringstring purchaseResponseMessage) {
        super.onCancelled(purchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

