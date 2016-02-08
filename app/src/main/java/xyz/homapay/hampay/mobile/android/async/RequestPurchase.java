package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.psp.Vectorstring2stringMapEntry;

/**
 * Created by amir on 7/3/15.
 */
public class RequestPurchase extends AsyncTask<DoWorkInfo, Void, Vectorstring2stringMapEntry> {

    private static final String TAG = "RequestPurchase";

    private Context context;
    private AsyncTaskCompleteListener<Vectorstring2stringMapEntry> listener;


    public RequestPurchase(Context context, AsyncTaskCompleteListener<Vectorstring2stringMapEntry> listener)
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
    protected Vectorstring2stringMapEntry doInBackground(DoWorkInfo... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newPurchaseResponse(params[0]);
    }


    @Override
    protected void onPostExecute(Vectorstring2stringMapEntry purchaseResponseMessage)
    {
        super.onPostExecute(purchaseResponseMessage);
        listener.onTaskComplete(purchaseResponseMessage);
    }

    @Override
    protected void onCancelled(Vectorstring2stringMapEntry purchaseResponseMessage) {
        super.onCancelled(purchaseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

