package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestImageDownloader extends AsyncTask<String, Integer, Bitmap> {

    private static final String TAG = "RequestImageDownloader";

    private Context context;
    private AsyncTaskCompleteListener<Bitmap> listener;


    public RequestImageDownloader(Context context, AsyncTaskCompleteListener<Bitmap> listener)
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
    protected Bitmap doInBackground(String... params) {

        WebServices webServices = new WebServices(context);

        return webServices.newImageDownloader(params[0]);
    }


    @Override
    protected void onPostExecute(Bitmap tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    @Override
    protected void onCancelled(Bitmap tacResponseResponseMessage) {
        super.onCancelled(tacResponseResponseMessage);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

