package xyz.homapay.hampay.mobile.android.img;

import java.io.File;

import okhttp3.Cache;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;

/**
 * Created by mohammad on 7/18/16.
 */

public class ImageCacheProvider {

    private static ImageCacheProvider instance;
    private Cache cache;
    private ModelLayer modelLayer;

    private ImageCacheProvider(final ModelLayer modelLayer) {
        this.modelLayer = modelLayer;
    }

    public static ImageCacheProvider getInstance(final ModelLayer modelLayer) {
        if (instance == null)
            instance = new ImageCacheProvider(modelLayer);
        return instance;
    }

    public Cache provideCache() {
        try {
            if (cache == null)
                cache = new Cache(new File(modelLayer.getCacheDir(), "http-cache"), 10 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void clearCache() {
        try {
            cache.evictAll();
            cache.flush();
            cache = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
