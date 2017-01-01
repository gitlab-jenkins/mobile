package xyz.homapay.hampay.mobile.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Check password contains just alphabet and digits and the length is 5
        Pattern pattern1 = Pattern.compile("^[A-Za-z\\d]{5}$");
        Matcher matcher1 = pattern1.matcher(password);
        boolean result1 = matcher1.matches();
        if (!result1){
            return R.string.hp_msg_password_complexity_1;
        }

//        Check password contains at least 3 alphabets
        Pattern pattern2 = Pattern.compile("(.*?[a-zA-Z].*?){3}");
        Matcher matcher2 = pattern2.matcher(password);
        boolean result2 = matcher2.matches();
        if (!result2){
            return R.string.hp_msg_password_complexity_2;
        }

//        Check password contains at least one letter
        Pattern pattern3 = Pattern.compile("(.*[0-9].*?){1}");
        Matcher matcher3 = pattern3.matcher(password);
        boolean result3 = matcher3.matches();
        if (!result3){
            return R.string.hp_msg_password_complexity_3;
        }

//        Check password does not contain more than two the same characters
        Pattern pattern4 = Pattern.compile("(.)(.*\\1){2}");
        Matcher matcher4 = pattern4.matcher(password);
        boolean result4 = matcher4.find();
        if (result4){
            return R.string.hp_msg_password_complexity_4;
        }

        return 1;
    }

}
