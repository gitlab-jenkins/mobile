package xyz.homapay.hampay.mobile.android.p.netpay;

import xyz.homapay.hampay.common.pspproxy.model.request.NetPayRequest;

/**
 * Created by amir on 2/4/17.
 */

public interface NetPay {

    void netPay(NetPayRequest netPayRequest, String authToken, byte[] encKey, byte[] ivKey);

}
