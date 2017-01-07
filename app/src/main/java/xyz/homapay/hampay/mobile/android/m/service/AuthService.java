package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;

/**
 * Created by mohammad on 1/7/17.
 */

public interface AuthService {

    @POST("/hampay/users/reg-entry")
    Call<ResponseMessage<RegistrationEntryResponse>> register(@Body RequestBody body);

}
