package xyz.homapay.hampay.mobile.android.p.auth;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;

/**
 * Created by mohammad on 1/7/17.
 */

public interface RegisterEntryView {

    void onError();

    void onRegisterResponse(boolean state, ResponseMessage<RegistrationEntryResponse> data, String message);

    void showProgressDialog();

    void dismissProgressDialog();

    void keyExchangeProblem();

    void keyExchangeDone();

}
