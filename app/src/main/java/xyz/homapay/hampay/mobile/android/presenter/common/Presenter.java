package xyz.homapay.hampay.mobile.android.presenter.common;

/**
 * Created by mohammad on 12/31/16.
 */

public abstract class Presenter<T> {

    protected T view;

    public Presenter(T view) {
        this.view = view;
    }
}
