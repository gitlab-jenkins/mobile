package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;

/**
 * Created by mohammad on 1/7/17.
 */

public interface AuthService {

    @POST("users/reg-entry")
    Call<ResponseMessage<RegistrationEntryResponse>> register(@Body RequestBody body);

    @POST("users/reg-sms-token")
    Call<ResponseMessage<RegistrationSendSmsTokenResponse>> sendSMS(@Body RequestBody body);

}
