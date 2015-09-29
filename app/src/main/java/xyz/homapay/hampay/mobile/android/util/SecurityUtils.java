package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
        return deviceId + toHexString(memorableKey) + String.valueOf(installationToken.length() * 3 + 11) + passCode + installationToken + String.valueOf(memorableKey.length()*17 + 23);
    }



    public byte[] generateSHA_256(String macAddress, String IMEI, String androidId) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String rawData = macAddress + IMEI + androidId;
        messageDigest.update(rawData.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = messageDigest.digest();

        return digest;

    }

    static String bin2hex(byte[] data) {

        return String.format("%064x", new java.math.BigInteger(1, data));

//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < data.length; i++) {
//            String hex = Integer.toHexString(0xFF & data[i]);
//            if (hex.length() == 1) {
//                sb.append('0');
//            }
//            sb.append(hex);
//        }
//        return sb.toString();

//        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
    }

    private String toHexString(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(toHexString(str.charAt(i)));
        }
        return sb.toString();
    }

    private String toHexString(char ch) {
        String hex = Integer.toHexString((int) ch);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        return hex;
    }
}
