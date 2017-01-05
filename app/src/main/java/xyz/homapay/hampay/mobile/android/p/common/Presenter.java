package xyz.homapay.hampay.mobile.android.p.common;

import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;

/**
 * Created by mohammad on 1/5/17.
 */

public abstract class Presenter<T> {

    protected T view;
    protected ModelLayer modelLayer;

    public Presenter(ModelLayer modelLayer, T view) {
        this.view = view;
        this.modelLayer = modelLayer;
    }
}
