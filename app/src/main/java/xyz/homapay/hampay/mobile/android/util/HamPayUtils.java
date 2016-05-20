package xyz.homapay.hampay.mobile.android.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by amir on 5/20/16.
 */
public class HamPayUtils {

    public String removeDuplicatedChars(String string){
        char[] chars = string.toCharArray();
        Set<Character> charSet = new LinkedHashSet<Character>();
        for (char c : chars) {
            charSet.add(c);
        }
        StringBuilder sb = new StringBuilder();
        for (Character character : charSet) {
            sb.append(character);
        }
        return sb.toString();
    }

}
