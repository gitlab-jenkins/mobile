package xyz.homapay.hampay.mobile.android.img;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by mohammad on 7/18/16.
 */

public class ImageCacheProvider {

    private static ImageCacheProvider instance;
    private Cache cache;
    private Context ctx;

    private ImageCacheProvider(final Context ctx) {
        this.ctx = ctx;
    }

    public static ImageCacheProvider getInstance(final Context ctx) {
        if (instance == null)
            instance = new ImageCacheProvider(ctx);
        return instance;
    }

    public Cache provideCache() {
        try {
            if (cache == null)
                cache = new Cache(new File(ctx.getCacheDir(), "http-cache"), 10 * 1024 * 1024);
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
