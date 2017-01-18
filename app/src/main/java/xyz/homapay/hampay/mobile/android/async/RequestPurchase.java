package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPurchase extends AsyncTask<DoWorkInfo, Void, CBUArrayOfKeyValueOfstringstring> {

    private static final String TAG = "RequestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<CBUArrayOfKeyValueOfstringstring> listener;
    private String tokenPayUrl;


    public RequestPurchase(Context context, AsyncTaskCompleteListener<CBUArrayOfKeyValueOfstringstring> listener, String tokenPayUrl)
    {
        this.context = context;
        this.listener = listener;
        this.tokenPayUrl = tokenPayUrl;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected CBUArrayOfKeyValueOfstringstring doInBackground(DoWorkInfo... params) {

        WebServices webServices = new WebServices(context);

        try {
            return webServices.purchaseResponse(params[0], tokenPayUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(CBUArrayOfKeyValueOfstringstring purchaseResponseMessage)
    {
        super.onPostExecute(purchaseResponseMessage);
        listener.onTaskComplete(purchaseResponseMessage);
    }

    @Override
    protected void onCancelled(CBUArrayOfKeyValueOfstringstring purchaseResponseMessage) {
        super.onCancelled(purchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

