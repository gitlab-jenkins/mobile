package xyz.homapay.hampay.mobile.android.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import xyz.homapay.hampay.mobile.android.webservice.ConnectionMethod;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;
import xyz.homapay.hampay.mobile.android.webservice.ProxyService;

public class ImageManager {

    private HashMap<String, SoftReference<Bitmap>> imageMap = new HashMap<String, SoftReference<Bitmap>>();

    private File cacheDir;
    private ImageQueue imageQueue = new ImageQueue();
    private Thread imageLoaderThread = new Thread(new ImageQueueManager());

    private Activity activity;

    private boolean forceDownload = false;

    public ImageManager(Activity activity, long _cacheDuration, boolean forceDownload) {

        this.forceDownload = forceDownload;

        this.activity = activity;
        imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDir = new File(sdDir,activity.getFilesDir().getPath());

        } else {
            cacheDir = activity.getCacheDir();
        }

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public void displayImage(String url, ImageView imageView, int defaultDrawableId) {

        if(imageMap.containsKey(url)) {
            imageView.setImageBitmap(imageMap.get(url).get());

        } else {
            queueImage(url, imageView, defaultDrawableId);
            imageView.setImageResource(defaultDrawableId);
        }
    }

    private void queueImage(String url, ImageView imageView, int defaultDrawableId) {
        imageQueue.Clean(imageView);
        ImageRef p = new ImageRef(url, imageView, defaultDrawableId);

        synchronized(imageQueue.imageRefs) {
            imageQueue.imageRefs.push(p);
            imageQueue.imageRefs.notifyAll();
        }
        if(imageLoaderThread.getState() == Thread.State.NEW) {
            imageLoaderThread.start();
        }
    }

    private Bitmap getBitmap(String url) {

        try {
            String filename = String.valueOf(url.split("/")[6].hashCode());
            File bitmapFile = new File(cacheDir, filename);
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getPath());
            if (!forceDownload) {
                if (bitmap != null) {
                    return bitmap;
                }
            }
            else {
                URL imageURL = new URL(url);
                ProxyService proxyService = new ProxyService(activity, ConnectionType.HTTPS, ConnectionMethod.GET, imageURL);
                bitmap = proxyService.imageInputStream();
                proxyService.closeConnection();
                writeFile(bitmap, bitmapFile);
            }

            return bitmap;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
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
        public String url;
        public ImageView imageView;
        public int defDrawableId;

        public ImageRef(String u, ImageView i, int defaultDrawableId) {
            url=u;
            imageView=i;
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

                        Bitmap bmp = getBitmap(imageToLoad.url);
                        imageMap.put(imageToLoad.url, new SoftReference<Bitmap>(bmp));
                        Object tag = imageToLoad.imageView.getTag();

                        // Make sure we have the right view - thread safety defender
                        if(tag != null && tag.equals(imageToLoad.url.split("/")[6])) {
                            BitmapDisplayer bmpDisplayer =
                                    new BitmapDisplayer(bmp, imageToLoad.imageView, imageToLoad.defDrawableId);
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