package xyz.homapay.hampay.mobile.android.util;

/**
 * Created by amir on 1/9/17.
 */

public class BillsValidator {

    public boolean validateBillId(String billId) {
        try {
            if (billId.length() < 6 || billId.length() > 13)
                return false;
            String checkDigit = billId.substring(billId.length() - 1, billId.length());
            char[] billIdChars = billId.substring(0, billId.length() - 1).toCharArray();
            return verifyUtilityBillCheckDigit(checkDigit, billIdChars);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validatePayId(String payId) {
        try {
            if (payId.length() < 6 || payId.length() > 13)
                return false;
            String checkDigit = payId.substring(payId.length() - 2, payId.length() - 1);
            char[] payIdChars = payId.substring(0, payId.length() - 2).toCharArray();
            return verifyUtilityBillCheckDigit(checkDigit, payIdChars);
        } catch (Exception e) {
            return false;
        }
    }


    public boolean validatePayAndBillId(String billId, String payId) {
        try {
            String checkDigit = payId.substring(payId.length() - 1, payId.length());
            if (payId.startsWith("0"))
                payId = payId.substring(1, payId.length());
            if (billId.startsWith("0"))
                billId = billId.substring(1, billId.length());
            char[] utilityBillsChars = (billId + payId.substring(0, payId.length() - 1)).toCharArray();
            return verifyUtilityBillCheckDigit(checkDigit, utilityBillsChars);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean verifyUtilityBillCheckDigit(String checkDigit, char[] chars) {
        int multiply = 2;
        int result = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            int billIdChar = Character.getNumericValue(chars[i]);
            result = result + billIdChar * multiply;
            if (multiply < 7)
                ++multiply;
            else multiply = 2;
        }
        int modResult = result % 11;
        if (modResult == 0 || modResult == 1)
            modResult = 0;
        return modResult != 0 ? 11 - modResult == Integer.valueOf(checkDigit) : 0 == Integer.valueOf(checkDigit);
    }

}
