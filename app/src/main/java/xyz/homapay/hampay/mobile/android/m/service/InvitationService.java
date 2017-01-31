package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.FriendsInvitationResponse;

/**
 * Created by mohammad on 1/31/17.
 */

public interface InvitationService {

//    @Headers({"Content-Encoding: gzip"})
    @POST("users/invite")
    Call<ResponseMessage<FriendsInvitationResponse>> sendInvitation(@Body RequestBody body);
}
