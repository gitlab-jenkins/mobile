package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

/**
 * Created by mohammad on 1/7/17.
 */

public class AppManager {

    public static String getAuthToken(final Context ctx) {
        return AppSettings.getString(ctx, Constants.LOGIN_TOKEN_ID, "");
    }

}
