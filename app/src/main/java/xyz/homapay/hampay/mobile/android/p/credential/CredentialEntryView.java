package xyz.homapay.hampay.mobile.android.p.credential;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by amir on 1/22/17.
 */

public interface CredentialEntryView extends BaseView {

    void onRegisterResponse(boolean state, ResponseMessage<RegistrationCredentialsResponse> data, String message);

    void keyExchangeProblem();

}
