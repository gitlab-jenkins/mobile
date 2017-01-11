package xyz.homapay.hampay.mobile.android.p.security;

import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;

/**
 * Created by mohammad on 1/5/17.
 */

public interface KeyExchangeView {

    void onExchangeDone(boolean state, KeyAgreementModel data, String message);

}
