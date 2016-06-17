package xyz.homapay.hampay.mobile.android.util;

/**
 * Created by amir on 5/20/16.
 */
public class HamPayUtils {

    public String splitStringEvery(String s, int interval) {
        String formattedString = "";
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        for (String string: result){
            formattedString += " " + string;
        }

        return formattedString;
    }

}
