package xyz.homapay.hampay.mobile.android.util.font;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

/**
 * Created by mohammad on 8/6/14.
 */
public class FontCorrector {

    /**
     * Call this method for some where that you can not set font face directly to one object such as actionbar title
     *
     * @param ctx      Context of application
     * @param input    String that you want to set with specific font
     * @param fontName Font name enum to select one font
     * @return
     */
    @NonNull
    public static SpannableString corrector(@NonNull Context ctx, String input, @NonNull FontNames fontName) {
        SpannableString s = new SpannableString(input);
        s.setSpan(new TypefaceSpan(ctx, fontName), 0, s.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    @NonNull
    public static SpannableString underliner(String input) {
        SpannableString s = new SpannableString(input);
        s.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

}
