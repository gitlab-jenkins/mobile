package com.hampay.mobile.android.util;

/**
 * Created by amir on 7/8/15.
 */
public class NationalCodeVerification {

    String nationalCode;
    int[] nationalCodeIntArray;
    long nationalCodeCumulative;
    long nationalCodeMod;
    String[] exeptionsNationalCode = {
            "0000000000",
//            "1111111111",
            "2222222222",
            "3333333333",
            "4444444444",
            "5555555555",
            "6666666666",
            "7777777777",
            "8888888888",
            "9999999999"};

    public NationalCodeVerification(String nationalCode){
        this.nationalCode = nationalCode;
    }

    public boolean isValidCode(){

        if (nationalCode == null){
            return false;
        }
        if (nationalCode.length() < 10){
            return false;
        }


        for (String exeptionNationalCode : exeptionsNationalCode){
            if (nationalCode.equalsIgnoreCase(exeptionNationalCode)){
                return false;
            }
        }

        nationalCodeIntArray = new int[10];
        int nationalCodeLength = nationalCode.length();
        if(nationalCodeLength != 10) {
            return false;
        } else {
            for(int i = 0; i < 10; i++) {
                if (!Character.isDigit(nationalCode.charAt(i))) {
                    return false;
                }
                nationalCodeIntArray[i] = Integer.parseInt(String.valueOf(nationalCode.charAt(i))) * (10 - i);
            }
        }

        for(int i = 0; i < 9; i++){
            nationalCodeCumulative += nationalCodeIntArray[i];
        }

        nationalCodeMod = nationalCodeCumulative % 11;

        if(((nationalCodeMod < 2) && (nationalCodeIntArray[9] == nationalCodeMod)) || ((nationalCodeMod >= 2) && ((11 - nationalCodeMod) == nationalCodeIntArray[9]))){
            return true;
        }
        else {
            return false;
        }
    }

}
