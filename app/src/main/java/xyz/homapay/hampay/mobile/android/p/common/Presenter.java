package xyz.homapay.hampay.mobile.android.p.common;

import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangeView;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;

/**
 * Created by mohammad on 1/5/17.
 */

public abstract class Presenter<T> implements KeyExchangeView {

    private static byte[] key;
    private static byte[] iv;
    private static String encId;
    protected T view;
    protected ModelLayer modelLayer;
    private KeyAgreementModel keyAgreementModel;
    private KeyExchangerImpl keyExchanger;

    public Presenter(ModelLayer modelLayer, T view) {
        this.view = view;
        this.modelLayer = modelLayer;
    }

    protected void keyExchange() {
        if (modelLayer.canUseStaticKeys() && key != null && iv != null && encId != null && keyAgreementModel != null) {
            onKeyExchangeDone();
        } else {
            keyExchanger = new KeyExchangerImpl(modelLayer, this);
            keyExchanger.exchange();
        }
    }

    @Override
    public void onExchangeDone(boolean state, KeyAgreementModel data, String message) {
        try {
            if (state) {
                setKey(data.getKey());
                setIv(data.getIv());
                setEncId(data.getEncId());
                setKeyAgreementModel(data);
                modelLayer.requestUseStaticKeys();
                onKeyExchangeDone();
            } else {
                onKeyExchangeError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            onKeyExchangeError();
        }
    }

    public abstract void onKeyExchangeDone();

    public abstract void onKeyExchangeError();

    public byte[] getKey() {
        return key;
    }

    public static void setKey(byte[] key) {
        Presenter.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public static void setIv(byte[] iv) {
        Presenter.iv = iv;
    }

    public String getEncId() {
        return encId;
    }

    public static void setEncId(String encId) {
        Presenter.encId = encId;
    }

    public KeyAgreementModel getKeyAgreementModel() {
        return keyAgreementModel;
    }

    public void setKeyAgreementModel(KeyAgreementModel keyAgreementModel) {
        this.keyAgreementModel = keyAgreementModel;
    }
}
