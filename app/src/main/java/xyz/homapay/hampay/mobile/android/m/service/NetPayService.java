package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.NetPayResponse;

/**
 * Created by amir on 2/4/17.
 */

public interface NetPayService {

    @POST("psp-proxy/netpay")
    Call<ResponseMessage<NetPayResponse>> netPay(@Body RequestBody body);

}
