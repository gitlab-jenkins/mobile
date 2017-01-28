package xyz.homapay.hampay.mobile.android.m.service;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public interface BusinesService {

    @POST("businesses")
    Call<ResponseMessage<BusinessListResponse>> businessList(@Body RequestBody body);

    @POST("businesses/search")
    Call<ResponseMessage<BusinessListResponse>> businessSearch(@Body RequestBody body);

}
