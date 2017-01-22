package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/10/17.
 */

public interface TopUpInfoView extends BaseView {

    void onInfoLoaded(boolean state, ResponseMessage<TopUpInfoResponse> data, String message);

}
