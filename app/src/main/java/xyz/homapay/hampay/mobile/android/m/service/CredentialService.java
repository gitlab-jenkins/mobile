package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;

/**
 * Created by amir on 1/23/17.
 */

public interface CredentialService {

    @POST("users/reg-credential-entry")
    Call<ResponseMessage<RegistrationCredentialsResponse>> credential(@Body RequestBody body);

}
