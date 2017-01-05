package xyz.homapay.hampay.mobile.android.img;

import android.content.Context;

import java.io.IOException;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.DecryptedResponseInfo;
import xyz.homapay.hampay.mobile.android.security.KeyExchange;

/**
 * Created by mohammad on 12/30/16.
 */

public class JsonEncryptor {

    public String encrypt(Context ctx, String jsonBody) throws EncryptionException, IOException {
        KeyExchange keyExchange = new KeyExchange(ctx);
        AESMessageEncryptor messageEncryptor = new AESMessageEncryptor();
        if (keyExchange.getKey() == null || keyExchange.getIv() == null) {
            keyExchange.exchange();
            return messageEncryptor.encryptRequest(jsonBody, keyExchange.getKey(), keyExchange.getIv(), keyExchange.getEncId());
        } else {
            return messageEncryptor.encryptRequest(jsonBody, keyExchange.getKey(), keyExchange.getIv(), keyExchange.getEncId());
        }
    }

    public String decrypt(Context ctx, String response) throws Exception {
        AESMessageEncryptor messageEncryptor = new AESMessageEncryptor();
        KeyExchange keyExchange = new KeyExchange(ctx);
        DecryptedResponseInfo decryptedResponseInfo = messageEncryptor.decryptResponse(response, keyExchange.getKey(), keyExchange.getIv());
        if (decryptedResponseInfo.getResponseCode() == 0) {
            return decryptedResponseInfo.getPayload();
        } else {
            return "";
        }
    }

}
