package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;

/**
 * Created by mohammad on 1/12/2017 AD.
 */

public interface TopUpCreateView {

    void showProgress();

    void cancelProgress();

    void onError();

    void onCreated(boolean state, ResponseMessage<TopUpResponse> data, String message);

}
