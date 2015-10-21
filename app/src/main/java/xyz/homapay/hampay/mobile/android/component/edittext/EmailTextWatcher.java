package xyz.homapay.hampay.mobile.android.component.edittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.ImageView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;

/**
 * Created by amir on 7/2/15.
 */
public class EmailTextWatcher implements TextWatcher {

    FacedEditText emailValue;
    ImageView emailIcon;
    CheckBox email_confirm_check;


    public EmailTextWatcher(FacedEditText emailValue, ImageView emailIcon, CheckBox email_confirm_check)
    {
        this.emailValue = emailValue;
        this.emailIcon = emailIcon;
        this.email_confirm_check = email_confirm_check;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "EmailTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {
        emailValue.removeTextChangedListener(this);

        emailValue.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        emailValue.removeTextChangedListener(this);

        if (new EmailVerification().isValid(emailValue.getText().toString())){
            emailIcon.setImageResource(R.drawable.right_icon);
            email_confirm_check.setChecked(true);
        }else {
            emailIcon.setImageResource(R.drawable.false_icon);
            email_confirm_check.setChecked(false);
        }

        emailValue.addTextChangedListener(this);

    }
}