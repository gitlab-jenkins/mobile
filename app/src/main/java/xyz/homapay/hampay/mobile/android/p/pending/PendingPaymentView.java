package xyz.homapay.hampay.mobile.android.p.pending;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/22/17.
 */

public interface PendingPaymentView extends BaseView {

    void onListLoaded(boolean state, ResponseMessage<PendingFundListResponse> data, String message);

}
