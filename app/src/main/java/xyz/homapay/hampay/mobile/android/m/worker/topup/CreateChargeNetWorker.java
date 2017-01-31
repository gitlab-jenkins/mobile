package xyz.homapay.hampay.mobile.android.m.worker.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.TopUpService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/12/2017 AD.
 */

public class CreateChargeNetWorker extends NetWorker<TopUpService> {

    public CreateChargeNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, TopUpService.class, keyAgreementModel, encryption, gZip);
    }

    public void createTopUp(String body, OnNetworkLoadListener<ResponseMessage<TopUpResponse>> listener) {
        execute(service.topUpCreate(getPlainBodyRequest(body)), listener);
    }

    public void topUpGetDetail(String body, OnNetworkLoadListener<ResponseMessage<TopUpResponse>> listener) {
        execute(service.topUpGetDetail(getPlainBodyRequest(body)), listener);
    }
}
