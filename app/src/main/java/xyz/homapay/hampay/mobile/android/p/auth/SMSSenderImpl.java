package xyz.homapay.hampay.mobile.android.p.auth;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.authorization.SendSMSNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/24/2017 AD.
 */

public class SMSSenderImpl extends Presenter<SMSSenderView> implements SMSSender, OnNetworkLoadListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {

    private String registerUserIdToken;

    public SMSSenderImpl(ModelLayer modelLayer, SMSSenderView view) {
        super(modelLayer, view);
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
            registrationSendSmsTokenRequest.setRequestUUID(UUID.randomUUID().toString());
            registrationSendSmsTokenRequest.setUserIdToken(registerUserIdToken);

            RequestMessage<RegistrationSendSmsTokenRequest> request = new RequestMessage<>(registrationSendSmsTokenRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), getKey(), getIv(), getEncId());
            new SendSMSNetWorker(modelLayer, getKeyAgreementModel(), true, false).sendSMS(strJson, this);
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
    public SMSSender send(String registerUserIdToken) {
        try {
            this.registerUserIdToken = registerUserIdToken;
            view.showProgress();
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        } finally {
            return this;
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<RegistrationSendSmsTokenResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onSMSSent(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
