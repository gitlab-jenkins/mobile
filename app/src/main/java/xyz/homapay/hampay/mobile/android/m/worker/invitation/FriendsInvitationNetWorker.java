package xyz.homapay.hampay.mobile.android.m.worker.invitation;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.FriendsInvitationResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.service.InvitationService;
import xyz.homapay.hampay.mobile.android.m.worker.common.NetWorker;

/**
 * Created by mohammad on 1/31/17.
 */

public class FriendsInvitationNetWorker extends NetWorker<InvitationService> {

    public FriendsInvitationNetWorker(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        super(modelLayer, InvitationService.class, keyAgreementModel, encryption, gZip);
    }

    public void sendInvitation(String body, OnNetworkLoadListener<ResponseMessage<FriendsInvitationResponse>> listener) {
        execute(service.sendInvitation(getPlainBodyRequest(body)), listener);
    }

}
