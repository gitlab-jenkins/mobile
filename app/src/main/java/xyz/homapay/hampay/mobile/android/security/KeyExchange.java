package xyz.homapay.hampay.mobile.android.security;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.DiffieHellmanKeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.encrypt.KeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.PublicKeyPair;
import xyz.homapay.hampay.common.common.encrypt.SecretKeyPair;
import xyz.homapay.hampay.common.common.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionMethod;
import xyz.homapay.hampay.mobile.android.webservice.SecuredProxyService;

/**
 * Created by amir on 8/3/16.
 */
public class KeyExchange {

    private static SecretKeyPair secretKeyPair;
    private static String encryptionId;
    private Context context;
    private KeyExchanger keyExchanger;
    private KeyAgreementRequest keyAgreementRequest;
    private static ResponseMessage<KeyAgreementResponse> keyAgreementResponseMessage = null;

    public KeyExchange(Context context) {
        this.context = context;
    }

    public void exchange() throws EncryptionException, IOException {
        keyExchanger = new DiffieHellmanKeyExchanger();
        PublicKeyPair publicKeyPair = keyExchanger.getPublicKey();
        keyAgreementRequest = new KeyAgreementRequest();
        keyAgreementRequest.setRequestUUID(UUID.randomUUID().toString());
        keyAgreementRequest.setKeyData(publicKeyPair.getEncPublicKey().getEncoded());
        keyAgreementRequest.setIvData(publicKeyPair.getIvPublicKey().getEncoded());

        URL url = new URL(Constants.HTTPS_SERVER_IP + "/security/agree-key");
        SecuredProxyService proxyService = new SecuredProxyService(context, Constants.CONNECTION_TYPE, ConnectionMethod.POST, url);
        RequestMessage<KeyAgreementRequest> message = new RequestMessage<>(keyAgreementRequest, "", Constants.API_LEVEL, System.currentTimeMillis());

        Type requestType = new TypeToken<RequestMessage<KeyAgreementRequest>>() {
        }.getType();
        String jsonRequest = new Gson().toJson(message, requestType);

        proxyService.setJsonBody(jsonRequest);

        Gson gson = new Gson();

        keyAgreementResponseMessage = gson.fromJson(proxyService.getResponse(), new TypeToken<ResponseMessage<KeyAgreementResponse>>() {
        }.getType());

        Log.e("ID", keyAgreementResponseMessage.getService().getId());

        if (keyAgreementResponseMessage != null) {
            if (keyAgreementResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                PublicKey peerEncPublicKey = null;
                PublicKey peerIvPublicKey = null;
                try {
                    try {
                        peerEncPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(keyAgreementResponseMessage.getService().getKeyData()));
                        peerIvPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(keyAgreementResponseMessage.getService().getIvData()));
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
            } else {
            }
        } else {
        }

        proxyService.closeConnection();

    }

    public byte[] getKey() {
        if (secretKeyPair != null) {
            return secretKeyPair.getEncSecretKey().getEncoded();
        } else {
            return null;
        }
    }

    public byte[] getIv() {
        if (secretKeyPair != null) {
            return secretKeyPair.getIvSecretKey().getEncoded();
        } else {
            return null;
        }
    }

    public String getEncId() {
        encryptionId = keyAgreementResponseMessage.getService().getId();
        if (encryptionId != null) {
            return encryptionId;
        } else {
            return "";
        }
    }
}
