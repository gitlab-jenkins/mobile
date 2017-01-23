package xyz.homapay.hampay.mobile.android.p.payment;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public interface PendingPOListView extends BaseView {

    void onListLoaded(boolean state, ResponseMessage<PendingPOListResponse> data, String message);

}
