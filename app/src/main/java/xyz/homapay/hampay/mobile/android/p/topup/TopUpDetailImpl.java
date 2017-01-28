package xyz.homapay.hampay.mobile.android.p.topup;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TopUpDetailRequest;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.topup.CreateChargeNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/14/2017 AD.
 */

public class TopUpDetailImpl extends Presenter<TopUpDetailView> implements TopUpDetail, OnNetworkLoadListener<ResponseMessage<TopUpResponse>> {

    private String providerId;

    public TopUpDetailImpl(ModelLayer modelLayer, TopUpDetailView view) {
        super(modelLayer, view);
    }

    @Override
    public void getDetail(String providerId) {
        try {
            view.showProgress();
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
            TopUpDetailRequest request = new TopUpDetailRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            request.setProviderId(providerId);
            RequestMessage<TopUpDetailRequest> requestRequestMessage = new RequestMessage<>(request, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());

            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(requestRequestMessage), getKey(), getIv(), getEncId());
            new CreateChargeNetWorker(modelLayer, getKeyAgreementModel(), true, false).topUpGetDetail(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        view.onError();
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<TopUpResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onDetailLoaded(status, data, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
