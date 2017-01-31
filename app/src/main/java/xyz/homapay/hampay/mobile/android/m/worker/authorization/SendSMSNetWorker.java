package xyz.homapay.hampay.mobile.android.m.worker.authorization;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.AuthService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/24/2017 AD.
 */

public class SendSMSNetWorker extends NetWorker<AuthService> {

    public SendSMSNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, AuthService.class, keyAgreementModel, encryption, gZip);
    }

    public void sendSMS(String body, final OnNetworkLoadListener<ResponseMessage<RegistrationSendSmsTokenResponse>> listener) {
        execute(service.sendSMS(getPlainBodyRequest(body)), listener);
    }

}
