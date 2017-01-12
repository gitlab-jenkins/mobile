package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;

/**
 * Created by mohammad on 1/10/17.
 */

public interface TopUpService {

    @POST("/hampay/topup/info")
    Call<ResponseMessage<TopUpInfoResponse>> topUpInfo(@Body RequestBody body);

    @POST("/hampay/topup/info")
    Call<ResponseMessage<TopUpResponse>> topUpCreate(@Body RequestBody body);
}
