package xyz.homapay.hampay.mobile.android.m.common;

import java.io.File;

import xyz.homapay.hampay.mobile.android.ssl.SSLKeyStore;

/**
 * Created by mohammad on 7/15/2016 AD.
 */

public interface ModelLayer {

    void showNoNetworkDialog();

    File getCacheDir();

    boolean isConnected();

    void log(final String log);

    String getBaseUrl();

    SSLKeyStore getSSLKeyStore();

}
