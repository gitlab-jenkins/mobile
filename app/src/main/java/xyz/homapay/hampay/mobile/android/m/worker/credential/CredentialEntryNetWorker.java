package xyz.homapay.hampay.mobile.android.m.worker.credential;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.CredentialService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by amir on 1/23/17.
 */

public class CredentialEntryNetWorker extends NetWorker<CredentialService> {

    public CredentialEntryNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel) {
        super(modelLayer, CredentialService.class, keyAgreementModel, true, true);
    }

    public void credential(String body, final OnNetworkLoadListener<ResponseMessage<RegistrationCredentialsResponse>> listener) {
        execute(service.credential(getPlainBodyRequest(body)), listener);
    }

}
