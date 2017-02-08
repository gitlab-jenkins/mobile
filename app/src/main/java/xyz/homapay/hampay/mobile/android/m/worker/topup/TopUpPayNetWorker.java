package xyz.homapay.hampay.mobile.android.m.worker.topup;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.TopupResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.TopUpPayService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;
import xyz.homapay.hampay.mobile.android.m.worker.common.ServiceType;

/**
 * Created by amir on 2/4/17.
 */

public class TopUpPayNetWorker extends NetWorker<TopUpPayService> {

    public TopUpPayNetWorker(ModelLayer modelLayer, byte[] encKey, byte[] ivKey) {
        super(modelLayer, TopUpPayService.class, encKey, ivKey, true, false, ServiceType.PSP);
    }

    public void topUpPay(String body, final OnNetworkLoadListener<ResponseMessage<TopupResponse>> listener) {
        execute(service.topUpPay(getPlainBodyRequest(body)), listener);
    }

}
