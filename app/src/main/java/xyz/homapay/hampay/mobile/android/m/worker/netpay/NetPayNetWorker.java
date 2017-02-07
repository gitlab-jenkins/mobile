package xyz.homapay.hampay.mobile.android.m.worker.netpay;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.NetPayResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.NetPayService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;
import xyz.homapay.hampay.mobile.android.m.worker.common.ServiceType;

/**
 * Created by amir on 2/4/17.
 */

public class NetPayNetWorker extends NetWorker<NetPayService> {

    public NetPayNetWorker(ModelLayer modelLayer, byte[] encKey, byte[] ivKey) {
        super(modelLayer, NetPayService.class, encKey, ivKey, true, false, ServiceType.PSP);
    }

    public void netPay(String body, final OnNetworkLoadListener<ResponseMessage<NetPayResponse>> listener) {
        execute(service.netPay(getPlainBodyRequest(body)), listener);
    }

}
