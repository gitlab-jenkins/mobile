package xyz.homapay.hampay.mobile.android.util;

/**
 * Created by amir on 4/9/16.
 */
public class CardNumberValidator {

    public CardNumberValidator(){

    }

    public boolean validate(String cardNumber){
        if (cardNumber.length() < 16) return false;
        int sum = 0;
        boolean odd = true;

        cardNumber = cardNumber.replaceAll("-", "");

        //Add tejarat bank card number exception
        if (cardNumber.startsWith(Constants.TEJERAT_CARD_PREFIX_1) || cardNumber.startsWith(Constants.TEJERAT_CARD_PREFIX_2)){
            return true;
        }

        for (char ch : cardNumber.toCharArray()) {
            int digit;

            try {
                digit = Integer.parseInt(String.valueOf(ch));
            } catch (NumberFormatException e) {
                return false;
            }

            sum += (odd ? (digit * 2 > 9 ? digit * 2 - 9 : digit * 2) : digit);
            odd = !odd;
        }

        if ((sum % 10) == 0) return true;
        return false;
    }

}
