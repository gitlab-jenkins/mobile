package xyz.homapay.hampay.mobile.android.p.pending;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.pending.PendingPaymentNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/22/17.
 */

public class PendingPaymentImpl extends Presenter<PendingPaymentView> implements PendingPayment, OnNetworkLoadListener<ResponseMessage<PendingFundListResponse>> {

    private PendingFundListRequest pendingFundRequest;
    private FundType type;

    public PendingPaymentImpl(ModelLayer modelLayer, PendingPaymentView view) {
        super(modelLayer, view);
    }

    @Override
    public void getList(FundType type) {
        try {
            this.type = type;
            view.showProgress();
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            pendingFundRequest = new PendingFundListRequest();
            pendingFundRequest.setRequestUUID(UUID.randomUUID().toString());
            pendingFundRequest.setType(type);

            RequestMessage<PendingFundListRequest> request = new RequestMessage<>(pendingFundRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), getKey(), getIv(), getEncId());
            new PendingPaymentNetWorker(modelLayer, getKeyAgreementModel(), true, false).pendingList(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        try {
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<PendingFundListResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onListLoaded(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
