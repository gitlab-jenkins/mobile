package xyz.homapay.hampay.mobile.android.m.worker.business;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.BusinesService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public class BusinessNetWorker extends NetWorker<BusinesService> {

    public BusinessNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, BusinesService.class, keyAgreementModel, encryption, gZip);
    }

    public void businessList(String body, OnNetworkLoadListener<ResponseMessage<BusinessListResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.businessList(requestBody), listener);
    }

    public void businessSearch(String body, OnNetworkLoadListener<ResponseMessage<BusinessListResponse>> listener) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
        execute(service.businessSearch(requestBody), listener);
    }
}
