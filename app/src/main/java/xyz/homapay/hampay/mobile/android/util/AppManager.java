package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by mohammad on 1/7/17.
 */

public class AppManager {

    public static final String getAuthToken(final Context ctx) {
        return AppSettings.getString(ctx, Constants.LOGIN_TOKEN_ID, "");
    }

    public static final void setAuthToken(final Context ctx, String authToken) {
        AppSettings.setValue(ctx, Constants.LOGIN_TOKEN_ID, authToken);
    }

    public static final int getOperatorImageResource(Operator operator) {
        int resId;
        switch (operator) {
            case MCI:
                resId = R.mipmap.hamrah_active;
                break;
            case MTN:
                resId = R.mipmap.irancell_active;
                break;
            case RAYTEL:
                resId = R.mipmap.rightel_active;
                break;
            default:
                resId = R.mipmap.hamrah_active;
                break;
        }
        return resId;
    }

    public static final String amountFixer(long amount) {
        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();
        return persianEnglishDigit.E2P(currencyFormatter.format(amount));
    }

}
