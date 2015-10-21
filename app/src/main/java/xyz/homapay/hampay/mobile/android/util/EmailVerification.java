package xyz.homapay.hampay.mobile.android.util;

import android.text.TextUtils;

/**
 * Created by amir on 10/20/15.
 */
public class EmailVerification {

    private String email;

    public EmailVerification(){
    }

    public EmailVerification(String email){
        this.email = email;
    }

    public boolean isValid(String email){

        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

}
