package xyz.homapay.hampay.mobile.android.p.security;

/**
 * Created by mohammad on 1/5/17.
 */

public interface KeyExchanger {

    void exchange();

    byte[] getKey();

    byte[] getIv();

    String getEncId();

}
