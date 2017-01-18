package xyz.homapay.hampay.mobile.android.p.auth;

import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;

/**
 * Created by mohammad on 1/7/17.
 */

public interface RegisterEntry {

    void register(RegistrationEntryRequest registrationEntryRequest, String authToken);

}
