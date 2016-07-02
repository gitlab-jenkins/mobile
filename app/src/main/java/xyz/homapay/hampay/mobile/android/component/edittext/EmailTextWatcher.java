package xyz.homapay.hampay.mobile.android.component.edittext;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;

/**
 * Created by amir on 7/2/15.
 */
public class EmailTextWatcher implements TextWatcher {

    FacedEditText emailValue;
    ImageView emailIcon;
    private boolean isValidEmail = true;


    public EmailTextWatcher(FacedEditText emailValue, ImageView emailIcon)
    {
        this.emailValue = emailValue;
        this.emailIcon = emailIcon;
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
    public void onTextChanged(CharSequence charSequence, int start, int before, int count)
    {
        emailValue.removeTextChangedListener(this);
        emailIcon.setVisibility(View.VISIBLE);

        if (new EmailVerification().isValid(charSequence.toString().trim())){
            emailIcon.setImageResource(R.drawable.right_icon);
            isValidEmail = true;
        }else {
            emailIcon.setImageResource(R.drawable.false_icon);
            isValidEmail = false;
        }

        if (charSequence.toString().length() == 0){
            emailIcon.setVisibility(View.INVISIBLE);
            isValidEmail = true;
        }

        emailValue.addTextChangedListener(this);

    }

    public boolean isValid(){
        return isValidEmail;
    }

}