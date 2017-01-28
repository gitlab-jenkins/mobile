package xyz.homapay.hampay.mobile.android.p.credential;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.credential.CredentialEntryNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangeView;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 1/23/17.
 */

public class CredentialEntryImpl extends Presenter<CredentialEntryView> implements CredentialEntry, KeyExchangeView, OnNetworkLoadListener<ResponseMessage<RegistrationCredentialsResponse>> {

    private MessageEncryptor messageEncryptor;
    private RegistrationCredentialsRequest registrationCredentialsRequest;
    private String authToken;
    private boolean permission;

    public CredentialEntryImpl(ModelLayer modelLayer, CredentialEntryView view) {
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
    public void onKeyExchangeDone() {

        try {

            List<ContactDTO> contacts;
            if (permission) {
                contacts = modelLayer.getUserContacts();
            } else {
                contacts = new ArrayList<>();
            }
            registrationCredentialsRequest.setContacts(contacts);
            registrationCredentialsRequest.setRequestUUID(UUID.randomUUID().toString());
            RequestMessage<RegistrationCredentialsRequest> message = new RequestMessage<>(registrationCredentialsRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = messageEncryptor.encryptRequest(new Gson().toJson(message), getKey(), getIv(), getEncId());

            new CredentialEntryNetWorker(modelLayer, getKeyAgreementModel()).credential(strJson, this);

        } catch (Exception e) {
            view.keyExchangeProblem();
            e.printStackTrace();
            onError();
        }


    }

    @Override
    public void onKeyExchangeError() {
        onError();
    }

    @Override
    public void credential(RegistrationCredentialsRequest registrationCredentialsRequest, String authToken, boolean permission) {
        try {
            view.showProgress();
            messageEncryptor = new AESMessageEncryptor();
            this.registrationCredentialsRequest = registrationCredentialsRequest;
            this.authToken = authToken;
            this.permission = permission;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<RegistrationCredentialsResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onRegisterResponse(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }


}
