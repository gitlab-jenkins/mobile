package xyz.homapay.hampay.mobile.android.m.common;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import xyz.homapay.hampay.common.common.encrypt.KeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.PublicKeyPair;
import xyz.homapay.hampay.common.common.encrypt.SecretKeyPair;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;

/**
 * Created by mohammad on 1/11/17.
 */

public class KeyAgreementModel {

    private SecretKeyPair secretKeyPair;
    private PublicKey peerIvPublicKey;
    private PublicKey peerEncPublicKey;
    private byte[] key;
    private byte[] iv;
    private String encId;

    public KeyAgreementModel(KeyExchanger keyExchanger, ResponseMessage<KeyAgreementResponse> data) {
        encId = data.getService().getId();
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

    public byte[] getKey() {
        return makeKey();
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return makeIV();
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public String getEncId() {
        return encId;
    }

    public void setEncId(String encId) {
        this.encId = encId;
    }

    private byte[] makeKey() {
        if (secretKeyPair != null) {
            return secretKeyPair.getEncSecretKey().getEncoded();
        } else {
            return null;
        }
    }

    private byte[] makeIV() {
        if (secretKeyPair != null) {
            return secretKeyPair.getIvSecretKey().getEncoded();
        } else {
            return null;
        }
    }
}
