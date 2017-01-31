package xyz.homapay.hampay.mobile.android.p.invitation;

import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.FriendsInvitationRequest;
import xyz.homapay.hampay.common.core.model.response.FriendsInvitationResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.invitation.FriendsInvitationNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/31/17.
 */

public class FriendsInvitationImpl extends Presenter<FriendsInvitationView> implements FriendsInvitation, OnNetworkLoadListener<ResponseMessage<FriendsInvitationResponse>> {

    private List<String> contacts;

    public FriendsInvitationImpl(ModelLayer modelLayer, FriendsInvitationView view) {
        super(modelLayer, view);
    }

    @Override
    public void invite(List<String> contacts) {
        try {
            view.showProgress();
            this.contacts = contacts;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            FriendsInvitationRequest request = new FriendsInvitationRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            request.setRecipients(this.contacts);

            RequestMessage<FriendsInvitationRequest> friendsInvitationRequest = new RequestMessage<>(request, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(friendsInvitationRequest), getKey(), getIv(), getEncId());
            new FriendsInvitationNetWorker(modelLayer, getKeyAgreementModel(), true, false).sendInvitation(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onKeyExchangeError() {
        try {
            view.onError();
            view.cancelProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<FriendsInvitationResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onSendInvitation(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
