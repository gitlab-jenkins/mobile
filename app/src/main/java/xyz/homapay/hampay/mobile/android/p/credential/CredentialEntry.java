package xyz.homapay.hampay.mobile.android.p.credential;

import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;

/**
 * Created by amir on 1/23/17.
 */

public interface CredentialEntry {

    void credential(RegistrationCredentialsRequest registrationCredentialsRequest, String authToken, boolean permission);

}
