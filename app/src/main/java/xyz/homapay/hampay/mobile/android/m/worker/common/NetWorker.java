package xyz.homapay.hampay.mobile.android.m.worker.common;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.MyCallBack;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.common.TrustedOkHttpClient;

/**
 * Created by mohammad on 1/5/17.
 */

public class NetWorker<T> {

    protected T service;
    protected ModelLayer modelLayer;
    protected Retrofit retrofit;

    public NetWorker(final ModelLayer modelLayer, final Class<T> tClass, boolean encryption, boolean gZip) {
        this.modelLayer = modelLayer;
        retrofit = new Retrofit.Builder()
                .baseUrl(modelLayer.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(TrustedOkHttpClient.getTrustedOkHttpClient(modelLayer, encryption, gZip))
                .build();
        service = retrofit.create(tClass);
    }

    protected void execute(Call call, OnNetworkLoadListener listener) {
        call.enqueue(new MyCallBack(modelLayer, listener));
    }
}