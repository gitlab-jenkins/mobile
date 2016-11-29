package xyz.homapay.hampay.mobile.android.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.core.model.request.RetrieveImageRequest;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionMethod;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;
import xyz.homapay.hampay.mobile.android.webservice.SecuredProxyService;

public class ImageManager {

    private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();

    private SharedPreferences prefs;
    private String authToken = "";
    private File cacheDir;
    private ImageQueue imageQueue = new ImageQueue();
    private Thread imageLoaderThread = new Thread(new ImageQueueManager());

    private Activity activity;

    private boolean forceDownload = false;

    public ImageManager(Activity activity, long _cacheDuration, boolean forceDownload) {

        this.forceDownload = forceDownload;

        this.activity = activity;
        imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        prefs =  activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir, activity.getFilesDir().getPath() + "/.images");

        } else {
            cacheDir = activity.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public void displayImage(String imageId, ImageView imageView, int defaultDrawableId) {

        if(imageMap.containsKey(imageId)) {
            imageView.setImageBitmap(imageMap.get(imageId));

        } else {
            queueImage(imageId, imageView, defaultDrawableId);
            imageView.setImageResource(defaultDrawableId);
        }
    }

    private void queueImage(String imageId, ImageView imageView, int defaultDrawableId) {
        imageQueue.Clean(imageView);
        ImageRef p = new ImageRef(imageId, imageView, defaultDrawableId);

        synchronized(imageQueue.imageRefs) {
            imageQueue.imageRefs.push(p);
            imageQueue.imageRefs.notifyAll();
        }
        if(imageLoaderThread.getState() == Thread.State.NEW) {
            imageLoaderThread.start();
        }
    }

    private Bitmap getBitmap(String imageId) {

        try {
            Bitmap bitmap = null;
            String filename = String.valueOf(imageId);
            File bitmapFile = new File(cacheDir, filename);
            if (forceDownload){
                URL imageURL = new URL(Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + "retrieve");
                SecuredProxyService proxyService = new SecuredProxyService(true, activity, ConnectionType.HTTPS, ConnectionMethod.POST, imageURL);
                RetrieveImageRequest retrieveImageRequest = new RetrieveImageRequest();
                retrieveImageRequest.setImageId(imageId);
                retrieveImageRequest.setRequestUUID(UUID.randomUUID().toString());
                RequestMessage<RetrieveImageRequest> message = new RequestMessage<>(retrieveImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
                Type requestType = new TypeToken<RequestMessage<RetrieveImageRequest>>() {}.getType();
                String jsonRequest = new Gson().toJson(message, requestType);
                proxyService.setJsonBody(jsonRequest);

                bitmap = scaleDown(proxyService.imageInputStream(), 100, true);
                proxyService.closeConnection();
                writeFile(bitmap, bitmapFile);
            }
            else {
                bitmap = decodeFile(bitmapFile, 100, 100);
                if (bitmap != null) {
                }else {
                    URL imageURL = new URL(Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + "retrieve");
                    SecuredProxyService proxyService = new SecuredProxyService(true, activity, ConnectionType.HTTPS, ConnectionMethod.POST, imageURL);
                    RetrieveImageRequest retrieveImageRequest = new RetrieveImageRequest();
                    retrieveImageRequest.setImageId(imageId);
                    retrieveImageRequest.setRequestUUID(UUID.randomUUID().toString());
                    RequestMessage<RetrieveImageRequest> message = new RequestMessage<>(retrieveImageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
                    Type requestType = new TypeToken<RequestMessage<RetrieveImageRequest>>() {}.getType();
                    String jsonRequest = new Gson().toJson(message, requestType);
                    proxyService.setJsonBody(jsonRequest);

                    bitmap = scaleDown(proxyService.imageInputStream(), 100, true);
                    proxyService.closeConnection();
                    writeFile(bitmap, bitmapFile);
                }
            }

            return bitmap;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

    private void writeFile(Bitmap bmp, File f) {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try { if (out != null ) out.close(); }
            catch(Exception ex) {}
        }
    }

    private class ImageRef {
        public String imageId;
        public ImageView imageView;
        public int defDrawableId;

        public ImageRef(String imageId, ImageView i, int defaultDrawableId) {
            this.imageId = imageId;
            imageView = i;
            defDrawableId = defaultDrawableId;
        }
    }

    //stores list of images to download
    private class ImageQueue {
        private Stack<ImageRef> imageRefs =
                new Stack<ImageRef>();

        //removes all instances of this ImageView
        public void Clean(ImageView view) {

            for(int i = 0 ;i < imageRefs.size();) {
                if(imageRefs.get(i).imageView == view)
                    imageRefs.remove(i);
                else ++i;
            }
        }
    }

    private class ImageQueueManager implements Runnable {
        @Override
        public void run() {
            try {
                while(true) {
                    // Thread waits until there are images in the
                    // queue to be retrieved
                    if(imageQueue.imageRefs.size() == 0) {
                        synchronized(imageQueue.imageRefs) {
                            imageQueue.imageRefs.wait();
                        }
                    }

                    // When we have images to be loaded
                    if(imageQueue.imageRefs.size() != 0) {
                        ImageRef imageToLoad;

                        synchronized(imageQueue.imageRefs) {
                            imageToLoad = imageQueue.imageRefs.pop();
                        }

                        Bitmap bitmap = getBitmap(imageToLoad.imageId);
                        imageMap.put(imageToLoad.imageId, bitmap);
                        Object tag = imageToLoad.imageView.getTag();

                        // Make sure we have the right view - thread safety defender
                        if(tag != null && tag.equals(imageToLoad.imageId)) {
                            BitmapDisplayer bmpDisplayer =
                                    new BitmapDisplayer(bitmap, imageToLoad.imageView, imageToLoad.defDrawableId);
                            activity.runOnUiThread(bmpDisplayer);
                        }
                    }

                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {}
        }
    }

    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        ImageView imageView;
        int defDrawableId;

        public BitmapDisplayer(Bitmap b, ImageView i, int defaultDrawableId) {
            bitmap=b;
            imageView=i;
            defDrawableId = defaultDrawableId;
        }

        public void run() {
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageResource(defDrawableId);
        }
    }
}