package xyz.homapay.hampay.mobile.android.p.netpay;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.NetPayResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by amir on 2/4/17.
 */

public interface NetPayView extends BaseView {

    void onNetPayResponse(boolean state, ResponseMessage<NetPayResponse> data, String message);

    void keyExchangeProblem();

}
