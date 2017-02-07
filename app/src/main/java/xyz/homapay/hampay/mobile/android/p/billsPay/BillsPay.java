package xyz.homapay.hampay.mobile.android.p.billsPay;

import xyz.homapay.hampay.common.pspproxy.model.request.BillRequest;

/**
 * Created by amir on 2/4/17.
 */

public interface BillsPay {

    void billsPay(BillRequest billRequest, String authToken, byte[] encKey, byte[] ivKey);

}
