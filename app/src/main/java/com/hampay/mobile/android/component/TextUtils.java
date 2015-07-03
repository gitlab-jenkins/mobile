package com.hampay.mobile.android.component;

import android.content.Context;
import android.graphics.Typeface;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.util.Constants;


public class TextUtils {

    private static TextUtils instance;
    private static Typeface defaultTypeface;
    private static Typeface numericTypeface;
    private static Typeface passwordTypeface;

    private TextUtils() {
    }

    public static TextUtils getInstance(Context context) {
        if (instance == null)
            instance = new TextUtils();
        return instance;
    }

    public static String formatAmount(String amountString) {
        StringBuilder builder = new StringBuilder();
        if( amountString.length() <= 3 ) {
            builder.append( amountString );
        } else {
            int mod = amountString.length()%3;
            builder.append(amountString.substring(0,mod));
            if( mod != 0 )
                builder.append(",");
            for( int i=0 ; i<amountString.length()/3 ; i++ ) {
                builder.append(amountString.substring((i*3) + mod , ((i+1)*3) + mod));
                if( i < (amountString.length() / 3 - 1 ) )
                    builder.append(",");
            }
        }

        builder.append(" ");
        return builder.toString();
    }

    public static String formatAmount(Context context, String amountString, boolean hasCurrency) {
        String result = formatAmount(amountString);
        if( result != null ) {
            result += " ";
            result += context.getResources().getString(R.string.currency_rials);
        }

        return result;
    }

    public static String formatAmount(Context context, long amount) {
        String amountString = String.valueOf(amount);
        return formatAmount(amountString);
    }

    public static String formatAmount(Context context, long amount, boolean hasCurrency) {
        if( !hasCurrency ) {
            String amountString = String.valueOf(amount);
            return formatAmount(amountString);
        } else {
            String amountString = String.valueOf(amount);
            String result = formatAmount(amountString);
            if( result != null ) {
                result += " ";
                result += context.getResources().getString(R.string.currency_rials);
            }

            return result;
        }

    }

//    public static String formatProductName(String productName, Context context) {
//        String [] assetTitleParts = context.getResources().getStringArray(R.array.asset_title_parts_array);
//        if( assetTitleParts == null || assetTitleParts.length == 0 )
//            return productName;
//        for( String part : assetTitleParts ) {
//            productName = productName.replaceFirst(part, "");
//        }
//
//        return productName;
//    }

    public static boolean hasDigit(String text){
        if (text == null || text.length() ==0)
            return false;
        int length = text.length();
        for (int i=0;i<length; i++){
            if (Character.isDigit(text.charAt(i)))
                return true;
        }
        return false;
    }

    public static Typeface getDefaultTypeface(Context context) {
        if( defaultTypeface == null )
            defaultTypeface = Typeface.createFromAsset(context.getAssets(), Constants.DEFAULT_TYPEFACE_NAME);
        return defaultTypeface;
    }

    public static Typeface getNumericTypeface(Context context) {
        if( numericTypeface == null )
            numericTypeface = Typeface.createFromAsset(context.getAssets(), Constants.NUMERIC_TYPEFACE_NAME);
        return numericTypeface;
    }

    public static Typeface getPasswordTypeface(Context context) {
        if( passwordTypeface == null )
            passwordTypeface = Typeface.createFromAsset(context.getAssets(), Constants.PASSWORD_TYPEFACE_NAME);
        return passwordTypeface;
    }
}
