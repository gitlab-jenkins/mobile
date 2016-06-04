package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;

import xyz.homapay.hampay.mobile.android.util.Constants;
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

        WebServices webServices = new WebServices(context, Constants.CONNECTION_TYPE);
        try {
            return webServices.imageDownloader(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        super.onPostExecute(bitmap);
        listener.onTaskComplete(bitmap);
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

