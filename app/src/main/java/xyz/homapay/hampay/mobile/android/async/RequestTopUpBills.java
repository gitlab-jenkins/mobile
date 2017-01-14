package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.model.BillsTokenDoWork;
import xyz.homapay.hampay.mobile.android.model.TopUpTokenDoWork;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.psp.topup.HHBArrayOfKeyValueOfstringstring;

/**
 * Created by amir on 7/3/15.
 */
public class RequestTopUpBills extends AsyncTask<TopUpTokenDoWork, Void, HHBArrayOfKeyValueOfstringstring> {

    private static final String TAG = "RequestTopUpBills";

    private Context context;
    private AsyncTaskCompleteListener<HHBArrayOfKeyValueOfstringstring> listener;
    private String payUrl;


    public RequestTopUpBills(Context context, AsyncTaskCompleteListener<HHBArrayOfKeyValueOfstringstring> listener, String payUrl) {
        this.context = context;
        this.listener = listener;
        this.payUrl = payUrl;
    }


    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected HHBArrayOfKeyValueOfstringstring doInBackground(TopUpTokenDoWork... params) {

        WebServices webServices = new WebServices(context);

        try {
            return webServices.tokenTopUp(params[0], payUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(HHBArrayOfKeyValueOfstringstring purchaseResponseMessage) {
        super.onPostExecute(purchaseResponseMessage);
        listener.onTaskComplete(purchaseResponseMessage);
    }

    @Override
    protected void onCancelled(HHBArrayOfKeyValueOfstringstring purchaseResponseMessage) {
        super.onCancelled(purchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

