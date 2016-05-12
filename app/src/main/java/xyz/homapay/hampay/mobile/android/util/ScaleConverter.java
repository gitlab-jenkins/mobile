package xyz.homapay.hampay.mobile.android.util;

import android.content.res.Resources;

/**
 * Created by amir on 5/12/16.
 */
public class ScaleConverter {

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
