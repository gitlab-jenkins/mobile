package xyz.homapay.hampay.mobile.android.p.payment;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.pending.PendingPaymentNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class PendingPOListImpl extends Presenter<PendingPOListView> implements PendingPOList, OnNetworkLoadListener<ResponseMessage<PendingPOListResponse>> {

    private PendingPOListRequest pendingPOListRequest;

    public PendingPOListImpl(ModelLayer modelLayer, PendingPOListView view) {
        super(modelLayer, view);
    }

    @Override
    public void getList() {
        try {
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
            pendingPOListRequest = new PendingPOListRequest();
            pendingPOListRequest.setRequestUUID(UUID.randomUUID().toString());

            RequestMessage<PendingPOListRequest> request = new RequestMessage<>(pendingPOListRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), getKey(), getIv(), getEncId());
            new PendingPaymentNetWorker(modelLayer, getKeyAgreementModel(), true, false).getPendingPOList(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
            view.cancelProgress();
        }
    }

    @Override
    public void onKeyExchangeError() {
        try {
            view.cancelProgress();
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<PendingPOListResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onListLoaded(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
