package xyz.homapay.hampay.mobile.android.p.topup.pay;

import xyz.homapay.hampay.common.pspproxy.model.request.TopupRequest;

/**
 * Created by amir on 2/4/17.
 */

public interface TopUpPay {

    void topUpPay(TopupRequest topupRequest, String authToken, byte[] encKey, byte[] ivKey);

}
