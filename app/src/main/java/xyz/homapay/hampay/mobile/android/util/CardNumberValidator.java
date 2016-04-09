package xyz.homapay.hampay.mobile.android.util;

/**
 * Created by amir on 4/9/16.
 */
public class CardNumberValidator {

    public CardNumberValidator(){

    }

    public boolean validate(String cardNumber){
        if(cardNumber.length() != 16) return false;
        int sum = 0;
        boolean odd = true;
        for (char ch : cardNumber.toCharArray())
        { int digit = Integer.parseInt(String.valueOf(ch)); sum = odd ? (digit * 2 > 9 ? digit * 2 - 9 : digit * 2 ) : digit ; }
        return (sum % 10) == 0;
    }

}
