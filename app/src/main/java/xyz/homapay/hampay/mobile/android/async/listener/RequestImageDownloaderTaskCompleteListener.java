package xyz.homapay.hampay.mobile.android.async.listener;

import android.graphics.Bitmap;
import android.widget.ImageView;

import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;

/**
 * Created by amir on 10/18/15.
 */
public class RequestImageDownloaderTaskCompleteListener implements AsyncTaskCompleteListener<Bitmap> {

    private ImageView imageView;

    public RequestImageDownloaderTaskCompleteListener(ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    public void onTaskComplete(Bitmap bitmap)
    {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onTaskPreRun() {   }
}