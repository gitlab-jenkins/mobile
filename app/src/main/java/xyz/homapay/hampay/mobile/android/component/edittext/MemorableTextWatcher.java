package xyz.homapay.hampay.mobile.android.component.edittext;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by amir on 7/2/15.
 */
public class MemorableTextWatcher implements TextWatcher {

    FacedEditText memorable_value;


    public MemorableTextWatcher(FacedEditText memorable_value)
    {
        this.memorable_value = memorable_value;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "MemorableTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {
        memorable_value.removeTextChangedListener(this);

        memorable_value.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        memorable_value.removeTextChangedListener(this);
        if (s.toString().contains(" ")){
            memorable_value.setText(s.toString().replace(" ", ""));
            memorable_value.setSelection(memorable_value.getText().toString().length());
        }
        memorable_value.addTextChangedListener(this);

    }
}