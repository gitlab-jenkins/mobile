package xyz.homapay.hampay.mobile.android.util;

import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 5/23/16.
 */
public class PasswordComplexity {

    String password;

    public PasswordComplexity(String password){
        this.password = password;
    }

    public int check(){

        if (password.equalsIgnoreCase("12345") || password.equalsIgnoreCase("54321")){
            return R.string.msg_password_complexity;
        }
        Map<String, Integer> passCodeMap = new HashMap<>();
        for(int i = 0; i < password.length(); i++) {
            if (passCodeMap.get(String.valueOf(password.charAt(i))) == null){
                passCodeMap.put(String.valueOf(password.charAt(i)), 1);
            }else {
                passCodeMap.put(String.valueOf(password.charAt(i)), passCodeMap.get(String.valueOf(password.charAt(i))) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : passCodeMap.entrySet())
        {
            if (entry.getValue() > 2){
                return R.string.msg_invalid_password;
            }
        }

        return 1;
    }

}
