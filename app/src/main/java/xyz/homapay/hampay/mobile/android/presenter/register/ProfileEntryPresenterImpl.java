package xyz.homapay.hampay.mobile.android.presenter.register;

import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.mobile.android.presenter.common.Presenter;

/**
 * Created by mohammad on 12/31/16.
 */

public class ProfileEntryPresenterImpl extends Presenter<ProfileEntryView> implements ProfileEntryPresenter {

    public ProfileEntryPresenterImpl(ProfileEntryView view) {
        super(view);
    }

    @Override
    public void register(RegistrationEntryRequest model) {
        if (!view.validate()) {
            view.showError();
            return;
        } else {

        }
    }
}
