package com.hampay.mobile.android.component.edittext;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


/**
 * Created by amir on 7/7/15.
 */
public class CurrencyFormatter implements TextWatcher{

    private EditText editText;

    private String current = "";

    public CurrencyFormatter(EditText editText)
    {
        this.editText = editText;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if(!s.toString().equals(current)){
            editText.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

            current = formatted;
            editText.setText(formatted);
            editText.setSelection(formatted.length());

            editText.addTextChangedListener(this);
        }
    }

}
