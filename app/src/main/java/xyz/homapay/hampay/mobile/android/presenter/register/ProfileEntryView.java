package xyz.homapay.hampay.mobile.android.presenter.register;

/**
 * Created by mohammad on 12/31/16.
 */

public interface ProfileEntryView {

    boolean phoneNumberCheck();

    boolean nationalCodeCheck();

    boolean cardNumberCheck();

    boolean nameCheck();

    boolean emailCheck();

    void showError(String message);


}
