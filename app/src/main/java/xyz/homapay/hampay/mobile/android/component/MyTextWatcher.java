package xyz.homapay.hampay.mobile.android.component;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public class MyTextWatcher implements TextWatcher {

    private OnChangedListener listener;

    public MyTextWatcher(OnChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try {
            if (listener != null && editable != null)
                listener.onChanged(editable.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnChangedListener {
        void onChanged(String text);
    }
}
