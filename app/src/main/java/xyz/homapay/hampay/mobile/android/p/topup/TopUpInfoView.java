package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;

/**
 * Created by mohammad on 1/10/17.
 */

public interface TopUpInfoView {

    void showProgress();

    void dismissProgress();

    void onError();

    void onInfoLoaded(boolean state, ResponseMessage<TopUpInfoResponse> data, String message);

}
