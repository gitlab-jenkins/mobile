package xyz.homapay.hampay.mobile.android.p.topup.pay;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.request.TopupRequest;
import xyz.homapay.hampay.common.pspproxy.model.response.TopupResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.topup.TopUpPayNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 2/4/17.
 */

public class TopUpPayImpl extends Presenter<TopUpPayView> implements TopUpPay, OnNetworkLoadListener<ResponseMessage<TopupResponse>> {


    private MessageEncryptor messageEncryptor;
    private TopupRequest topupRequest;
    private String authToken;
    private byte[] encKey;
    private byte[] ivKey;

    public TopUpPayImpl(ModelLayer modelLayer, TopUpPayView view){
        super(modelLayer, view);
    }

    private void onError() {
        try {
            view.cancelProgress();
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<TopupResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onTopUpPayResponse(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void topUpPay(TopupRequest topupRequest, String authToken, byte[] encKey, byte[] ivKey) {
        try {
            view.showProgress();
            messageEncryptor = new AESMessageEncryptor();
            this.topupRequest = topupRequest;
            this.authToken = authToken;
            this.encKey = encKey;
            this.ivKey = ivKey;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {

        try {
            topupRequest.setRequestUUID(UUID.randomUUID().toString());
            RequestMessage<TopupRequest> message = new RequestMessage<>(topupRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = messageEncryptor.encryptRequest(new Gson().toJson(message), encKey, ivKey, getEncId());
            new TopUpPayNetWorker(modelLayer, encKey, ivKey).topUpPay(strJson, this);
        }catch (Exception e) {
            view.keyExchangeProblem();
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        onError();
    }
}
