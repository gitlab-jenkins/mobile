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
    private static KeyAgreementModel keyAgreementModel;
    protected T view;
    protected ModelLayer modelLayer;
    private KeyExchangerImpl keyExchanger;

    public Presenter(ModelLayer modelLayer, T view) {
        this.view = view;
        this.modelLayer = modelLayer;
    }

    public static final void invalidateKeys() {
        key = null;
        iv = null;
        encId = null;
        keyAgreementModel = null;
    }

    protected void keyExchange() {
        if (key != null && iv != null && encId != null && keyAgreementModel != null) {
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
                key = data.getKey();
                iv = data.getIv();
                encId = data.getEncId();
                keyAgreementModel = data;
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

    public byte[] getIv() {
        return iv;
    }

    public String getEncId() {
        return encId;
    }

    public KeyAgreementModel getKeyAgreementModel() {
        return keyAgreementModel;
    }
}
