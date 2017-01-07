package xyz.homapay.hampay.mobile.android.m.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;

/**
 * Created by mohammad on 1/5/17.
 */

public interface KeyExchangeService {

    @POST("security/agree-key")
    Call<ResponseMessage<KeyAgreementResponse>> exchange(@Body RequestMessage<KeyAgreementRequest> body);

}
