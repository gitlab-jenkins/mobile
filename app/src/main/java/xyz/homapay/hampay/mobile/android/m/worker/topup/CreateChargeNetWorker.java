package xyz.homapay.hampay.mobile.android.m.worker.topup;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.topUpCreate(requestBody), listener);
    }

    public void topUpGetDetail(String body, OnNetworkLoadListener<ResponseMessage<TopUpResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.topUpGetDetail(requestBody), listener);
    }
}
