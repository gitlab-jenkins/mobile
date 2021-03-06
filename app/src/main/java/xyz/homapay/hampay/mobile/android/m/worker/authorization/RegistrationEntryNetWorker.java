package xyz.homapay.hampay.mobile.android.m.worker.authorization;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.AuthService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/7/17.
 */

public class RegistrationEntryNetWorker extends NetWorker<AuthService> {

    public RegistrationEntryNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel) {
        super(modelLayer, AuthService.class, keyAgreementModel, true, true);
    }

    public void register(String body, final OnNetworkLoadListener<ResponseMessage<RegistrationEntryResponse>> listener) {
        execute(service.register(getPlainBodyRequest(body)), listener);
    }

}
