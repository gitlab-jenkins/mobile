package xyz.homapay.hampay.mobile.android.m.common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mohammad on 5/21/16.
 */
public class MyCallBack<T> implements Callback<T> {

    private OnNetworkLoadListener<T> listener;
    private ModelLayer modelLayer;

    public MyCallBack(final ModelLayer helper, final OnNetworkLoadListener<T> listener) {
        this.listener = listener;
        this.modelLayer = helper;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        try {
            if (response.isSuccessful())
                listener.onNetworkLoad(true, response.body(), response.message());
            else
                listener.onNetworkLoad(false, null, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        try {
            if (t != null) {
                if (t instanceof NoNetworkException) {
                    modelLayer.showNoNetworkDialog();
                } else {
                    modelLayer.showServerConnectionError();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (t != null)
                listener.onNetworkLoad(false, null, t.getMessage());
            else
                listener.onNetworkLoad(false, null, "");
        }
    }
}
