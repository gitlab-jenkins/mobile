package xyz.homapay.hampay.mobile.android.p.common;

import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangeView;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;

/**
 * Created by mohammad on 1/5/17.
 */

public abstract class Presenter<T> implements KeyExchangeView {

    protected T view;
    protected ModelLayer modelLayer;
    protected byte[] key;
    protected byte[] iv;
    protected String encId;
    protected KeyAgreementModel keyAgreementModel;
    private KeyExchangerImpl keyExchanger;

    public Presenter(ModelLayer modelLayer, T view) {
        this.view = view;
        this.modelLayer = modelLayer;
    }

    protected void keyExchange() {
        keyExchanger = new KeyExchangerImpl(modelLayer, this);
        keyExchanger.exchange();
    }

    @Override
    public void onExchangeDone(boolean state, KeyAgreementModel data, String message) {
        try {
            if (state) {
                this.key = data.getKey();
                this.iv = data.getIv();
                this.encId = data.getEncId();
                this.keyAgreementModel = data;
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
