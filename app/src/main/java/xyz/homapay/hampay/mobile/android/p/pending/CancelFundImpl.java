package xyz.homapay.hampay.mobile.android.p.pending;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.CancelFundRequest;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.pending.PendingPaymentNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/22/17.
 */

public class CancelFundImpl extends Presenter<CancelFundView> implements CancelFund, OnNetworkLoadListener<ResponseMessage<CancelFundResponse>> {

    private FundType type;
    private String providerId;
    private CancelFundRequest cancelFundRequest;

    public CancelFundImpl(ModelLayer modelLayer, CancelFundView view) {
        super(modelLayer, view);
    }

    @Override
    public void cancel(FundType type, String providerId) {
        try {
            view.showProgress();
            this.type = type;
            this.providerId = providerId;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            cancelFundRequest = new CancelFundRequest();
            cancelFundRequest.setRequestUUID(UUID.randomUUID().toString());
            cancelFundRequest.setFundType(type);
            cancelFundRequest.setProviderId(providerId);

            RequestMessage<CancelFundRequest> request = new RequestMessage<>(cancelFundRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), getKey(), getIv(), getEncId());
            new PendingPaymentNetWorker(modelLayer, getKeyAgreementModel(), true, false).cancel(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<CancelFundResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onCancelDone(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
