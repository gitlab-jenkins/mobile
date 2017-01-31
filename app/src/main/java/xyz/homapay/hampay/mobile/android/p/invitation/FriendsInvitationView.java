package xyz.homapay.hampay.mobile.android.p.invitation;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.FriendsInvitationResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/31/17.
 */

public interface FriendsInvitationView extends BaseView {

    void onSendInvitation(boolean state, ResponseMessage<FriendsInvitationResponse> data, String message);

}
