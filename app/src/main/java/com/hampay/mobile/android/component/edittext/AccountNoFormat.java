package com.hampay.mobile.android.component.edittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * Created by amir on 7/2/15.
 */
public class AccountNoFormat implements TextWatcher {

    FacedEditText accountNumberValue;
    String accountNumberFormat;

    String rawAccountNumberValue = "";
    int rawAccountNumberValueLength = 0;
    int rawAccountNumberValueLengthOffset = 0;
    String procAccountNumberValue = "";

    public AccountNoFormat(FacedEditText accountNumberValue, String accountNumberFormat)
    {
        this.accountNumberValue = accountNumberValue;
        this.accountNumberFormat = accountNumberFormat;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {
        accountNumberValue.removeTextChangedListener(this);

        Log.e("FORMAT", accountNumberFormat);

        rawAccountNumberValue = s.toString().replace("/", "");
        rawAccountNumberValueLength = rawAccountNumberValue.length();
        rawAccountNumberValueLengthOffset = 0;

        procAccountNumberValue = "";


        if (rawAccountNumberValue.length() > 0) {

            for (int i = 0; i < rawAccountNumberValueLength; i++) {

                if (accountNumberFormat.charAt(i + rawAccountNumberValueLengthOffset) == '/') {
                    procAccountNumberValue += "/" + rawAccountNumberValue.charAt(i);
                    rawAccountNumberValueLengthOffset++;
                } else {
                    procAccountNumberValue += rawAccountNumberValue.charAt(i);
                }

            }

            accountNumberValue.setText(procAccountNumberValue);
            accountNumberValue.setSelection(accountNumberValue.getText().toString().length());
        }

        accountNumberValue.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        accountNumberValue.removeTextChangedListener(this);
        accountNumberValue.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }
}