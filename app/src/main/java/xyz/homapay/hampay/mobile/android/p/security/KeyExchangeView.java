package xyz.homapay.hampay.mobile.android.p.security;

import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;

/**
 * Created by mohammad on 1/5/17.
 */

public interface KeyExchangeView {

    void onExchangeDone(boolean state, ResponseMessage<KeyAgreementResponse> data, String message);

}
