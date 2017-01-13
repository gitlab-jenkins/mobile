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
            onKeyExchangeDone(state, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            onKeyExchangeError();
        }
    }

    public abstract void onKeyExchangeDone(boolean state, KeyAgreementModel data, String message);

    public abstract void onKeyExchangeError();
}
