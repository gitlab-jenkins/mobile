package xyz.homapay.hampay.mobile.android.p.netpay;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.Service;
import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.request.NetPayRequest;
import xyz.homapay.hampay.common.pspproxy.model.response.NetPayResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.common.ServiceType;
import xyz.homapay.hampay.mobile.android.m.worker.netpay.NetPayNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 2/4/17.
 */

public class NetPayImpl extends Presenter<NetPayView> implements NetPay, OnNetworkLoadListener<ResponseMessage<NetPayResponse>> {


    private MessageEncryptor messageEncryptor;
    private NetPayRequest netPayRequest;
    private String authToken;
    private byte[] encKey;
    private byte[] ivKey;

    public NetPayImpl(ModelLayer modelLayer, NetPayView view){
        super(modelLayer, view);
    }

    private void onError() {
        try {
            view.cancelProgress();
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<NetPayResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onNetPayResponse(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void netPay(NetPayRequest netPayRequest, String authToken, byte[] encKey, byte[] ivKey) {
        try {
            view.showProgress();
            messageEncryptor = new AESMessageEncryptor();
            this.netPayRequest = netPayRequest;
            this.authToken = authToken;
            this.encKey = encKey;
            this.ivKey = ivKey;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {

        try {
            netPayRequest.setRequestUUID(UUID.randomUUID().toString());
            RequestMessage<NetPayRequest> message = new RequestMessage<>(netPayRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = messageEncryptor.encryptRequest(new Gson().toJson(message), encKey, ivKey, getEncId());
            new NetPayNetWorker(modelLayer, encKey, ivKey).netPay(strJson, this);
        }catch (Exception e) {
            view.keyExchangeProblem();
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        onError();
    }
}
