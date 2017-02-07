package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.response.BillResponse;

/**
 * Created by amir on 2/4/17.
 */

public interface BillsPayService {

    @POST("psp-proxy/bill")
    Call<ResponseMessage<BillResponse>> billsPay(@Body RequestBody body);

}
