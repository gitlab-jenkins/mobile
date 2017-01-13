package xyz.homapay.hampay.mobile.android.m.worker.topup;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.TopUpService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/10/17.
 */

public class TopUpInfoNetWorker extends NetWorker<TopUpService> {

    public TopUpInfoNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, TopUpService.class, keyAgreementModel, encryption, gZip);
    }

    public void topUpInfo(String body, OnNetworkLoadListener<ResponseMessage<TopUpInfoResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.topUpInfo(requestBody), listener);
    }
}
