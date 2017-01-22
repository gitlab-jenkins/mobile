package xyz.homapay.hampay.mobile.android.m.worker.pending;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.PendingPaymentsService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/22/17.
 */

public class PendingPaymentNetWorker extends NetWorker<PendingPaymentsService> {

    public PendingPaymentNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, PendingPaymentsService.class, keyAgreementModel, encryption, gZip);
    }

    public void pendingList(String body, OnNetworkLoadListener<ResponseMessage<PendingFundListResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.pendingList(requestBody), listener);
    }

    public void cancel(String body, OnNetworkLoadListener<ResponseMessage<CancelFundResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.cancel(requestBody), listener);
    }
}
