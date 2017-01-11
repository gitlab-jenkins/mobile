package xyz.homapay.hampay.mobile.android.p.topup;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TopUpInfoRequest;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.topup.TopUpNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangeView;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchanger;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/10/17.
 */

public class TopUpInfoImpl extends Presenter<TopUpInfoView> implements TopUpInfo, OnNetworkLoadListener<ResponseMessage<TopUpInfoResponse>>, KeyExchangeView {

    private TopUpInfoRequest topUpInfoRequest;
    private KeyExchanger keyExchanger;
    private Operator operator;

    public TopUpInfoImpl(ModelLayer modelLayer, TopUpInfoView view) {
        super(modelLayer, view);
    }

    @Override
    public void getInfo(Operator operator) {
        try {
            this.operator = operator;
            view.showProgress();
            keyExchanger = new KeyExchangerImpl(modelLayer, this);
            keyExchanger.exchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<TopUpInfoResponse> data, String message) {
        try {
            view.dismissProgress();
            view.onInfoLoaded(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onExchangeDone(boolean state, KeyAgreementModel data, String message) {
        try {
            if (state) {
                if (data.getKey() != null && data.getIv() != null && data.getEncId() != null) {
                    topUpInfoRequest = new TopUpInfoRequest();
                    topUpInfoRequest.setRequestUUID(UUID.randomUUID().toString());
                    topUpInfoRequest.setOperator(operator);
                    RequestMessage<TopUpInfoRequest> request = new RequestMessage<>(topUpInfoRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
                    String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), data.getKey(), data.getIv(), data.getEncId());
                    new TopUpNetWorker(modelLayer, data, true, false).topUpInfo(strJson, this);
                } else {
                    view.onError();
                }
            } else
                view.onError();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
    }
}
