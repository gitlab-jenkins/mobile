package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;

/**
 * Created by mohammad on 1/14/2017 AD.
 */

public interface TopUpDetailView {

    void showProgress();

    void dissmisProgress();

    void onError();

    void onDetailLoaded(boolean status, ResponseMessage<TopUpResponse> data, String message);

}
