package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/12/2017 AD.
 */

public interface TopUpCreateView extends BaseView {

    void onCreated(boolean state, ResponseMessage<TopUpResponse> data, String message);

}
