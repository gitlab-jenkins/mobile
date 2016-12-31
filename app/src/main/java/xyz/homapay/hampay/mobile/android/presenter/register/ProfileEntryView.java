package xyz.homapay.hampay.mobile.android.presenter.register;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;

/**
 * Created by mohammad on 12/31/16.
 */

public interface ProfileEntryView {

    boolean validate();

    boolean showError();

    boolean hasPermission();

    void requestPermission();

    void onRegistrationComplete(ResponseMessage<RegistrationEntryResponse> registrationEntryResponse);
}
