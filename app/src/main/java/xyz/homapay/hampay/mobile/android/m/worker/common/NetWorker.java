package xyz.homapay.hampay.mobile.android.m.worker.common;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.MyCallBack;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.common.TrustedOkHttpClient;
import xyz.homapay.hampay.mobile.android.webservice.DateGsonBuilder;

/**
 * Created by mohammad on 1/5/17.
 */

public class NetWorker<T> {

    protected T service;
    protected ModelLayer modelLayer;
    protected Retrofit retrofit;

    public NetWorker(final ModelLayer modelLayer, final Class<T> tClass, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
        this.modelLayer = modelLayer;
        TrustedOkHttpClient trustedOkHttpClient = new TrustedOkHttpClient(ServiceType.OWN);
        retrofit = new Retrofit.Builder()
                .baseUrl(modelLayer.getBaseUrl() + "/")
                .addConverterFactory(GsonConverterFactory.create(new DateGsonBuilder().getDatebuilder().create()))
                .client(trustedOkHttpClient.getTrustedOkHttpClient(modelLayer, keyAgreementModel, encryption, gZip))
                .build();
        service = retrofit.create(tClass);
    }

    public NetWorker(final ModelLayer modelLayer, final Class<T> tClass, byte[] encKey, byte[] ivKey, boolean encryption, boolean gZip, ServiceType serviceType) {
        this.modelLayer = modelLayer;
        TrustedOkHttpClient trustedOkHttpClient = new TrustedOkHttpClient(serviceType);
        retrofit = new Retrofit.Builder()
                .baseUrl(modelLayer.getPspBaseUrl() + "/")
                .addConverterFactory(GsonConverterFactory.create(new DateGsonBuilder().getDatebuilder().create()))
                .client(trustedOkHttpClient.getTrustedOkHttpClient(modelLayer, encKey, ivKey, encryption, gZip))
                .build();
        service = retrofit.create(tClass);
    }

    protected void execute(Call call, OnNetworkLoadListener listener) {
        call.enqueue(new MyCallBack(modelLayer, listener));
    }

    protected RequestBody getPlainBodyRequest(final String body) {
        return RequestBody.create(MediaType.parse("application/plain; charset=utf-8"), body);
    }
}