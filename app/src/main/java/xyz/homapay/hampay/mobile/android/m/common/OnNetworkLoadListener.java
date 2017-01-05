package xyz.homapay.hampay.mobile.android.m.common;

/**
 * Created by mohammad on 5/17/16.
 */
public interface OnNetworkLoadListener<T> {

    void onNetworkLoad(final boolean status, final T data, final String message);

}
