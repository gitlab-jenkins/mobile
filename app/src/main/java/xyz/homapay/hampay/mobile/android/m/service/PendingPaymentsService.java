package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;

/**
 * Created by mohammad on 1/22/17.
 */

public interface PendingPaymentsService {

    @POST("fund/pending-list")
    Call<ResponseMessage<PendingFundListResponse>> pendingList(@Body RequestBody body);

    @POST("fund/cancel")
    Call<ResponseMessage<CancelFundResponse>> cancel(@Body RequestBody body);

    @POST("payment/pending-po-list")
    Call<ResponseMessage<PendingPOListResponse>> getPendingPOList(@Body RequestBody body);
}
