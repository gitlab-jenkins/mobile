package xyz.homapay.hampay.mobile.android.m.worker.billsPay;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.BillResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.BillsPayService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;
import xyz.homapay.hampay.mobile.android.m.worker.common.ServiceType;

/**
 * Created by amir on 2/4/17.
 */

public class BillsPayNetWorker extends NetWorker<BillsPayService> {

    public BillsPayNetWorker(ModelLayer modelLayer, byte[] encKey, byte[] ivKey) {
        super(modelLayer, BillsPayService.class, encKey, ivKey, true, false, ServiceType.PSP);
    }

    public void billsPay(String body, final OnNetworkLoadListener<ResponseMessage<BillResponse>> listener) {
        execute(service.billsPay(getPlainBodyRequest(body)), listener);
    }

}
