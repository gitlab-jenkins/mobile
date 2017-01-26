package xyz.homapay.hampay.mobile.android.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.List;

import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
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
            Log.i("Hampay-Network-Core", log.substring(0, 3200));
            log(log.substring(3200));
        } else {
            Log.i("Hampay-Network-Core", log);
        }
    }

    @Nullable
    @Override
    public String getBaseUrl() {
        return Constants.HTTPS_SERVER_IP;
    }

    @Override
    public SSLKeyStore getSSLKeyStore() {
        return new SSLKeyStore(ctx);
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        return new DeviceInfo(ctx);
    }


    @Override
    public String getAuthToken() {
        return AppManager.getAuthToken(ctx);
    }

    @Override
    public List<ContactDTO> getUserContacts() {
        UserContacts userContacts = new UserContacts(ctx);
        List<ContactDTO> contacts = userContacts.read();
        return contacts;
    }

    @Override
    public void showServerConnectionError() {
        try {
            new HamPayDialog((Activity) ctx).showNoServerConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNoNetworkDialog() {
        try {
            new HamPayDialog((Activity) ctx).showNoNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getCacheDir() {
        return ctx.getCacheDir();
    }

}
