package xyz.homapay.hampay.mobile.android.m.worker.security;

import retrofit2.Call;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.KeyExchangeService;
import xyz.homapay.hampay.mobile.android.m.worker.NetWorker;

/**
 * Created by mohammad on 1/5/17.
 */

public class KeyNetWorker extends NetWorker<KeyExchangeService> {

    public KeyNetWorker(ModelLayer modelLayer) {
        super(modelLayer, KeyExchangeService.class);
    }

    public void exchange(RequestMessage<KeyAgreementRequest> requestBody, OnNetworkLoadListener<ResponseMessage<KeyAgreementResponse>> listener) {
        Call<ResponseMessage<KeyAgreementResponse>> call = service.exchange(requestBody);
        execute(call, listener);
    }
}
