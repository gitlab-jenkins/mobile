package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import xyz.homapay.hampay.mobile.android.m.common.Const;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.ssl.SSLKeyStore;

/**
 * Created by mohammad on 7/15/2016 AD.
 */

public class ModelLayerImpl implements ModelLayer {

    private Context ctx;

    public ModelLayerImpl(final Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean isConnected() {
        return Connectivity.isConnected(ctx);
    }

    @Override
    public synchronized void log(String log) {

        if (log.length() > 3200) {
            Log.i("Ritmo-Network-Core", log.substring(0, 3200));
            log(log.substring(3200));
        } else {
            Log.i("Ritmo-Network-Core", log);
        }
    }

    @Nullable
    @Override
    public String getBaseUrl() {
        return Const.BASE_URL;
    }

    @Override
    public SSLKeyStore getSSLKeyStore() {
        return new SSLKeyStore(ctx);
    }

    @Override
    public void showNoNetworkDialog() {
        // TODO
    }

    @Override
    public File getCacheDir() {
        return ctx.getCacheDir();
    }

}
