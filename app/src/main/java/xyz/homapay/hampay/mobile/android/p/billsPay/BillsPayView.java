package xyz.homapay.hampay.mobile.android.p.billsPay;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.BillResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by amir on 2/4/17.
 */

public interface BillsPayView extends BaseView {

    void onBillPayResponse(boolean state, ResponseMessage<BillResponse> data, String message);

    void keyExchangeProblem();

}
