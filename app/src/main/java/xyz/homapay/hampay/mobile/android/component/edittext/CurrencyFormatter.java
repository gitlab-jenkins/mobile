package xyz.homapay.hampay.mobile.android.component.edittext;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 7/7/15.
 */
public class CurrencyFormatter implements TextWatcher{

    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText editText;

    public CurrencyFormatter(EditText editText)
    {
        df = new DecimalFormat("#,###.##");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###");
        this.editText = editText;
        hasFractionalPart = false;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {
        editText.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = editText.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = editText.getSelectionStart();
            if (hasFractionalPart) {
                editText.setText(new PersianEnglishDigit().E2P(df.format(n)));
            } else {
                editText.setText(new PersianEnglishDigit().E2P(dfnd.format(n)));
            }
            endlen = editText.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= editText.getText().length()) {
                editText.setSelection(sel);
            } else {
                // place cursor at the end?
                editText.setSelection(editText.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

//        editText.setText(new PersianEnglishDigit().E2P(editText.getText().toString()));
//        editText.setSelection(s.toString().length());
//
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

//        this.editText.removeTextChangedListener(this);
//        this.editText.setText(new PersianEnglishDigit().E2P(s.toString()));
//        this.editText.setSelection(s.toString().length());
//        this.editText.addTextChangedListener(this);
        
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
            hasFractionalPart = true;
        } else {
            hasFractionalPart = false;
        }
    }

}
