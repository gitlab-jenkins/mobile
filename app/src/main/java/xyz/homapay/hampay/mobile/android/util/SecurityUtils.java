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

    private SecurityUtils() {
    }

    public static SecurityUtils getInstance(Context context) {
        if( instance == null )
            instance = new SecurityUtils(context);
        return instance;
    }


    public static SecurityUtils getInstance() {
        if( instance == null )
            instance = new SecurityUtils();
        return instance;
    }


    //Old Password Generation - Api Level 2.0 and pre
    public String generatePassword(String passCode, String memorableKey, String deviceId, String installationToken) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String password = deviceId + toHexString(memorableKey) + String.valueOf(installationToken.length() * 3 + 11) + passCode + installationToken + String.valueOf(memorableKey.length()*17 + 23);
        return generateSHA_256_Password(password);
    }



    public String generateSHA_256_Password(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes("UTF-8"));
        byte[] digest = messageDigest.digest();
        return String.format("%0" + (digest.length * 2) + 'x', new BigInteger(1, digest));
    }

    public byte[] generateSHA_256(String macAddress, String IMEI, String androidId) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        String rawData = macAddress + IMEI + androidId;
        messageDigest.update(rawData.getBytes("UTF-8"));
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
