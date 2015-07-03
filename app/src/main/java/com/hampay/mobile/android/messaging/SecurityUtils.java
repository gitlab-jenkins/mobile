package com.hampay.mobile.android.messaging;

import android.content.Context;


public class SecurityUtils {

    private static final String TAG = SecurityUtils.class.getName();
    private static SecurityUtils instance;
    private Context context;

    private SecurityUtils(Context context) {
        this.context = context;
    }

    public static SecurityUtils getInstance(Context context) {
        if( instance == null )
            instance = new SecurityUtils(context);
        return instance;
    }

    public String generatePassword(String passCode, String memorableKey, String deviceId, String installationToken) {
        return deviceId + toHexString(memorableKey) + String.valueOf(installationToken.length()*3 + 11) + passCode + installationToken + String.valueOf(memorableKey.length()*17 + 23);
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br>
     * example)a?\u0061
     * @param str
     * @return
     */
    private String toHexString(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(toHexString(str.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br>
     * example)a?\u0061
     * @param ch
     * @return
     */
    private String toHexString(char ch) {
        String hex = Integer.toHexString((int) ch);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        return hex;
    }
}
