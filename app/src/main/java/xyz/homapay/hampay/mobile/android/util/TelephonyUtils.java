package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.Operator;

public class TelephonyUtils {

    public static String MCI_OPERATOR = "43211";

    // Telecommunication Kish
    public static String TKC_OPERATOR = "43214";
    // Mobile Telecommunications Company of Esfahan
    public static String MTCE_OPERATOR = "43219";
    public static String RIGHTEL_OPERATOR = "43220";
    public static String TALIYA_OPERATOR = "43232";
    public static String IRANCELL_OPERATOR = "43235";
    // Telecommunication Company of Iran
    public static String TCI_OPERATOR = "43270";
    static IranMobileOperators simOperator = null;

    private List<String> lstMCI;
    private List<String> lstMTN;
    private List<String> lstRIGHTEL;

    public TelephonyUtils() {
        lstMCI = new ArrayList<>();
        lstMTN = new ArrayList<>();
        lstRIGHTEL = new ArrayList<>();
        lstMCI.add("10");
        lstMCI.add("11");
        lstMCI.add("13");
        lstMCI.add("14");
        lstMCI.add("15");
        lstMCI.add("16");
        lstMCI.add("17");
        lstMCI.add("18");
        lstMCI.add("19");

        lstMTN.add("01");
        lstMTN.add("02");
        lstMTN.add("03");
        lstMTN.add("30");
        lstMTN.add("33");
        lstMTN.add("35");
        lstMTN.add("36");
        lstMTN.add("37");
        lstMTN.add("38");
        lstMTN.add("39");

        lstRIGHTEL.add("21");
    }

    /**
     * Get operator type such as inrancell , hamrahaval , rightel and talia
     *
     * @param _ctx Context of application
     * @return
     */
    public static IranMobileOperators getSimOperator(Context _ctx) {
        if (simOperator == null) {
            TelephonyManager manager = (TelephonyManager) _ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (checkSimExistence(_ctx)) {
                String strSimOperator = manager.getSimOperator();
                if (strSimOperator.equals(MCI_OPERATOR))
                    simOperator = IranMobileOperators.MCI;
                else if (strSimOperator.equals(TKC_OPERATOR))
                    simOperator = IranMobileOperators.TKC;
                else if (strSimOperator.equals(MTCE_OPERATOR))
                    simOperator = IranMobileOperators.MTCE;
                else if (strSimOperator.equals(RIGHTEL_OPERATOR))
                    simOperator = IranMobileOperators.RIGHTEL;
                else if (strSimOperator.equals(TALIYA_OPERATOR))
                    simOperator = IranMobileOperators.TALIYA;
                else if (strSimOperator.equals(IRANCELL_OPERATOR))
                    simOperator = IranMobileOperators.IRANCELL;
                else if (strSimOperator.equals(TCI_OPERATOR))
                    simOperator = IranMobileOperators.TCI;
                else {
                    simOperator = IranMobileOperators.UNKNOWN;
                }
            } else {
                simOperator = null;
            }
        }
        return simOperator;
    }

    public static String getSimPureOperatorCode(Context _ctx) {
        String strSimOperator = "";
        TelephonyManager manager = (TelephonyManager) _ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSimExistence(manager)) {
            strSimOperator = manager.getSimOperator();
        } else {
            strSimOperator = null;
        }
        return strSimOperator;
    }

    /**
     * Checking sim existence
     *
     * @param _ctx Context of application
     * @return
     */
    public static boolean checkSimExistence(Context _ctx) {
        TelephonyManager manager = (TelephonyManager) _ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    private static boolean checkSimExistence(TelephonyManager manager) {
        return manager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    public static String getSimSerial(final Context ctx) {
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * Check if this number is a iran region valid number or not
     *
     * @param number Input number
     * @return
     */
    public static boolean isIranValidNumber(String number) {

        boolean isOK = false;

        try {
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber num = util.parse(number, "IR");
            isOK = util.isValidNumber(num);
        } catch (NumberParseException e) {
            e.printStackTrace();
        } finally {
            return isOK;
        }
    }

    /**
     * Fixing
     *
     * @param rawNumber
     * @return
     */
    public static String fixPhoneNumber(Context ctx, String rawNumber) {
        String fixedNumber = "";

        // get current location iso code
        TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String curLocale = telMgr.getNetworkCountryIso().toUpperCase();
        if (curLocale == null || curLocale.equals(""))
            curLocale = "IR";

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumberProto;

        // gets the international dialling code for our current location
        String curDCode = String.format("%d", phoneUtil.getCountryCodeForRegion(curLocale));
        String ourDCode = "";

        if (rawNumber.indexOf("+") == 0) {
            int bIndex = rawNumber.indexOf("(");
            int hIndex = rawNumber.indexOf("-");
            int eIndex = rawNumber.indexOf(" ");

            if (bIndex != -1) {
                ourDCode = rawNumber.substring(1, bIndex);
            } else if (hIndex != -1) {
                ourDCode = rawNumber.substring(1, hIndex);
            } else if (eIndex != -1) {
                ourDCode = rawNumber.substring(1, eIndex);
            } else {
                ourDCode = curDCode;
            }
        } else {
            ourDCode = curDCode;
        }

        try {
            phoneNumberProto = phoneUtil.parse(rawNumber, curLocale);
        } catch (NumberParseException e) {
            return rawNumber;
        }

        if (curDCode.compareTo(ourDCode) == 0)
            fixedNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        else
            fixedNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

        return fixedNumber.replace(" ", "");
    }

    public Operator getNumberOperator(String cellPhone) {
        PersianEnglishDigit p2e = new PersianEnglishDigit();
        cellPhone = p2e.P2E(cellPhone).substring(2, 4);
        if (lstMCI.contains(cellPhone))
            return Operator.MCI;
        else if (lstMTN.contains(cellPhone))
            return Operator.MTN;
        else if (lstRIGHTEL.contains(RIGHTEL_OPERATOR))
            return Operator.RAYTEL;
        else
            return Operator.MCI;
    }

    public enum IranMobileOperators {
        MCI, TKC, MTCE, TALIYA, IRANCELL, TCI, RIGHTEL, type, UNKNOWN
    }

}
