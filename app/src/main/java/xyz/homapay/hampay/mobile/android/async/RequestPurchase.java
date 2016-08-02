package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPurchase extends AsyncTask<DoWorkInfo, Void, TWAArrayOfKeyValueOfstringstring> {

    private static final String TAG = "RequestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<TWAArrayOfKeyValueOfstringstring> listener;


    public RequestPurchase(Context context, AsyncTaskCompleteListener<TWAArrayOfKeyValueOfstringstring> listener)
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
    protected TWAArrayOfKeyValueOfstringstring doInBackground(DoWorkInfo... params) {

        WebServices webServices = new WebServices(context);

        try {
            return webServices.newPurchaseResponse(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(TWAArrayOfKeyValueOfstringstring purchaseResponseMessage)
    {
        super.onPostExecute(purchaseResponseMessage);
        listener.onTaskComplete(purchaseResponseMessage);
    }

    @Override
    protected void onCancelled(TWAArrayOfKeyValueOfstringstring purchaseResponseMessage) {
        super.onCancelled(purchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

