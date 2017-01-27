package xyz.homapay.hampay.mobile.android.p.security;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.DiffieHellmanKeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.PublicKeyPair;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.security.KeyNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/5/17.
 */

public class KeyExchangerImpl extends Presenter<KeyExchangeView> implements KeyExchanger, OnNetworkLoadListener<ResponseMessage<KeyAgreementResponse>> {

    private DiffieHellmanKeyExchanger keyExchanger;
    private KeyAgreementRequest keyAgreementRequest;
    private RequestMessage message;

    public KeyExchangerImpl(ModelLayer modelLayer, KeyExchangeView view) {
        super(modelLayer, view);
    }

    @Override
    public void onKeyExchangeDone() {

    }

    @Override
    public void onKeyExchangeError() {

    }

    @Override
    public void exchange() {
        try {
            keyExchanger = new DiffieHellmanKeyExchanger();
            PublicKeyPair publicKeyPair = keyExchanger.getPublicKey();
            keyAgreementRequest = new KeyAgreementRequest();
            keyAgreementRequest.setRequestUUID(UUID.randomUUID().toString());
            keyAgreementRequest.setKeyData(publicKeyPair.getEncPublicKey().getEncoded());
            keyAgreementRequest.setIvData(publicKeyPair.getIvPublicKey().getEncoded());
            message = new RequestMessage<>(keyAgreementRequest, "", Constants.API_LEVEL, System.currentTimeMillis());
            new KeyNetWorker(modelLayer).exchange(message, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<KeyAgreementResponse> data, String message) {
        try {
            KeyAgreementModel keyAgreementModel = new KeyAgreementModel(keyExchanger, data);
            view.onExchangeDone(status, status ? keyAgreementModel : null, status ? message : "Failed to get request");
        } catch (Exception e) {
            e.printStackTrace();
            view.onExchangeDone(false, null, "Failed to get request");
        }
    }
}
