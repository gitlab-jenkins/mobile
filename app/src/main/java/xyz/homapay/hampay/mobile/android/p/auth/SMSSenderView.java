package xyz.homapay.hampay.mobile.android.p.auth;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/24/2017 AD.
 */

public interface SMSSenderView extends BaseView {

    void onSMSSent(boolean state, ResponseMessage<RegistrationSendSmsTokenResponse> data, String message);

}
