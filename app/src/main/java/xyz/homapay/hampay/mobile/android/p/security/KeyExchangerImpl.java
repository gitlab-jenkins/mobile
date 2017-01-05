package xyz.homapay.hampay.mobile.android.p.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.DiffieHellmanKeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.PublicKeyPair;
import xyz.homapay.hampay.common.common.encrypt.SecretKeyPair;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
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
    private String encryptionId;
    private SecretKeyPair secretKeyPair;
    private PublicKey peerEncPublicKey;
    private PublicKey peerIvPublicKey;

    public KeyExchangerImpl(ModelLayer modelLayer, KeyExchangeView view) {
        super(modelLayer, view);
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
    public byte[] getKey() {
        if (secretKeyPair != null) {
            return secretKeyPair.getEncSecretKey().getEncoded();
        } else {
            return null;
        }
    }

    @Override
    public byte[] getIv() {
        if (secretKeyPair != null) {
            return secretKeyPair.getIvSecretKey().getEncoded();
        } else {
            return null;
        }
    }

    @Override
    public String getEncId() {
        if (encryptionId != null) {
            return encryptionId;
        } else {
            return "";
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<KeyAgreementResponse> data, String message) {
        try {
            if (status) {
                encryptionId = data.getService().getId();
                if (data != null) {
                    if (data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                        try {
                            try {
                                peerEncPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(data.getService().getKeyData()));
                                peerIvPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(data.getService().getIvData()));
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            try {
                                secretKeyPair = keyExchanger.generateAndGetSecretKey(new PublicKeyPair(peerEncPublicKey, peerIvPublicKey));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            view.onExchangeDone(status, status ? data : null, status ? message : "Failed to get request");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
