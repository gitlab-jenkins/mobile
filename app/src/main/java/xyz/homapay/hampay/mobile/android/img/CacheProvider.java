package xyz.homapay.hampay.mobile.android.img;

import java.io.File;

import okhttp3.Cache;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;

/**
 * Created by mohammad on 7/18/16.
 */

public class CacheProvider {

    private static CacheProvider instance;
    private Cache cache;
    private ModelLayer modelLayer;

    private CacheProvider(final ModelLayer modelLayer) {
        this.modelLayer = modelLayer;
    }

    public static CacheProvider getInstance(final ModelLayer modelLayer) {
        if (instance == null)
            instance = new CacheProvider(modelLayer);
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
