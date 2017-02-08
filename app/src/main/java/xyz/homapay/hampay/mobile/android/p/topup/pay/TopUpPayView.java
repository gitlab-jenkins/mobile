package xyz.homapay.hampay.mobile.android.p.topup.pay;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.TopupResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by amir on 2/4/17.
 */

public interface TopUpPayView extends BaseView {

    void onTopUpPayResponse(boolean state, ResponseMessage<TopupResponse> data, String message);

    void keyExchangeProblem();

}
