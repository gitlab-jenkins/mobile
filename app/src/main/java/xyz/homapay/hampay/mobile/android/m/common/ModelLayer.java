package xyz.homapay.hampay.mobile.android.m.common;

import java.io.File;
import java.util.List;

import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.mobile.android.ssl.SSLKeyStore;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;

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

    DeviceInfo getDeviceInfo();

    String getAuthToken();

    List<ContactDTO> getUserContacts();

    void showServerConnectionError();

    String getPspBaseUrl();
}
