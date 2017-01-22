package xyz.homapay.hampay.mobile.android.p.pending;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;

/**
 * Created by mohammad on 1/22/17.
 */

public interface PendingPaymentView {

    void showProgress();

    void cancelProgress();

    void onError();

    void onListLoaded(boolean state, ResponseMessage<PendingFundListResponse> data, String message);

}
