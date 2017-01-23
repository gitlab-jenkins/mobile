package xyz.homapay.hampay.mobile.android.p.Credential;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;

/**
 * Created by amir on 1/22/17.
 */

public interface CredentialEntryView {

    void onError();

    void onRegisterResponse(boolean state, ResponseMessage<RegistrationCredentialsResponse> data, String message);

    void showProgressDialog();

    void dismissProgressDialog();

    void keyExchangeProblem();

}
